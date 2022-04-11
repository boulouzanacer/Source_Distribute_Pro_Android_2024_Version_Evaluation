package com.safesoft.proapp.distribute.activities;

import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.safesoft.proapp.distribute.postData.PostData_Position;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityMaps extends AppCompatActivity implements LocationListener {

    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String address;
    private MediaPlayer mp;
    private DATABASE controller;
    private ArrayList<PostData_Position> positions;
    private PostData_Position positionadd;
    ConnectionDetector ConnectionDetector;

    LocationManager locationManager;
    // Millisecond
    final long MIN_TIME_BW_UPDATES = 1000;
    // Met
    final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    Location myLocation = null;
    String locationProvider;
    private static final String MYTAG = "MYTAG";
    LatLng latlongi;
    // Request Code to ask the user for permission to view their current location (***).
    // Value 8bit (value <256)
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Create Progress Bar.
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Display Progress Bar.
        myProgress.show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List PV visité");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));

        ConnectionDetector = new ConnectionDetector(
                getApplicationContext());

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        geocoder = new Geocoder(this, Locale.getDefault());
        controller = new DATABASE(this);
        // Set callback listener, on Google Map ready.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public ArrayList<PostData_Position> getItems() {
        positions = new ArrayList<>();
        positions.clear();

        String querry = "SELECT " +
                "Position.LAT, " +
                "Position.LONGI, " +
                "Position.COLOR, " +
                "Position.CLIENT, " +
                "Position.NUM_BON, " +


                "Position.ADRESS " +

                "FROM Position";

        // querry = "SELECT * FROM Events";
        positions = controller.select_position_from_database(querry);

        return positions;
    }

    private void onMyMapReady(GoogleMap googleMap) {
        // Get Google Map from Fragment.
        myMap = googleMap;
        // Sét OnMapLoadedCallback Listener.
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                checkLocationPermission();
                // Map loaded. Dismiss this dialog, removing it from the screen.
                myProgress.dismiss();

            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.setMyLocationEnabled(true);
        this.showMyLocation();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
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
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        myMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    // Find Location provider is openning.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Criteria to find location provider.
        Criteria criteria = new Criteria();

        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    // Call this method only when you have the permissions to view a user's location.
    private void showMyLocation() {
        if(ConnectionDetector.isInternetAvailble())
        {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationProvider = this.getEnabledLocationProvider();

            if (locationProvider == null) {
                return;
            }


            try {
                // This code need permissions (Asked above ***)
                locationManager.requestLocationUpdates(
                        locationProvider,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                // Getting Location.
                // Lấy ra vị trí.
                myLocation = locationManager
                        .getLastKnownLocation(locationProvider);
            }
            // With Android API >= 23, need to catch SecurityException.
            catch (SecurityException e) {
                Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
                e.printStackTrace();
                return;
            }

            if (myLocation != null) {


                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                try {
                    addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                    address = addresses.get(0).getAddressLine(0);

                } catch (Exception e) {

                }
                BitmapDescriptor iconr = BitmapDescriptorFactory.fromResource(R.mipmap.rouge);
                BitmapDescriptor iconv = BitmapDescriptorFactory.fromResource(R.mipmap.vert);


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if(getItems().size() <= 0){

                }else {
                    for(int i=0;i< getItems().size();i++)
                    {
                        latlongi = new LatLng(getItems().get(i).lat, getItems().get(i).longi);
                        MarkerOptions option = new MarkerOptions();
                        option.position(latlongi);
                        option.title("NUM_BON="+getItems().get(i).num_bon+"/Client="+getItems().get(i).client);
                        option.snippet("Adresse="+getItems().get(i).adresse);
                        if(getItems().get(i).color == 1)
                        {
                            option.icon(iconv);
                        }else
                        {
                            option.icon(iconr);
                        }

                        Marker currentMarker = myMap.addMarker(option);
                        currentMarker.showInfoWindow();
                    }
                }


                // Add Marker to Map
                //  MarkerOptions option = new MarkerOptions();
                ///   option.title(address);
                //  option.snippet("....");
                //   option.position(latLng);
                //   Marker currentMarker = myMap.addMarker(option);
                //   currentMarker.showInfoWindow();
            } else {
                Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
                Log.i(MYTAG, "Location not found");
            }
        }else
        {
            Crouton.makeText(ActivityMaps.this, "Pas de connexion", Style.ALERT).show();

        }




    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);

        } catch (Exception e) {

        }


        // Showing the current location in Google Map
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .bearing(location.getBearing())
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        myMap.animateCamera(camUpd3);

        // Add Marker to Map
        // MarkerOptions option = new MarkerOptions();
        //option.title("My Location");
        //option.snippet("....");
        //option.position(pos);
        //Marker currentMarker = myMap.addMarker(option);
        //currentMarker.showInfoWindow();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }


    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
    }

    public void Sound(int SourceSound) {
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);


//////////////////////////////////////////////////////////////////////
///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
//////////////////////////////////////////////////////////////////////


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                // do whatever
                return true;
            case R.id.rouge:
                positionadd = new PostData_Position();

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                locationProvider = this.getEnabledLocationProvider();

                if (locationProvider == null) {
                    return false;
                }


                try {
                    // This code need permissions (Asked above ***)
                    locationManager.requestLocationUpdates(
                            locationProvider,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    // Getting Location.
                    // Lấy ra vị trí.
                    myLocation = locationManager
                            .getLastKnownLocation(locationProvider);
                }
                // With Android API >= 23, need to catch SecurityException.
                catch (SecurityException e) {
                    Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
                    e.printStackTrace();
                    return true;
                }

                if (myLocation != null) {

                    LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    try {
                        addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                        address = addresses.get(0).getAddressLine(0);

                    } catch (Exception e) {

                    }

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)             // Sets the center of the map to location user
                            .zoom(15)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.rouge);


                    // Add Marker to Map
                    MarkerOptions option = new MarkerOptions();
                    option.title(address);
                    option.position(latLng);
                    option.icon(icon);
                    Marker currentMarker = myMap.addMarker(option);
                    currentMarker.showInfoWindow();
                    positionadd.adresse = address;
                    positionadd.longi = myLocation.getLongitude();
                    positionadd.lat = myLocation.getLatitude();
                    positionadd.color=0;
                    positionadd.num_bon="";
                    positionadd.client="";
                    controller.Insert_into_position(positionadd);

                } else {
                    Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
                    Log.i(MYTAG, "Location not found");
                }

                // do whatever
                Toast.makeText(this, "rouge", Toast.LENGTH_LONG).show();
                return true;
              /*  positionadd = new PostData_Position();


                if (locationProvider == null) {
                    return false;
                }

                // Millisecond
                // Met

                try {
                    // This code need permissions (Asked above ***)
                    locationManager.requestLocationUpdates(
                            locationProvider,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    // Getting Location.
                    // Lấy ra vị trí.
                    myLocation = locationManager
                            .getLastKnownLocation(locationProvider);
                }
                // With Android API >= 23, need to catch SecurityException.
                catch (SecurityException e) {
                    Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
                    e.printStackTrace();
                    return true;
                }

                if (myLocation != null) {

                    LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    try {
                        addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                        address = addresses.get(0).getAddressLine(0);

                    } catch (Exception e) {

                    }

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)             // Sets the center of the map to location user
                            .zoom(15)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.vert);


                    // Add Marker to Map
                    MarkerOptions option = new MarkerOptions();
                    option.title(address);
                    option.snippet("....");
                    option.position(latLng);
                    option.icon(icon);
                    Marker currentMarker = myMap.addMarker(option);
                    currentMarker.showInfoWindow();
                    positionadd.adresse = address;
                    positionadd.longi = myLocation.getLongitude();
                    positionadd.lat = myLocation.getLatitude();
                    positionadd.color=1;
                    controller.Insert_into_position(positionadd);
                    return true;
                }*/
        }
        return  true;
    }
}
