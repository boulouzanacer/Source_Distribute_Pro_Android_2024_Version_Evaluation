package com.safesoft.proapp.distribute.devicecheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Client Retrofit pour l'API Device Checker.
 *
 * Endpoint : POST /api/devices/check
 * Auth     : Header Authorization: Bearer <token>
 *
 * Permet a l'application Distribute Pro de s'enregistrer (1ere fois) ou
 * d'envoyer un heartbeat (deja enregistre) et de savoir si le device est
 * actif ou bloque.
 */
public class DeviceCheckClient {

    private static final String TAG = "DeviceCheckClient";

    private static final String PREFS = "ALL_PREFS";

    /** URL par defaut (emulateur Android pointe vers 10.0.2.2 = localhost du PC). */
    public static final String DEFAULT_API_URL = "https://provit-dz.com/api/";

    /** Token par defaut (doit correspondre a DEVICE_CHECKER_API_TOKEN du .env Laravel). */
    public static final String DEFAULT_API_TOKEN = "b00363d06497569921ee8ae3d3c94758bda128d89f704d978a54d68e681dc80e";

    public static final String KEY_DEVICE_CHECKER_URL = "DEVICE_CHECKER_URL";
    public static final String KEY_DEVICE_CHECKER_TOKEN = "DEVICE_CHECKER_TOKEN";

    /**
     * Hote concerne par la resolution DNS resiliente (voir {@link DeviceCheckDns}).
     */
    private static final String PINNED_HOST = "provit-dz.com";

    /**
     * Adresses Cloudflare canoniques de provit-dz.com (reponse officielle de la zone).
     *
     * Constat terrain : certains reseaux resolvent le domaine vers d'autres
     * adresses Cloudflare (ex: 188.114.96.x / 2a06:98c1::) totalement injoignables,
     * ce qui provoque des SocketTimeoutException. Ces adresses sont essayees en
     * priorite ; le DNS systeme reste utilise en secours.
     */
    private static final String[] PINNED_IPS = {
            "104.21.92.213",
            "172.67.198.216"
    };

    /** Delais de connexion/lecture (le defaut OkHttp de 10 s est trop court en mobile). */
    private static final int CONNECT_TIMEOUT_SECONDS = 20;
    private static final int READ_TIMEOUT_SECONDS = 30;
    private static final int WRITE_TIMEOUT_SECONDS = 20;

    /** Interface Retrofit de l'API Device Checker. */
    public interface DeviceCheckApi {

        @POST("devices/check")
        Call<DeviceCheckResponse> checkDevice(
                @Header("Authorization") String authHeader,
                @Body DeviceCheckRequest request);

        @POST("devices/disconnect")
        Call<Void> disconnectDevice(
                @Header("Authorization") String authHeader,
                @Body DeviceDisconnectRequest request);
    }

    /** Corps de la requete envoyee au Device Checker. */
    public static class DeviceCheckRequest {

        @SerializedName("device_id")
        public String deviceId;

        @SerializedName("revendeur")
        public String revendeur;

        @SerializedName("app_version")
        public String appVersion;

        @SerializedName("package_name")
        public String packageName;

        /** Nom de l'application (ex: "Distribute Pro", "Resto Pro"). */
        @SerializedName("app_name")
        public String appName;

        /**
         * Code d'activation de l'application.
         * Vaut 0 lorsque l'application n'est pas activee (mode evaluation).
         */
        @SerializedName("activation_code")
        public int activationCode;

        public DeviceCheckRequest(String deviceId, String revendeur,
                                  String appVersion, String packageName, String appName,
                                  int activationCode) {
            this.deviceId = deviceId;
            this.revendeur = revendeur;
            this.appVersion = appVersion;
            this.packageName = packageName;
            this.appName = appName;
            this.activationCode = activationCode;
        }
    }

    /** Reponse renvoyee par le Device Checker. */
    public static class DeviceCheckResponse {

        /** "active" ou "blocked". */
        @SerializedName("status")
        public String status;

        /** "registered" (1ere fois) ou "heartbeat" (deja connu). */
        @SerializedName("action")
        public String action;

        @SerializedName("message")
        public String message;

        public boolean isBlocked() {
            return "blocked".equalsIgnoreCase(status);
        }
    }

    /** Corps de la requete de deconnexion envoyee au Device Checker. */
    public static class DeviceDisconnectRequest {

        @SerializedName("device_id")
        public String deviceId;

        public DeviceDisconnectRequest(String deviceId) {
            this.deviceId = deviceId;
        }
    }

    private static volatile DeviceCheckApi instance;

    /** Retourne l'instance de l'API (singleton). */
    public static DeviceCheckApi getApi(Context context) {
        if (instance == null) {
            synchronized (DeviceCheckClient.class) {
                if (instance == null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(getApiUrl(context))
                            .client(buildHttpClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    instance = retrofit.create(DeviceCheckApi.class);
                    Log.i(TAG, "Retrofit initialise : baseUrl=" + retrofit.baseUrl()
                            + ", token=" + maskToken(getApiToken(context)));
                }
            }
        }
        return instance;
    }

    /** Construit le client HTTP avec des delais adaptes au mobile et un DNS resilient. */
    private static OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .dns(new DeviceCheckDns())
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * Resolution DNS resiliente : les adresses Cloudflare canoniques de l'API sont
     * essayees avant celles du DNS systeme, qui peuvent etre injoignables selon le
     * reseau. Les autres hotes utilisent la resolution systeme normale.
     */
    private static class DeviceCheckDns implements Dns {

        @Override
        public List<InetAddress> lookup(String hostname) throws UnknownHostException {
            List<InetAddress> addresses = new ArrayList<>();

            if (PINNED_HOST.equalsIgnoreCase(hostname)) {
                for (String ip : PINNED_IPS) {
                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        if (!addresses.contains(address)) {
                            addresses.add(address);
                        }
                    } catch (UnknownHostException ignored) {
                        // Adresse connue invalide : le DNS systeme prendra le relais.
                    }
                }
            }

            try {
                for (InetAddress address : Dns.SYSTEM.lookup(hostname)) {
                    if (!addresses.contains(address)) {
                        addresses.add(address);
                    }
                }
            } catch (UnknownHostException e) {
                if (addresses.isEmpty()) {
                    throw e;
                }
                Log.w(TAG, "Resolution systeme en echec pour " + hostname
                        + " : utilisation des adresses connues uniquement.");
            }

            Log.i(TAG, "DNS " + hostname + " -> " + addresses);
            return addresses;
        }
    }

    /**
     * Retourne l'URL de base de l'API.
     * Priorite : SharedPreferences > constante par defaut.
     */
    public static String getApiUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String url = prefs.getString(KEY_DEVICE_CHECKER_URL, null);
        if (url == null || url.trim().isEmpty()) {
            url = DEFAULT_API_URL;
        }
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }

    /**
     * Retourne le token d'authentification.
     * Priorite : SharedPreferences > constante par defaut.
     */
    public static String getApiToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_DEVICE_CHECKER_TOKEN, DEFAULT_API_TOKEN);
    }

    /** Masque un token pour le journal (n'expose que le debut et la fin). */
    private static String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return "****";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}
