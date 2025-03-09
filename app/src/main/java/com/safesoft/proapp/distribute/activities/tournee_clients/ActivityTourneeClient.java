package com.safesoft.proapp.distribute.activities.tournee_clients;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.PassageEvent;
import com.safesoft.proapp.distribute.eventsClasses.ProductEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditLocation;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditProduct;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Tournee1;
import com.safesoft.proapp.distribute.postData.PostData_Tournee2;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class ActivityTourneeClient extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private String scannedClientCode;
    private final String PREFS = "ALL_PREFS";
    String TYPE_ACTIVITY = "";
    String SOURCE_EXPORT = "";
    private SharedPreferences prefs;
    private DATABASE controller;
    private String NUM_TOURNEE;
    private PostData_Tournee1 tournee1;
    private ArrayList<PostData_Tournee2> passage_list;
    String date_sub_title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournee_client);

        controller = new DATABASE(this);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Passage clients");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        initViews();
        tournee1 = new PostData_Tournee1();

        //get num tournee
        if (getIntent() != null) {
            TYPE_ACTIVITY = getIntent().getStringExtra("TYPE_ACTIVITY");
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        } else {
            Crouton.makeText(ActivityTourneeClient.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        if (TYPE_ACTIVITY.equals("NEW_TOURNEE")) {
            //get num bon tournee
            String selectQuery = "SELECT MAX(NUM_TOURNEE) AS max_id FROM TOURNEE1 WHERE NUM_TOURNEE IS NOT NULL";
            NUM_TOURNEE = controller.select_max_num_bon(selectQuery);

            // get date and time
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate_Show = date_format.format(c.getTime());

            date_sub_title = formattedDate_Show;

            tournee1.date_tournee = formattedDate_Show;
            tournee1.num_tournee = NUM_TOURNEE;
            tournee1.name_tournee = "TOURNEE_" + NUM_TOURNEE;
            // tournee1.code_depot = CODE_DEPOT;

            controller.insert_into_Tournee1(tournee1);


        } else if (TYPE_ACTIVITY.equals("EDIT_TOURNEE")) {
            //get num bon
            if (getIntent() != null) {
                NUM_TOURNEE = getIntent().getStringExtra("NUM_TOURNEE");
            }

            String querry = "SELECT " +
                    "TOURNEE1.RECORDID, " +
                    "TOURNEE1.DATE_TOURNEE, " +
                    "TOURNEE1.NUM_TOURNEE, " +
                    "TOURNEE1.NAME_TOURNEE, " +
                    "TOURNEE1.NBR_CLIENT, " +
                    "TOURNEE1.EXPORTATION, " +
                    "TOURNEE1.IS_EXPORTED " +
                    //"TOURNEE1.CODE_DEPOT, " +
                   // "INV1.BLOCAGE " +
                    "FROM TOURNEE1 " +
                    "WHERE TOURNEE1.NUM_TOURNEE ='" + NUM_TOURNEE + "'";

            ///////////////////////////////////
            tournee1 = controller.select_tournee1_from_database(querry);

            /*passage_list = controller.select_inventaire2_from_database("SELECT " +
                    "INV2.RECORDID, " +
                    "INV2.CODE_BARRE, " +
                    "INV2.NUM_INV, " +
                    "INV2.PRODUIT, " +
                    "INV2.NBRE_COLIS, " +
                    "INV2.COLISSAGE, " +
                    "INV2.PA_HT, " +
                    "INV2.QTE, " +
                    "INV2.QTE_TMP, " +
                    "INV2.QTE_NEW, " +
                    "INV2.TVA, " +
                    "INV2.VRAC, " +
                    "INV2.CODE_DEPOT " +
                    "FROM INV2 " +
                    "WHERE INV2.NUM_INV = '" + NUM_TOURNEE + "'");*/
            //private String formattedDate;
            date_sub_title = tournee1.date_tournee;

        } else {
            Crouton.makeText(ActivityTourneeClient.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        getSupportActionBar().setTitle(tournee1.name_tournee);
        getSupportActionBar().setSubtitle(date_sub_title + " : " + NUM_TOURNEE);
        //validate_theme();
    }

    private void initViews() {
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Register as a subscriber
        //bus.register(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //scanButton.setOnClickListener(v -> initiateQRScan());
        checkLocationEnabled();
        requestLocationPermission();
    }

    private void startScanProduct() {

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ActivityTourneeClient.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        fetchCurrentLocation(barcode.rawValue);
                    }
                }).build();
        materialBarcodeScanner.startScan();
    }

    private void checkLocationEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            Toast.makeText(this, "Please enable location services.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Location is already enabled.", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
                    }
                });
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void fetchCurrentLocation(String scannedClientCode) {
        this.scannedClientCode = scannedClientCode;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Search for client in database
        PostData_Client client = null;
        client = controller.select_client_from_database(scannedClientCode);
        if (client == null) {
            //Toast.makeText(this, "Client not found.", Toast.LENGTH_SHORT).show();
            Crouton.makeText(ActivityTourneeClient.this, "Client introuvable !", Style.ALERT).show();
            return;
        }


        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            //insert into tournee2 with status passed
            //insert into tournee2 with status not passed
            saveClientVisit(scannedClientCode, location);
        });


    }

    private void saveClientVisit(String clientCode, Location location) {
        PostData_Tournee2 tournee2 = new PostData_Tournee2();
        tournee2.num_tournee = tournee1.num_tournee;
        tournee2.code_client = clientCode;

        if (location != null) {
            FragmentNewEditLocation fragmentnewpassage = new FragmentNewEditLocation();
            fragmentnewpassage.showDialogbox(ActivityTourneeClient.this, "NEW_PASSAGE", tournee2, location.getLatitude(),location.getLongitude());
            // Simulating saving to a database
            String message = "Client Code: " + clientCode + "\nLatitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude();
            Toast.makeText(this, "Visit Saved:\n" + message, Toast.LENGTH_LONG).show();

            Log.e("POSITIONS : ", clientCode);
            Log.e("POSITIONS : ", String.valueOf(location.getLatitude()));
            Log.e("POSITIONS : ", String.valueOf(location.getLongitude()));

        }else{
            FragmentNewEditLocation fragmentnewpassage = new FragmentNewEditLocation();
            fragmentnewpassage.showDialogbox(ActivityTourneeClient.this, "NEW_PASSAGE", tournee2,0,0);
            Toast.makeText(ActivityTourneeClient.this, "Unable to retrieve location.", Toast.LENGTH_SHORT).show();
        }


        // TODO: Replace with database or server API call to save the data.
    }

    @Subscribe
    public void onPassageAdded(PassageEvent passageEvent) {
        //setRecycle("", false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tournee_client, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.new_scanner) {
            //startScanProduct();
            fetchCurrentLocation("62211154");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound(R.raw.back);
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int SourceSound) {
        MediaPlayer mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }
}