package com.safesoft.proapp.distribute.activities.fournisseur;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapter_Situation_fournisseur;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedFournisseurEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditFournisseur;
import com.safesoft.proapp.distribute.fragments.FragmentVersementFournisseur;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_f;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.printing.Printing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.nekocode.badge.BadgeDrawable;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import mehdi.sakout.fancybuttons.FancyButton;

public class ActivityFournisseurDetail extends AppCompatActivity implements RecyclerAdapter_Situation_fournisseur.ItemClick {

    private NumberFormat nf;

    private PostData_Fournisseur fournisseur;
    private TextView TvFournis, TvTel, TvLat, TvLog, TvAdresse, TvCodeFs, TvModeT, TvAchat, TvVerser, TvSolde;
    private ImageView imgv_fournisseur_map;
    private FancyButton Btn_itenerary, Btn_Call, Btn_update_position, BtnVerser, BtnAchat;

    private MediaPlayer mp;

    private final EventBus bus = EventBus.getDefault();

    private static final int ACCES_FINE_LOCATION = 2;
    private static final int BLUETOOTH_CONNECT = 3;
    private Boolean checkPermission = false;

    private Intent intent_location;

    private ProgressDialog progress;

    private DATABASE controller;

    private RecyclerView recyclerView;
    RecyclerAdapter_Situation_fournisseur adapter;
    ArrayList<PostData_Carnet_f> carnet_fs;
    private String CODE_FRS;
    String url_static_map;
    private PostData_Carnet_f carnet_f_print;

    PostData_Carnet_f selected_versement = null;
    private final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fournisseur_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Situation Fournisseur");
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        CODE_FRS = getIntent().getStringExtra("CODE_FRS");

        controller = new DATABASE(this);

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        initViews();

        getFournisseur();

        requestPermission();

        // Register as a subscriber
        bus.register(this);

        intent_location = new Intent(this, ServiceLocation.class);
        if (checkPermission) {
            //  startService(intent_location);
        }

        setStaticMap(fournisseur.latitude, fournisseur.longitude);
    }


    private void setStaticMap(double latitude, double longitude) {
        int zoom = 15;
        String label = "P";
        url_static_map = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=" + zoom + "&size=1200x300&markers=color:red%7Clabel:" + label + "%7C" + latitude + "," + longitude + "&key=AIzaSyAzMUqTnhsnrXuog5ZjSrnSYMPM-XShfRA";
        if (latitude != 0 && longitude != 0) {
            new DownloadImageTask(imgv_fournisseur_map).execute();
        }

    }

    protected void getFournisseur() {
        fournisseur = new PostData_Fournisseur();
        fournisseur = controller.select_fournisseur_from_database(CODE_FRS);
        iniData(fournisseur);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecycle();
    }

    protected void initViews() {

        //TextView
        TvFournis = findViewById(R.id.fournisseur_name);
        TvTel = findViewById(R.id.tel);
        // TvLat = (TextView) findViewById(R.id.lat);
        // TvLog = (TextView) findViewById(R.id.log);
        TvAdresse = findViewById(R.id.adresse);
        TvCodeFs = findViewById(R.id.code_frs);
        TvModeT = findViewById(R.id.mode_tarif);
        TvAchat = findViewById(R.id.achat);
        TvVerser = findViewById(R.id.verser);
        TvSolde = findViewById(R.id.solde);

        //Button
        // BtnCall = (ImageButton) findViewById(R.id.btnCall);
        BtnVerser = findViewById(R.id.btnVerser);
        BtnAchat = findViewById(R.id.btnAchat);
        Btn_itenerary = findViewById(R.id.btn_itenerary);
        Btn_update_position = findViewById(R.id.btn_update_pos);
        Btn_Call = findViewById(R.id.btnCall);

        Btn_itenerary.setIconResource(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_itenerary_24));
        Btn_update_position.setIconResource(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_update_location_24));
        Btn_Call.setIconResource(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_local_phone_white_24));

        //ImageView
        imgv_fournisseur_map = findViewById(R.id.imgv_fournisseur_map);
        //RecycleViews
        recyclerView = findViewById(R.id.recycler_view_fournisseur_detail);
    }

    private void setRecycle() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter_Situation_fournisseur(this, getItems());
        recyclerView.setAdapter(adapter);
    }

    public ArrayList<PostData_Carnet_f> getItems() {

        carnet_fs = new ArrayList<>();
        String querry = "SELECT CARNET_F.RECORDID, " +
                "CARNET_F.CODE_FRS, " +
                "CARNET_F.DATE_CARNET, " +

                "FOURNIS.FOURNIS, " +
                "FOURNIS.CODE_FRS, " +
                "FOURNIS.LATITUDE, " +
                "FOURNIS.LONGITUDE, " +
                "FOURNIS.TEL, " +
                "FOURNIS.ADRESSE, " +
                "FOURNIS.RC, " +
                "FOURNIS.IFISCAL, " +
                "FOURNIS.AI, " +
                "FOURNIS.NIS, " +

                "CARNET_F.HEURE, " +
                "CARNET_F.ACHATS, " +
                "CARNET_F.VERSEMENTS, " +
                "CARNET_F.SOURCE, " +
                "CARNET_F.NUM_BON, " +
                "CARNET_F.MODE_RG, " +
                "CARNET_F.REMARQUES, " +
                "CARNET_F.UTILISATEUR, " +
                "CARNET_F.EXPORTATION, " +
                "CARNET_F.IS_EXPORTED " +

                "FROM CARNET_F " +
                "LEFT JOIN FOURNIS ON " +
                "CARNET_F.CODE_FRS = FOURNIS.CODE_FRS " +
                "WHERE CARNET_F.CODE_FRS = '" + fournisseur.code_frs + "' ORDER BY CARNET_F.HEURE DESC";


        // querry = "SELECT * FROM Events";
        carnet_fs = controller.select_carnet_f_from_database(querry);

        return carnet_fs;
    }

    protected void iniData(PostData_Fournisseur fournisseur) {

        TvFournis.setText(fournisseur.fournis);
        TvTel.setText(fournisseur.tel);
        TvAdresse.setText(fournisseur.adresse);
        TvCodeFs.setText(fournisseur.code_frs);

        if (prefs.getBoolean("SHOW_ACHAT_CLIENT", false)) {
            TvAchat.setVisibility(View.VISIBLE);
        } else {
            TvAchat.setVisibility(View.GONE);
        }

        final BadgeDrawable drawable6 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(0xff303F9F)
                        .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(fournisseur.achat_montant)))
                        .text2(" DA ")
                        .build();
        SpannableString spannableString6 = new SpannableString(TextUtils.concat(drawable6.toSpannable()));
        TvAchat.setText(spannableString6);


        final BadgeDrawable drawable7 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(0xff303F9F)
                        .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(fournisseur.verser_montant)))
                        .text2(" DA ")
                        .build();
        SpannableString spannableString7 = new SpannableString(TextUtils.concat(drawable7.toSpannable()));
        TvVerser.setText(spannableString7);


        if (prefs.getBoolean("AFFICHAGE_SOLDE_CLIENT", true)) {
            final BadgeDrawable drawable8 =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                            .badgeColor(0xff303F9F)
                            .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(fournisseur.solde_montant)))
                            .text2(" DA ")
                            .build();
            SpannableString spannableString8 = new SpannableString(TextUtils.concat(drawable8.toSpannable()));
            TvSolde.setText(spannableString8);
        } else {
            TvSolde.setText("********");
        }


    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(Void... urls) {
            String urldisplay = url_static_map;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                mIcon11 = BitmapFactory.decodeResource(getResources(), R.mipmap.noimg);
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCall:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fournisseur.tel));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
                    return;
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.btnVerser:
                if(!prefs.getBoolean("APP_AUTONOME", false)){
                    FragmentVersementFournisseur fragmentversementfournisseur = new FragmentVersementFournisseur();
                    fragmentversementfournisseur.showDialogbox(ActivityFournisseurDetail.this, fournisseur.solde_montant, fournisseur.verser_montant, 0, "", fournisseur.code_frs, false, "");
                }else {
                    Crouton.makeText(ActivityFournisseurDetail.this, "Versement fournisseur disponible seulement en mode Mono-Poste !", Style.ALERT).show();
                }

                break;

            case R.id.btnVente:
                break;

            case R.id.btn_update_pos:

                startService(intent_location);

                progress = new ProgressDialog(ActivityFournisseurDetail.this);
                progress.setTitle("Position");
                progress.setMessage("Recherche position...");
                progress.setIndeterminate(true);
                progress.setCancelable(true);
                progress.show();

                break;
            case R.id.btn_itenerary:
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + fournisseur.latitude + "," + fournisseur.longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details_client, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.print_versement) {
            if (selected_versement != null) {
                Print_versement(selected_versement);
            } else {
                Crouton.makeText(ActivityFournisseurDetail.this, "Vous devez séléctionner une versement au-dessous !", Style.ALERT).show();
            }

        } else if (item.getItemId() == R.id.edit_client) {
            FragmentNewEditFournisseur fragment_new_edit_fournisseur = new FragmentNewEditFournisseur();
            fragment_new_edit_fournisseur.showDialogbox(ActivityFournisseurDetail.this, getBaseContext(), "EDIT_FOURNISSEUR", fournisseur);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void Sound() {
        mp = MediaPlayer.create(this, R.raw.back);
        mp.start();
    }


    public void requestPermission() {
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, RECIEVE_SMS);
        }
*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT);
                checkPermission = false;
            } else {
                checkPermission = true;
            }
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
            checkPermission = false;
        } else {
            checkPermission = true;
        }
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
        }
        */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCES_FINE_LOCATION) {
            startService(new Intent(this, ServiceLocation.class));
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Subscribe
    public void onEvent(LocationEvent event) {
        Log.e("TRACKKK", "Recieved location : " + event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());
        fournisseur.latitude = event.getLocationData().getLatitude();
        fournisseur.longitude = event.getLocationData().getLongitude();

        //controller.update_position_client(client.latitude, client.longitude, client.code_client);
        if (progress != null) {
            progress.dismiss();
        }

        setStaticMap(event.getLocationData().getLatitude(), event.getLocationData().getLongitude());

    }


    public void Update_fournisseur_details() {
        getFournisseur();
        setRecycle();
    }


    @Subscribe
    public void onFournisseurSelected(SelectedFournisseurEvent fournisseurEvent) {
        getFournisseur();
    }


    @Override
    public void onClick(View v, int position, final PostData_Carnet_f carnet_f) {

        switch (v.getId()) {
            case R.id.btn_edit_situation -> {

                if (carnet_f.is_exported != 0) {
                    new SweetAlertDialog(ActivityFournisseurDetail.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention !")
                            .setContentText("Versement déjà exporté, Modification impossible !")
                            .show();
                    return;

                }
                if (prefs.getBoolean("AUTORISE_MODIFY_BON", true)) {
                    new SweetAlertDialog(ActivityFournisseurDetail.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Situation")
                            .setContentText("Voulez-vous vraiment modifier cette situation?!")
                            .setCancelText("Non")
                            .setConfirmText("Modifier")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                FragmentVersementFournisseur fragmentversementfournisseur = new FragmentVersementFournisseur();
                                fragmentversementfournisseur.showDialogbox(ActivityFournisseurDetail.this, fournisseur.solde_montant, fournisseur.verser_montant, carnet_f.carnet_versement, carnet_f.carnet_remarque, fournisseur.code_frs, true, carnet_f.recordid);

                                sDialog.dismiss();
                            }).show();
                } else {
                    new SweetAlertDialog(ActivityFournisseurDetail.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention !")
                            .setContentText("Vous n'êtes pas autorisé à modifier cette situation !")
                            .show();
                }

            }
            case R.id.btn_remove_situation -> {

                if (carnet_f.is_exported != 0) {
                    new SweetAlertDialog(ActivityFournisseurDetail.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention !")
                            .setContentText("Versement déjà exporté, Suppression impossible !")
                            .show();
                    return;

                }

                if (prefs.getBoolean("AUTORISE_MODIFY_BON", true)) {
                    new SweetAlertDialog(ActivityFournisseurDetail.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Suppression")
                            .setContentText("Voulez-vous vraiment supprimer la situation " + carnet_f.recordid + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Supprimer")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                if (controller.delete_versement_fournisseur(carnet_f, fournisseur.solde_montant + carnet_f.carnet_versement, fournisseur.verser_montant - carnet_f.carnet_versement)) {
                                    Crouton.makeText(ActivityFournisseurDetail.this, "Situation fournisseur supprimé !", Style.INFO).show();
                                } else {
                                    Crouton.makeText(ActivityFournisseurDetail.this, "Problème au moment de suppression de la situation !", Style.ALERT).show();
                                }
                                Update_fournisseur_details();

                                sDialog.dismiss();

                            }).show();
                } else {
                    new SweetAlertDialog(ActivityFournisseurDetail.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention !")
                            .setContentText("Vous n'êtes pas autorisé à supprimer cette situation !")
                            .show();
                }

            }

            case R.id.lnr_item_root -> {
                selected_versement = carnet_f;
            }

        }
    }

    protected void Print_versement(PostData_Carnet_f carnet_f_print) {


        /*carnet_f_print = new PostData_Carnet_f();
        String querry = "SELECT CARNET_F.RECORDID, " +
                "CARNET_F.CODE_FRS, " +
                "CARNET_F.DATE_CARNET, " +

                "FOURNIS.FOURNIS, " +
                "FOURNIS.LATITUDE, " +
                "FOURNIS.LONGITUDE, " +
                "FOURNIS.CODE_FRS, " +
                "FOURNIS.TEL, " +
                "FOURNIS.ADRESSE, " +
                "FOURNIS.RC, " +
                "FOURNIS.IFISCAL, " +
                "FOURNIS.AI, " +
                "FOURNIS.NIS, " +
                "FOURNIS.MODE_TARIF, " +

                "CARNET_F.HEURE, " +
                "CARNET_F.ACHATS, " +
                "CARNET_F.VERSEMENTS, " +
                "CARNET_F.SOURCE, " +
                "CARNET_F.NUM_BON, " +
                "CARNET_F.MODE_RG, " +
                "CARNET_F.REMARQUES, " +
                "CARNET_F.UTILISATEUR, " +
                "CARNET_F.EXPORTATION " +

                "FROM CARNET_F " +
                "LEFT JOIN FOURNIS ON " +
                "CARNET_F.CODE_FRS = FOURNIS.CODE_FRS " +
                
                "WHERE CARNET_F.CODE_FRS = '" + fournisseur.code_frs + "' ";

        carnet_f_print  = controller.select_carnet_f_from_database_single(querry);*/
        //carnet_f_print = selected_versement;


        Activity bactivity;
        bactivity = ActivityFournisseurDetail.this;

        Printing printer = new Printing();
        printer.start_print_versement_fournisseur(bactivity, carnet_f_print);

    }


    @Override
    protected void onDestroy() {

        bus.unregister(this);
        stopService(intent_location);

        super.onDestroy();
    }

}
