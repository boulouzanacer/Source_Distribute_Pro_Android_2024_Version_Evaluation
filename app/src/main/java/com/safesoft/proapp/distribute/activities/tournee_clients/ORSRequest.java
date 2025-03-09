package com.safesoft.proapp.distribute.activities.tournee_clients;

import java.util.List;

public class ORSRequest {
    private List<List<Double>> coordinates;

    public ORSRequest(double lat1, double lon1, double lat2, double lon2) {
        this.coordinates = List.of(
                List.of(lon1, lat1), // OpenRouteService utilise "longitude,latitude"
                List.of(lon2, lat2)
        );
    }
}
