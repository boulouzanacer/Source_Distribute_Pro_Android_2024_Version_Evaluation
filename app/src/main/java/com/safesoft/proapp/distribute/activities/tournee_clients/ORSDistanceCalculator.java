package com.safesoft.proapp.distribute.activities.tournee_clients;

import java.text.DecimalFormat;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ORSDistanceCalculator {
    private static final String BASE_URL = "https://api.openrouteservice.org/";
    private static final String API_KEY = "5b3ce3597851110001cf62480e74f9934c094cdba0b0e304f24f081d";  // Remplace par ta clé API gratuite

    private final OpenRouteServiceAPI api;

    public ORSDistanceCalculator() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(OpenRouteServiceAPI.class);
    }

    public void getDistance(double lat1, double lon1, double lat2, double lon2, DistanceCallback callback) {
        ORSRequest request = new ORSRequest(lat1, lon1, lat2, lon2);

        Call<ORSResponse> call = api.getDistance(API_KEY, request);
        call.enqueue(new Callback<ORSResponse>() {
            @Override
            public void onResponse(Call<ORSResponse> call, Response<ORSResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        !response.body().routes.isEmpty()) {

                    double distanceMeters = response.body().routes.get(0).summary.distance;
                    DecimalFormat df = new DecimalFormat("#.###");
                    distanceMeters = Double.parseDouble(df.format(distanceMeters / 1000));
                    callback.onSuccess(distanceMeters + " km", (int) distanceMeters);
                } else {
                    callback.onFailure("Réponse invalide");
                }
            }

            @Override
            public void onFailure(Call<ORSResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface DistanceCallback {
        void onSuccess(String distanceText, int distanceValue);
        void onFailure(String errorMessage);
    }
}
