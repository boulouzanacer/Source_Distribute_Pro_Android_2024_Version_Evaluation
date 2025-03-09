package com.safesoft.proapp.distribute.activities.tournee_clients;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ORSResponse {
    @SerializedName("routes")
    public List<Route> routes;

    public static class Route {
        @SerializedName("summary")
        public Summary summary;
    }

    public static class Summary {
        @SerializedName("distance")
        public double distance;
    }
}
