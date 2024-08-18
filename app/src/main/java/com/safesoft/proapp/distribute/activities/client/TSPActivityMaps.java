package com.safesoft.proapp.distribute.activities.client;

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
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
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
    ArrayList<PostData_Client> tsp_clients;
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
        setContentView(binding.getRoot());

        controller = new DATABASE(this);
        temp_clients = new ArrayList<>();
        tsp_clients = new ArrayList<>();
        String querry = "SELECT * FROM ROUTING ORDER BY RECORDID";
        // querry = "SELECT * FROM Events";
        tsp_clients = controller.select_routing_from_database(querry);

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


        LatLng origine = new LatLng(36.734841, 3.175281);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origine, 9));
        mMap.setMyLocationEnabled(true);
        if (getPathValue()) {
            showPathOnMap();
        }

    }


    private void getTSP() {

        ConnectPyTask task = new ConnectPyTask();
        ConnectPyTask.context = getApplicationContext();

        JSONArray client = null;

        if (tsp_clients.size() > 0) {
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
                    for (int i = 0; i < tsp_clients.size(); i++) {
                        if (!tsp_clients.get(i).code_client.equals("000_ORIGIN_000")) {
                            JSONObject obj_detail1 = new JSONObject();
                            obj_detail1.put("CODE_CLIENT", tsp_clients.get(i).code_client);
                            obj_detail1.put("LATITUDE", tsp_clients.get(i).latitude);
                            obj_detail1.put("LONGITUDE", tsp_clients.get(i).longitude);
                            client.put(obj_detail1);
                            temp_clients.add(tsp_clients.get(i));
                        }
                    }
                } else {

                    //Creating list with position
                    for (int i = 0; i < tsp_clients.size(); i++) {
                        JSONObject obj_detail1 = new JSONObject();
                        obj_detail1.put("CODE_CLIENT", tsp_clients.get(i).code_client);
                        obj_detail1.put("LATITUDE", tsp_clients.get(i).latitude);
                        obj_detail1.put("LONGITUDE", tsp_clients.get(i).longitude);
                        client.put(obj_detail1);
                        temp_clients.add(tsp_clients.get(i));
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
            Log.e("ERROR", Objects.requireNonNull(ex.getLocalizedMessage()));
        }

        //Draw the polyline
        if (path.size() > 1) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        return path;
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
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
        mMap.addMarker(new MarkerOptions().position(latLng).title("Démmarage")
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.baseline_location_on_24)));

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

                    tsp_clients.clear();

                    for (int i = 0; i < myList.size(); i++) {
                        tsp_clients.add(temp_clients.get(Integer.parseInt(myList.get(i))));
                    }

                    controller.ExecuteTransactionInsertIntoRouting(tsp_clients);
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

        for (int i = 0; i < tsp_clients.size(); i++) {
            LatLng ddd = new LatLng(tsp_clients.get(i).latitude, tsp_clients.get(i).longitude);

            if (i == 0) {
                mMap.addMarker(new MarkerOptions().position(ddd).title("Démmarage").icon(BitmapFromVector(getApplicationContext(), R.drawable.baseline_location_on_24)));
            } else {
                mMap.addMarker(new MarkerOptions().position(ddd).title(tsp_clients.get(i).client));
            }
            if (i != tsp_clients.size() - 1) {
                traceRouteBetween2Point(contextApi, tsp_clients.get(i).latitude + "," + tsp_clients.get(i).longitude, tsp_clients.get(i + 1).latitude + "," + tsp_clients.get(i + 1).longitude);
            }
        }
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
