package com.safesoft.proapp.distribute.activities.client;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.utils.ConnectionDetector;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.databinding.ActivityTspmapsBinding;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class TSPActivityMaps extends FragmentActivity implements OnMapReadyCallback {

    ActivityTspmapsBinding binding;
    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String MYTAG = "MYTAG";
    private GoogleMap mMap;
    ArrayList<PostData_Client> temp_clients;
    ArrayList<PostData_Client> visite_clients;
    DATABASE controller;
    static final String SERVER_IP = "105.96.9.62"; // The SERVER_IP must be the same in server and client
    static final int PORT = 7575; // You can put any arbitrary PORT value
    Dialog dialog;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ConnectionDetector ConnectionDetector;
    GeoApiContext contextApi;

    private double currentLatitude;
    private double currentLongitude;

    private final String PREFS = "ALL_PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTspmapsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }*/

        controller = new DATABASE(this);
        temp_clients = new ArrayList<>();
        visite_clients = new ArrayList<>();
        String querry = "SELECT DISTINCT " +
                "CLIENT.CLIENT, " +
                "CLIENT.CODE_CLIENT, " +
                "CLIENT.LATITUDE, " +
                "CLIENT.LONGITUDE,  " +
                "BON1_TEMP.BLOCAGE " +
                "FROM CLIENT " +
                "JOIN BON1_TEMP ON CLIENT.CODE_CLIENT = BON1_TEMP.CODE_CLIENT " +
                "WHERE CLIENT.LATITUDE <> 0 AND CLIENT.LONGITUDE <> 0 " +
                "ORDER BY strftime('%Y-%m-%d', BON1_TEMP.DATE_BON) ASC, strftime('%H:%M:%S', BON1_TEMP.HEURE) ASC ";

        visite_clients = controller.select_visite_client_from_database(querry);

        dialog = new Dialog(TSPActivityMaps.this);
        dialog.setContentView(R.layout.progress_dialog_get_path);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


        ConnectionDetector = new ConnectionDetector(getApplicationContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_best_path) {
            if (mMap != null)
                mMap.clear();

            getTSP();

        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        dialog.setContentView(R.layout.progress_dialog_get_position);
        dialog.show();

        //Execute Directions API request
        contextApi = new GeoApiContext.Builder().apiKey(getResources().getString(R.string.google_maps_key)).build();

        checkLocationPermission();
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {

                binding.btnBestPath.setVisibility(View.VISIBLE);

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                showMyLocation(location);

                dialog.dismiss();
            }
        });

        showPathOnMap();
        //mMap.setMyLocationEnabled(true);

    }


    private void getTSP() {

        ConnectPyTask task = new ConnectPyTask();
        ConnectPyTask.context = getApplicationContext();

        JSONArray client = null;

        if (!visite_clients.isEmpty()) {
            try {


                client = new JSONArray();

                if (!getPathValue()) {

                    PostData_Client ccc = new PostData_Client();
                    ccc.client = "Point de démmarage";
                    ccc.code_client = "000_ORIGIN_000";
                    ccc.latitude = currentLatitude;
                    ccc.longitude = currentLongitude;
                    temp_clients.add(ccc);

                    //Add current position as starting point
                    JSONObject obj_detail = new JSONObject();
                    obj_detail.put("CODE_CLIENT", "000_ORIGIN_000");
                    obj_detail.put("LATITUDE", temp_clients.get(0).latitude);
                    obj_detail.put("LONGITUDE", temp_clients.get(0).longitude);
                    client.put(obj_detail);

                    //Creating list with position
                    for (int i = 0; i < visite_clients.size(); i++) {
                        if (!visite_clients.get(i).code_client.equals("000_ORIGIN_000")) {
                            JSONObject obj_detail1 = new JSONObject();
                            obj_detail1.put("CODE_CLIENT", visite_clients.get(i).code_client);
                            obj_detail1.put("LATITUDE", visite_clients.get(i).latitude);
                            obj_detail1.put("LONGITUDE", visite_clients.get(i).longitude);
                            client.put(obj_detail1);
                            temp_clients.add(visite_clients.get(i));
                        }
                    }
                } else {

                    //Creating list with position
                    for (int i = 0; i < visite_clients.size(); i++) {
                        JSONObject obj_detail1 = new JSONObject();
                        obj_detail1.put("CODE_CLIENT", visite_clients.get(i).code_client);
                        obj_detail1.put("LATITUDE", visite_clients.get(i).latitude);
                        obj_detail1.put("LONGITUDE", visite_clients.get(i).longitude);
                        client.put(obj_detail1);
                        temp_clients.add(visite_clients.get(i));
                    }
                }


            } catch (Exception e) {
                Log.v("Creating list client for server : ", e.getMessage());
            }

            assert client != null;
            task.execute(client.toString());
            dialog.setContentView(R.layout.progress_dialog_get_path);
            dialog.show();
        } else {
            Crouton.makeText(TSPActivityMaps.this, "Routing list est vide", Style.INFO).show();
        }

    }

    private List<LatLng> traceRouteBetween2Point(GeoApiContext contextApi, String startPosition, String EndPosition) {

        //https://www.akexorcist.com/2015/12/google-direction-library-for-android-en.html

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList<>();
        DirectionsApiRequest req = DirectionsApi.getDirections(contextApi, startPosition, EndPosition).mode(TravelMode.DRIVING);
        try {

            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("ERROR", Objects.requireNonNull(ex.getMessage()));
        }

        //Draw the polyline
        if (path.size() > 1) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        return path;
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    // Find Location provider is openning.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Criteria to find location provider.
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);

        String bestProvider = locationManager.getBestProvider(criteria, true);

        assert bestProvider != null;
        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }


    // Call this method only when you have the permissions to view a user's location.
    private void showMyLocation(Location myLocation) {

        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(9)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
       // mMap.addMarker(new MarkerOptions().position(latLng).title("Démmarage").icon(BitmapFromVector(getApplicationContext(), R.drawable.baseline_location_red_24)));

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    class ConnectPyTask extends AsyncTask<String, Void, String> {

        static Context context = null;
        private Marker customMarker;

        @Override
        protected String doInBackground(String... data) {
            try {

                StringBuilder result = new StringBuilder();
                Socket socket = new Socket(SERVER_IP, PORT); //Server IP and PORT
                Scanner sc = new Scanner(socket.getInputStream());
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                printWriter.write(data[0]); // Send Data
                //printWriter.write("Disconnect"); // Send Data
                printWriter.flush();
                while (sc.hasNext()) {
                    result.append(sc.next());
                }
                return result.toString();

            } catch (IOException e) {
                Log.d("Exception", e.toString());
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {

            if (s != null) {
                if (s.startsWith("[") && s.endsWith("]")) {
                    mMap.clear();
                    String replace = s.replace("[", "");
                    String replace1 = replace.replace("]", "");
                    List<String> myList = new ArrayList<>(Arrays.asList(replace1.split(",")));

                    visite_clients.clear();

                    for (int i = 0; i < myList.size(); i++) {
                        visite_clients.add(temp_clients.get(Integer.parseInt(myList.get(i))));
                    }

                    controller.ExecuteTransactionInsertIntoRouting(visite_clients);
                    showPathOnMap();

                    setPathValue();

                } else {
                    Crouton.makeText(TSPActivityMaps.this, "La réponse depuis le serveur est mal formater !", Style.ALERT).show();
                }
            } else {
                Crouton.makeText(TSPActivityMaps.this, "Aucune connexion avec le serveur !", Style.ALERT).show();
            }

            if (dialog != null)
                dialog.dismiss();
        }

        private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }

    }


    private void showPathOnMap() {

        PostData_Client last_client_visited = new PostData_Client();
        PostData_Client next_client_to_visite = new PostData_Client();
        for (int i = 0; i < visite_clients.size(); i++) {

            LatLng ddd = new LatLng(visite_clients.get(i).latitude, visite_clients.get(i).longitude);
            if(visite_clients.get(i).blocage.equals("T")){
                mMap.addMarker(new MarkerOptions().position(ddd).title(visite_clients.get(i).client).icon(BitmapFromVector(getApplicationContext(), R.drawable.baseline_location_green_24)));
                last_client_visited = visite_clients.get(i);
                next_client_to_visite = visite_clients.get(i + 1);
            }else{
                mMap.addMarker(new MarkerOptions().position(ddd).title(visite_clients.get(i).client).icon(BitmapFromVector(getApplicationContext(), R.drawable.baseline_location_red_24)));
            }

            /*if (i != visite_clients.size() - 1) {
                traceRouteBetween2Point(contextApi, visite_clients.get(i).latitude + "," + visite_clients.get(i).longitude, visite_clients.get(i + 1).latitude + "," + visite_clients.get(i + 1).longitude);
            }*/
        }
        traceRouteBetween2Point(contextApi, last_client_visited.latitude + "," + last_client_visited.longitude, next_client_to_visite.latitude + "," + next_client_to_visite.longitude);

    }

    private void setPathValue() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putBoolean("HAS_PATH", true);
        editor.apply();
    }

    private boolean getPathValue() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        return prefs.getBoolean("HAS_PATH", false);
    }
}
