package com.safesoft.proapp.distribute.activities.tournee_clients;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OpenRouteServiceAPI {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("v2/directions/driving-car")
    Call<ORSResponse> getDistance(@Query("api_key") String apiKey, @Body ORSRequest body);
}