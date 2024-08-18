package com.safesoft.proapp.distribute.activities.map;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
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
import com.safesoft.proapp.distribute.adapters.ListViewAdapterClientMaps;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.utils.ConnectionDetector;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityMaps extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String address;
    private MediaPlayer mp;
    private DATABASE controller;
    private ArrayList<PostData_Client> clients;
    com.safesoft.proapp.distribute.utils.ConnectionDetector ConnectionDetector;

    LocationManager locationManager;
    // Millisecond
    final long MIN_TIME_BW_UPDATES = 1000;
    // Met
    final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    Location myLocation = null;
    String locationProvider;
    private static final String MYTAG = "MYTAG";
    LatLng latlongi;
    private Toolbar mToolbar;
    private ListViewAdapterClientMaps mAdapter;
    private Menu menu;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        controller = new DATABASE(this);
        getItems();


        // Create Progress Bar.
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map chargement ...");
        myProgress.setMessage("Attendez svp...");
        myProgress.setCancelable(false);

        // Display Progress Bar.
        myProgress.show();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List clients");

        ConnectionDetector = new ConnectionDetector(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());

    }

    public ArrayList<PostData_Client> getItems() {
        clients = new ArrayList<>();
        String querry = "SELECT * FROM Client WHERE LATITUDE <> 0 AND LONGITUDE <> 0";
        clients = controller.select_clients_from_database(querry);
        return clients;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
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
        showMyLocation();
        showClientsOnMap();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
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
    private void showMyLocation() {
        if (ConnectionDetector.isInternetAvailble()) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationProvider = this.getEnabledLocationProvider();

            if (locationProvider == null) {
                return;
            }

            try {
                locationManager.requestLocationUpdates(locationProvider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                myLocation = locationManager.getLastKnownLocation(locationProvider);
            } catch (SecurityException e) {
                Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
                e.printStackTrace();
                return;
            }

            if (myLocation != null) {

                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                try {
                    addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                    address = addresses.get(0).getAddressLine(0);
                } catch (Exception e) {
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(12)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } else {
                Log.i(MYTAG, "Position non disponible!");
            }
        } else {
            Crouton.makeText(ActivityMaps.this, "Pas de connexion internet", Style.ALERT).show();
        }
    }

    private void showClientsOnMap() {

        BitmapDescriptor iconr = BitmapDescriptorFactory.fromResource(R.mipmap.rouge);

        if (clients.size() > 0) {
            for (int i = 0; i < clients.size(); i++) {
                latlongi = new LatLng(clients.get(i).latitude, clients.get(i).longitude);
                MarkerOptions option = new MarkerOptions();
                option.position(latlongi);
                option.title("Client : " + clients.get(i).client);
                option.snippet("Code client : " + clients.get(i).code_client);
                option.icon(iconr);
                Marker currentMarker = myMap.addMarker(option);
                assert currentMarker != null;
                //currentMarker.showInfoWindow();
            }
        }

    }


    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);

        } catch (Exception e) {

        }

        // Showing the current location in Google Map
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(12)
                .bearing(location.getBearing())
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        myMap.animateCamera(camUpd3);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int SourceSound) {
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_maps, menu);
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView mSearchView;
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search_item));
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        mSearchView.setSuggestionsAdapter(mAdapter);

        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                @SuppressLint("Range")
                String client_name = cursor.getString(cursor.getColumnIndex("clientName"));
                PostData_Client client = new PostData_Client();
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).client.equals(client_name)) {
                        client = clients.get(i);
                        break;
                    }
                }
                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(client.latitude);//your coords of course
                targetLocation.setLongitude(client.longitude);

                // Showing the current location in Google Map
                CameraPosition camPos = new CameraPosition.Builder()
                        .target(new LatLng(client.latitude, client.longitude))
                        .zoom(15)
                        .bearing(targetLocation.getBearing())
                        .build();
                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                myMap.animateCamera(camUpd3);

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                populateAdapter(newText);
                return true;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {
        ArrayList<PostData_Client> client_suggest = new ArrayList<>();
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "clientName"});
        for (int i = 0; i < clients.size(); i++) {
            if ((clients.get(i).client.toLowerCase().contains(query.toLowerCase())) || (clients.get(i).code_client.contains(query))) {
                c.addRow(new Object[]{i, clients.get(i).client});
                client_suggest.add(clients.get(i));
            }
        }

        //SearchView
        //SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView search = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mAdapter = new ListViewAdapterClientMaps(this, c, client_suggest);
        assert search != null;
        search.setSuggestionsAdapter(mAdapter);
        mAdapter.changeCursor(c);
        mAdapter.notifyDataSetChanged();
    }

}
