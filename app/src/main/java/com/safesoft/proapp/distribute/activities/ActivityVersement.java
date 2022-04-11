package com.safesoft.proapp.distribute.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.safesoft.proapp.distribute.activities.vente.ActivityVentes;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.R;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;

import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityVersement extends AppCompatActivity {

    ////////////////////////////////////////
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int PRINTPICTURE = 14;

    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog_wait_connecte;
    private final static int MAX_LEFT_DISTANCE = 255;
    private ProgressDialog dialog;
    private MyHandler_Integrate handler_Integrate;
    private MyHandler_Bluetooth handler_Bluetooth;
    private String PREFS_FOOTER = "Footer";

    private MediaPlayer mp;

    private TextView txt_ancien_solde, txt_montant_bon, txt_actuel_solde, txt_nouveau_solde;

    //private SlantedTextView stv ;
    private Button valider, imprimer;

    private EasyTextInputLayout versement_edittext;

    private Double actuel_solde, nouveau_solde;

    private EventBus bus = EventBus.getDefault();


    private PostData_Bon1 final_bon1_prepared;
    private List<PostData_Bon2> final_panier_prepared;

    private DATABASE controller;

    private BluetoothSPP bt;
    private String PREFS_PRINTER = "ConfigPrinter";
    Boolean printer_mode_integrate = true;

    private String NUM_BON;
    private String CODE_DEPOT;
    private Double LATITUDE;
    private Double LONGITUDE;

    private NumberFormat nf;

    private LinearLayout lnr_over_solde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_versement);

        controller = new DATABASE(this);
        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Versement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));

        IntentFilter pIntentFilter = new IntentFilter();
        pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
        registerReceiver(printReceive, pIntentFilter);


        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            //finish();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {

            public void onDeviceDisconnected() {
                if (progressDialog_wait_connecte != null && !ActivityVersement.this.isFinishing()) {
                    progressDialog_wait_connecte.dismiss();
                    progressDialog_wait_connecte = null;
                }
                Crouton.makeText(ActivityVersement.this, "Imprimente bluetooth non connecté ", Style.ALERT).show();
            }

            public void onDeviceConnectionFailed() {
                if (progressDialog_wait_connecte != null && !ActivityVersement.this.isFinishing()) {
                    progressDialog_wait_connecte.dismiss();
                    progressDialog_wait_connecte = null;
                }
                Crouton.makeText(ActivityVersement.this, "Imprimente connection erroné ", Style.ALERT).show();
            }

            public void onDeviceConnected(String name, String address) {
                if (progressDialog_wait_connecte != null && !ActivityVersement.this.isFinishing()) {
                    progressDialog_wait_connecte.dismiss();
                    progressDialog_wait_connecte = null;
                }
                Crouton.makeText(ActivityVersement.this, "Imprimente connecté à  " + name, Style.CONFIRM).show();
                prepareBon_Bluetooth();
            }

        });

        //controller.update_bon1()
    }

    @Override
    protected void onStart() {


        final_bon1_prepared = controller.select_bon1_from_database2("" +
                "SELECT Bon1.NUM_BON, " +
                "Bon1.CODE_CLIENT, " +
                "Bon1.DATE_BON, " +
                "Bon1.HEURE, " +
                "Bon1.CODE_DEPOT, " +
                "Bon1.TOT_HT, " +
                "Bon1.TOT_TVA, " +
                "Bon1.TOT_TTC, " +
                "Bon1.TOT_TTC_REMISE, " +
                "Bon1.MONTANT_BON, " +
                "Bon1.REMISE, " +
                "Bon1.REMISE_CHECK, " +
                "Bon1.TIMBRE, " +
                "Bon1.NBR_P, " +
                "Bon1.TIMBRE_CHECK, " +
                "Bon1.RESTE, " +
                "Bon1.VERSER, " +
                "Bon1.LATITUDE, " +
                "Bon1.LONGITUDE, " +
                "Client.CLIENT, " +
                "Client.CREDIT_LIMIT, " +
                "coalesce(Bon1.ANCIEN_SOLDE,0) AS ANCIEN_SOLDE " +
                "FROM Bon1,Client " +
                "WHERE " +
                "Bon1.CODE_CLIENT = Client.CODE_CLIENT " +
                "AND " +
                "Bon1.NUM_BON ='" + NUM_BON + "'");

        final_panier_prepared = controller.select_bon2_from_database("" +
                "SELECT " +
                "Bon2.RECORDID, " +
                "Bon2.CODE_BARRE, " +
                "Bon2.NUM_BON, " +
                "Bon2.PRODUIT, " +
                "Bon2.QTE, " +
                "Bon2.PV_HT, " +
                "Bon2.TVA, " +
                "Bon2.CODE_DEPOT, " +
                "Bon2.PA_HT, " +
                "Produit.STOCK " +
                "FROM Bon2 " +
                "INNER JOIN " +
                "Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                "WHERE Bon2.NUM_BON = '" + NUM_BON + "'");

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        calcule_in_versement();

        txt_ancien_solde.setText(nf.format(Double.valueOf(final_bon1_prepared.solde_ancien)));
        txt_montant_bon.setText(nf.format(Double.valueOf(final_bon1_prepared.montant_bon)));


        SharedPreferences prefs1 = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if (prefs1.getString("PRINTER", "INTEGRATE").toString().equals("INTEGRATE")) {
            printer_mode_integrate = true;
        } else {
            printer_mode_integrate = false;
        }

        if (!printer_mode_integrate) {
            if (!bt.isBluetoothEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            } else {
                if (!bt.isServiceAvailable()) {
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_ANDROID);
                    setup();
                }
            }
        }


        super.onStart();
    }

    protected void initView() {

        final_bon1_prepared = new PostData_Bon1();
        final_panier_prepared = new ArrayList<>();

        //TextView
        txt_ancien_solde = (TextView) findViewById(R.id.ancien_solde);
        txt_montant_bon = (TextView) findViewById(R.id.montant_bon);
        txt_actuel_solde = (TextView) findViewById(R.id.current_sold);
        txt_nouveau_solde = (TextView) findViewById(R.id.new_sold);

        // Linear layout
        lnr_over_solde = (LinearLayout) findViewById(R.id.lnr_over_sold);

        //EditText
        versement_edittext = (EasyTextInputLayout) findViewById(R.id.versement_edittext);
        versement_edittext.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              /*  if(versement_edittext.getEditText().getText().length() >0){
                    nouveau_solde = actuel_solde - Double.valueOf(versement_edittext.getEditText().getText().toString());
                    txt_nouveau_solde.setText(String.valueOf(nf.format(nouveau_solde)));

                }*/

                calcule_in_versement();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (getIntent() != null) {
            //
            NUM_BON = getIntent().getStringExtra("NUM_BON");
            CODE_DEPOT = getIntent().getStringExtra("CODE_DEPOT");
            LATITUDE = getIntent().getDoubleExtra("LATITUDE", 0.00);
            LONGITUDE = getIntent().getDoubleExtra("LONGITUDE", 0.00);
        }

        //Button
        valider = (Button) findViewById(R.id.valider);
        imprimer = (Button) findViewById(R.id.imprimer);

    }

    protected void calcule_in_versement() {
        if (!txt_ancien_solde.getText().toString().isEmpty() && !txt_montant_bon.getText().toString().isEmpty()) {
            actuel_solde = Double.valueOf(final_bon1_prepared.solde_ancien) + Double.valueOf(final_bon1_prepared.montant_bon);
            txt_actuel_solde.setText(nf.format(actuel_solde));

            Double versement_montant = 0.00;
            if (versement_edittext.getEditText().getText().length() > 0) {
                if (versement_edittext.getEditText().getText().toString().equals(".")) {
                    versement_edittext.getEditText().setText("0.");
                    txt_nouveau_solde.setText(nf.format(actuel_solde));
                    nouveau_solde = actuel_solde;
                } else {
                    versement_montant = Double.valueOf(versement_edittext.getEditText().getText().toString());
                    txt_nouveau_solde.setText(new DecimalFormat("##,##0.00").format(actuel_solde - versement_montant));
                    nouveau_solde = actuel_solde - versement_montant;
                }
            } else {
                txt_nouveau_solde.setText(nf.format(actuel_solde));
                nouveau_solde = actuel_solde;
            }


            if (final_bon1_prepared.credit_limit > 0) {
                if ((actuel_solde - versement_montant) > final_bon1_prepared.credit_limit) {
                    // Show message erreur oversolde
                    lnr_over_solde.setVisibility(View.VISIBLE);
                } else {
                    lnr_over_solde.setVisibility(View.GONE);
                }
            }
        }
    }

    public void onClickVersement(View v) {
        switch (v.getId()) {
            case R.id.valider:
                if (versement_edittext.getEditText().getText().toString().length() > 0) {

                    if (!controller.check_if_bon1_valide("Bon1", final_bon1_prepared.num_bon)) {
                        new SweetAlertDialog(ActivityVersement.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Bon de vente")
                                .setContentText("Voulez-vous vraiment valider ce Bon ?!")
                                .setCancelText("Non")
                                .setConfirmText("Oui")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        final_bon1_prepared.verser = versement_edittext.getEditText().getText().toString();
                                        final_bon1_prepared.reste = String.valueOf(nouveau_solde);
                                        final_bon1_prepared.latitude = LATITUDE;
                                        final_bon1_prepared.longitude = LONGITUDE;

                                        // Insert bon1, bon2 into local database
                                        if (controller.update_bon1_client(NUM_BON, final_bon1_prepared)) {
                                            new SweetAlertDialog(ActivityVersement.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Réussit!")
                                                    .setContentText("Bon Validé!")
                                                    .show();

                                            imprimer.setEnabled(true);
                                            versement_edittext.getEditText().setEnabled(false);

                                        } else {
                                            new SweetAlertDialog(ActivityVersement.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Oops...")
                                                    .setContentText("Bon non validé, erreur fatal!")
                                                    .show();
                                        }
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    } else {

                        new SweetAlertDialog(ActivityVersement.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention !")
                                .setContentText("Ce bon est déjà validé !")
                                .show();
                    }
                } else {
                    Crouton.makeText(ActivityVersement.this, "Montant versement incorrect !", Style.ALERT).show();
                }
                break;
            case R.id.imprimer:
                if (printer_mode_integrate) {
                    prepareBon_IntegratePrinter();
                } else {
                    if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                        // print
                        Toast.makeText(ActivityVersement.this, " Imprission ...", Toast.LENGTH_SHORT).show();
                        prepareBon_Bluetooth();

                    } else {
                        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
                        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                    }
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bt.connect(data);
                progressDialog_wait_connecte = ProgressDialog.show(ActivityVersement.this, getString(R.string.bl_dy1), getString(R.string.printing_wait1));
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Preparation de Bon pour l'imprimente intégrer
    protected void prepareBon_IntegratePrinter() {

        handler_Integrate = new ActivityVersement.MyHandler_Integrate();
        if (LowBattery == true) {
            handler_Integrate.sendMessage(handler_Integrate.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(ActivityVersement.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                handler_Integrate.sendMessage(handler_Integrate.obtainMessage(PRINTCONTENT, 1, 0, null));
            } else {
                Toast.makeText(ActivityVersement.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }
    }


    protected void prepareBon_Bluetooth() {

        handler_Bluetooth = new ActivityVersement.MyHandler_Bluetooth();
        if (LowBattery == true) {
            handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(ActivityVersement.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(PRINTCONTENT, 1, 0, null));
            } else {
                Toast.makeText(ActivityVersement.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }
    }

    //Class thread printing
    private class contentPrintThread_Integrate extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                ThermalPrinter.start(ActivityVersement.this);
                ThermalPrinter.reset();
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
                /////////////////////////////
                ThermalPrinter.reset();
                ThermalPrinter.setLeftIndent(0);
                ThermalPrinter.setLineSpace(28);
                ThermalPrinter.setFontSize(2);
                ThermalPrinter.setGray(12);

                SharedPreferences prefs = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
                if (prefs.getBoolean("ENTETE_SHOW", false)) {

                    ThermalPrinter.addString(prefs.getString("COMPANY_NAME", "") + "\n");
                    ThermalPrinter.addString(prefs.getString("ACTIVITY_NAME", "") + "\n");
                    ThermalPrinter.addString(prefs.getString("ADRESSE", "") + "\n");
                    ThermalPrinter.addString("--------------------------------");
                    ThermalPrinter.printString();
                }

                ThermalPrinter.reset();
                ThermalPrinter.setLeftIndent(0);
                ThermalPrinter.setLineSpace(28);
                ThermalPrinter.setFontSize(2);
                ThermalPrinter.enlargeFontSize(2, 2);
                ThermalPrinter.setGray(12);
                ////////////////////////////////
                //  ThermalPrinter.setBold(true);
                ThermalPrinter.addString(" BON DE VENTE\n");
                ThermalPrinter.addString("  " + final_bon1_prepared.num_bon + "\n");
                ThermalPrinter.addString("----------------");
                ThermalPrinter.printString();
                /////////////////////////////
                ThermalPrinter.reset();
                ThermalPrinter.setLeftIndent(0);
                ThermalPrinter.setLineSpace(28);
                ThermalPrinter.setFontSize(2);
                ThermalPrinter.setGray(12);
                ////////////////////////////////
                ThermalPrinter.addString("CLIENT : " + final_bon1_prepared.client);
                ThermalPrinter.printString();
                ThermalPrinter.addString("CODE CLIENT : " + final_bon1_prepared.code_client);
                ThermalPrinter.printString();
                ThermalPrinter.addString("          ************");
                ThermalPrinter.printString();
                ThermalPrinter.addString("CODE_DEPOT : " + final_bon1_prepared.code_depot);
                ThermalPrinter.printString();
                ThermalPrinter.addString("DATE HEURE : " + final_bon1_prepared.date_bon + " " + final_bon1_prepared.heure);
                ThermalPrinter.printString();
                ThermalPrinter.addString("--------------------------------");
                ThermalPrinter.printString();
                ///////////////////////
                ThermalPrinter.reset();
                ThermalPrinter.setLeftIndent(0);
                ThermalPrinter.setLineSpace(28);
                ThermalPrinter.setFontSize(1);
                ThermalPrinter.enlargeFontSize(1, 2);
                ThermalPrinter.setGray(12);
                ///////////////////////////


                for (int b = 0; b < final_panier_prepared.size(); b++) {
                    if (final_panier_prepared.get(b).produit.length() > 48) {
                        final_panier_prepared.get(b).produit = final_panier_prepared.get(b).produit.substring(0, 47);
                    }
                    ThermalPrinter.addString(final_panier_prepared.get(b).produit);
                    ThermalPrinter.printString();

                    String quantite = final_panier_prepared.get(b).qte;
                    String prix_u = nf.format(Double.valueOf(final_panier_prepared.get(b).p_u));
                    String total_produit = nf.format(Double.valueOf(final_panier_prepared.get(b).p_u) * Double.valueOf(final_panier_prepared.get(b).qte));
                    String espace_walk0 = "", espace_walk1 = "", espace_walk2 = "", espace_walk3 = "", espace_walk4 = "";

                    //Walk 1
                    if (quantite.length() == 1) {
                        espace_walk0 = "     ";
                    } else if (quantite.length() == 2) {
                        espace_walk0 = "    ";
                    } else if (quantite.length() == 3) {
                        espace_walk0 = "   ";
                    } else if (quantite.length() == 4) {
                        espace_walk0 = "  ";
                    } else if (quantite.length() == 5) {
                        espace_walk0 = " ";
                    }

                    espace_walk1 = "   ";

                    //Walk 2
                    if (prix_u.length() == 4) {
                        espace_walk2 = "          ";
                    } else if (prix_u.length() == 5) {
                        espace_walk2 = "         ";
                    } else if (prix_u.length() == 6) {
                        espace_walk2 = "        ";
                    } else if (prix_u.length() == 7) {
                        espace_walk2 = "       ";
                    } else if (prix_u.length() == 8) {
                        espace_walk2 = "      ";
                    } else if (prix_u.length() == 9) {
                        espace_walk2 = "     ";
                    } else if (prix_u.length() == 10) {
                        espace_walk2 = "    ";
                    } else if (prix_u.length() == 11) {
                        espace_walk2 = "   ";
                    } else if (prix_u.length() == 12) {
                        espace_walk2 = "  ";
                    }


                    //walk3
                    espace_walk3 = "   ";
                    // walk4
                    if (total_produit.length() == 4) {
                        espace_walk4 = "                ";
                    } else if (total_produit.length() == 5) {
                        espace_walk4 = "               ";
                    } else if (total_produit.length() == 6) {
                        espace_walk4 = "              ";
                    } else if (total_produit.length() == 7) {
                        espace_walk4 = "             ";
                    } else if (total_produit.length() == 8) {
                        espace_walk4 = "            ";
                    } else if (total_produit.length() == 9) {
                        espace_walk4 = "           ";
                    } else if (total_produit.length() == 10) {
                        espace_walk4 = "          ";
                    } else if (total_produit.length() == 11) {
                        espace_walk4 = "         ";
                    } else if (total_produit.length() == 12) {
                        espace_walk4 = "        ";
                    } else if (total_produit.length() == 13) {
                        espace_walk4 = "       ";
                    } else if (total_produit.length() == 14) {
                        espace_walk4 = "      ";
                    } else if (total_produit.length() == 15) {
                        espace_walk4 = "     ";
                    } else if (total_produit.length() == 16) {
                        espace_walk4 = "    ";
                    } else if (total_produit.length() == 17) {
                        espace_walk4 = "   ";
                    } else if (total_produit.length() == 18) {
                        espace_walk4 = "  ";
                    }

                    ThermalPrinter.addString(espace_walk0 + quantite + espace_walk1 + "X" + espace_walk2 + prix_u + espace_walk3 + "=" + espace_walk4 + total_produit);
                    ThermalPrinter.printString();
                }

                ThermalPrinter.addString("------------------------------------------------");
                ThermalPrinter.printString();
                /////////////////////////////
                ThermalPrinter.reset();
                ThermalPrinter.setLeftIndent(0);
                ThermalPrinter.setLineSpace(28);
                ThermalPrinter.setFontSize(2);
                ThermalPrinter.setGray(12);
                /////////////////////////////////////////////
                String walk = "";
                String total_ht = "TOTAL HT : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ht)) + " DA";
                for (int a = 0; a < 32 - total_ht.length(); a++) {
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + total_ht);
                ThermalPrinter.printString();

                walk = "";
                String tva = "TVA : " + nf.format(Double.valueOf(final_bon1_prepared.tot_tva)) + " DA";
                for (int a = 0; a < 32 - tva.length(); a++) {
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + tva);
                ThermalPrinter.printString();

                if (final_bon1_prepared.timbre_ckecked) {
                    walk = "";
                    String timbre = "TIMBRE : " + nf.format(Double.valueOf(final_bon1_prepared.timbre)) + " DA";
                    for (int a = 0; a < 32 - timbre.length(); a++) {
                        walk = walk + " ";
                    }
                    ThermalPrinter.addString(walk + timbre);
                    ThermalPrinter.printString();
                }

                walk = "";
                String total_ttc = "TOTAL TTC : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc)) + " DA";
                for (int a = 0; a < 32 - total_ttc.length(); a++) {
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + total_ttc);
                ThermalPrinter.printString();


                if (final_bon1_prepared.remise_ckecked) {

                    walk = "";
                    for (int a = 0; a < 32 - total_ttc.length(); a++) {
                        walk = walk + " ";
                    }

                    String line = "";
                    for (int a = 0; a < total_ttc.length(); a++) {
                        line = line + "_";
                    }

                    ThermalPrinter.addString(walk + line);
                    ThermalPrinter.printString();

                    walk = "";
                    String remise = "REMISE : " + nf.format(Double.valueOf(final_bon1_prepared.remise)) + " DA";
                    for (int a = 0; a < 32 - remise.length(); a++) {
                        walk = walk + " ";
                    }
                    ThermalPrinter.addString(walk + remise);
                    ThermalPrinter.printString();

                    walk = "";
                    String total_ttc_apres_remise = "MONTANT BON : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc_remise.toString())) + " DA";
                    for (int a = 0; a < 32 - total_ttc_apres_remise.length(); a++) {
                        walk = walk + " ";
                    }
                    ThermalPrinter.addString(walk + total_ttc_apres_remise);
                    ThermalPrinter.printString();
                }

                ThermalPrinter.addString("--------------------------------");
                ThermalPrinter.printString();

                walk = "";
                String Anciensolde = "ANCIEN SOLDE : " + nf.format(Double.valueOf(final_bon1_prepared.solde_ancien)) + " DA";
                for (int a = 0; a < 32 - Anciensolde.length(); a++) {
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + Anciensolde);
                ThermalPrinter.printString();


                if (final_bon1_prepared.remise_ckecked) {
                    walk = "";
                    String total_ttc_apres_remise = "MONTANT BON : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc_remise.toString())) + " DA";
                    for (int a = 0; a < 32 - total_ttc_apres_remise.length(); a++) {
                        walk = walk + " ";
                    }
                    ThermalPrinter.addString(walk + total_ttc_apres_remise);
                    ThermalPrinter.printString();
                } else {
                    walk = "";
                    String total_ttc_apres_remise = "MONTANT BON : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc.toString())) + " DA";
                    for (int a = 0; a < 32 - total_ttc_apres_remise.length(); a++) {
                        walk = walk + " ";
                    }
                    ThermalPrinter.addString(walk + total_ttc_apres_remise);
                    ThermalPrinter.printString();
                }


                walk = "";
                String Actuelsolde = "SOLDE ACTUEL: " + nf.format(Double.valueOf(final_bon1_prepared.solde_ancien) + Double.valueOf(final_bon1_prepared.montant_bon)) + " DA";
                for (int a = 0; a < 32 - Actuelsolde.length(); a++) {
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + Actuelsolde);
                ThermalPrinter.printString();


                walk = "";
                String versement = "VERSEMENT : " + nf.format(Double.valueOf(final_bon1_prepared.verser)) + " DA";
                for (int a = 0; a < 32 - versement.length(); a++) {
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + versement);
                ThermalPrinter.printString();

                walk = "";
                String reste = "NOUVEAU SOLDE : " + nf.format(Double.valueOf(final_bon1_prepared.reste)) + " DA";
                for (int a = 0; a < 32 - reste.length(); a++) {
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + reste);
                ThermalPrinter.printString();


                ThermalPrinter.walkPaper(100);

            } catch (TelpoException e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler_Integrate.sendMessage(handler_Integrate.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler_Integrate.sendMessage(handler_Integrate.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler_Integrate.sendMessage(handler_Integrate.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler_Integrate.sendMessage(handler_Integrate.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
                ThermalPrinter.stop(ActivityVersement.this);
            }
        }
    }

    private class MyHandler_Integrate extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityVersement.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;
                case PRINTVERSION:
                    dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        // textPrintVersion.setText(printVersion);
                    } else {
                        Toast.makeText(ActivityVersement.this, "Operation failed", Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTCONTENT:
                    new ActivityVersement.contentPrintThread_Integrate().start();
                    break;
                case PRINTPICTURE:
                    //   new printPicture().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !ActivityVersement.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityVersement.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(ActivityVersement.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    private class MyHandler_Bluetooth extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PRINTVERSION:
                    dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        // textPrintVersion.setText(printVersion);
                    } else {
                        Toast.makeText(ActivityVersement.this, "Operation failed", Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTCONTENT:
                    new ActivityVersement.contentPrintThread_Bluetooth().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !ActivityVersement.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityVersement.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(ActivityVersement.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                //TPS390 can not print,while in low battery,whether is charging or not charging
//                if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()){
//                    if (level * 5 <= scale) {
//                        LowBattery = true;
//                    } else {
//                        LowBattery = false;
//                    }
//                }else {
                if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                    if (level * 5 <= scale) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    LowBattery = false;
                }
//                }
            }
            //Only use for TPS550MTK devices
            else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
                int status = intent.getIntExtra("action", 0);
                int level = intent.getIntExtra("level", 0);
                if (status == 0) {
                    if (level < 1) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    LowBattery = false;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void noPaperDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(ActivityVersement.this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ThermalPrinter.stop(ActivityVersement.this);
            }
        });
        dlg.show();
    }

    //Class thread printing
    private class contentPrintThread_Bluetooth extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                byte[] arrayOfByte1 = {27, 33, 0};
                byte[] format = {27, 33, 0};
                // format[2] = ((byte) (0x20 | arrayOfByte1[2]));
                byte[] format_normal = {27, 15, 0}; // manipulate your font size in the second parameter
                byte[] format01 = {27, 33, 18}; // manipulate your font size in the second parameter
                byte[] center = {0x1b, 'a', 0x01}; // center alignment
                byte[] left = {0x1b, 'a', 0x00}; // center alignment
                byte[] right = {0x1b, 'a', 0x02}; // center alignment
                // Underline
                // format[2] = ((byte)(0x80 | arrayOfByte1[2]));
                bt.send(format, true);

                SharedPreferences prefs = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
                if (prefs.getBoolean("ENTETE_SHOW", false)) {
                    bt.send(center, true);


                    bt.send(prefs.getString("COMPANY_NAME", ""), true);
                    bt.send(prefs.getString("ACTIVITY_NAME", ""), true);
                    bt.send(prefs.getString("ADRESSE", ""), true);

                    bt.send("--------------------------------", true);

                }

                ////////////////////////////////
                //bt.send(center, true);
                format[2] = ((byte) (0x20 | arrayOfByte1[2]));
                bt.send(format, true);
                bt.send("BON DE LIVRAISON", true);
                bt.send("N " + final_bon1_prepared.num_bon, true);
                //bt.send("------------------------------------------------", true);

                /////////////////////////////

                SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");
                Date myDate = null;
                try {
                    myDate = df_save.parse(final_bon1_prepared.date_bon);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String formattedDate_Show = df_show.format(myDate);

                ////////////////////////////////
                bt.send(left, true);
                format[2] = ((byte) (0x1 | arrayOfByte1[2]));
                bt.send(format, true);
                bt.send("CLIENT : " + final_bon1_prepared.client.toUpperCase(), true);

                bt.send("CODE CLIENT : " + final_bon1_prepared.code_client, true);
                bt.send("          ************", true);

                // bt.send(format_normal, true);
                bt.send("CODE_DEPOT : " + final_bon1_prepared.code_depot, true);
                bt.send("DATE HEURE : " + formattedDate_Show + " " + final_bon1_prepared.heure, true);
                bt.send("------------------------------------------------", true);


                ///////////////////////

                ///////////////////////////

                format[2] = ((byte) (0x1 | arrayOfByte1[2]));
                bt.send(format, true);

                for (int b = 0; b < final_panier_prepared.size(); b++) {

                    if (final_panier_prepared.get(b).produit.length() > 48) {
                        final_panier_prepared.get(b).produit = final_panier_prepared.get(b).produit.substring(0, 47);
                    }


                    String quantite = final_panier_prepared.get(b).qte;
                    String prix_u = nf.format(Double.valueOf(final_panier_prepared.get(b).p_u) + (Double.valueOf(final_panier_prepared.get(b).p_u) * (Double.valueOf(final_panier_prepared.get(b).tva) / Double.valueOf(100))));
                    String total_produit = nf.format((Double.valueOf(final_panier_prepared.get(b).p_u) + (Double.valueOf(final_panier_prepared.get(b).p_u) * (Double.valueOf(final_panier_prepared.get(b).tva) / Double.valueOf(100)))) * Double.valueOf(final_panier_prepared.get(b).qte));

                    bt.send(final_panier_prepared.get(b).produit + " / " + quantite + " X " + prix_u + " = " + total_produit, true);
                }

                format[2] = ((byte) (0x0 | arrayOfByte1[2]));
                bt.send(format, true);
                bt.send("------------------------------------------------", true);

                /////////////////////////////////////////////
                String walk = "";


                if (final_bon1_prepared.timbre_ckecked) {
                    walk = "";
                    String timbre = "TIMBRE : " + nf.format(Double.valueOf(final_bon1_prepared.timbre.toString())) + " DA";
                    for (int a = 0; a < 48 - timbre.length(); a++) {
                        walk = walk + " ";
                    }
                    bt.send(walk + timbre, true);
                }

                walk = "";
                String total_ttc = "TOTAL TTC : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc.toString())) + " DA";
                for (int a = 0; a < 48 - total_ttc.length(); a++) {
                    walk = walk + " ";
                }
                bt.send(walk + total_ttc, true);


                if (final_bon1_prepared.remise_ckecked) {

                    walk = "";
                    for (int a = 0; a < 48 - total_ttc.length(); a++) {
                        walk = walk + " ";
                    }

                    String line = "";
                    for (int a = 0; a < total_ttc.length(); a++) {
                        line = line + "_";
                    }

                    bt.send(walk + line, true);

                    walk = "";
                    String remise = "REMISE : " + nf.format(Double.valueOf(final_bon1_prepared.remise.toString())) + " DA";
                    for (int a = 0; a < 48 - remise.length(); a++) {
                        walk = walk + " ";
                    }
                    bt.send(walk + remise, true);

                    walk = "";
                    String total_ttc_apres_remise = "MONTANT BON : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc_remise.toString())) + " DA";
                    for (int a = 0; a < 48 - total_ttc_apres_remise.length(); a++) {
                        walk = walk + " ";
                    }
                    bt.send(walk + total_ttc_apres_remise, true);
                }

                bt.send("------------------------------------------------", true);

                walk = "";
                String Anciensolde = "ANCIEN SOLDE : " + nf.format(Double.valueOf(final_bon1_prepared.solde_ancien)) + " DA";
                for (int a = 0; a < 48 - Anciensolde.length(); a++) {
                    walk = walk + " ";
                }
                bt.send(walk + Anciensolde, true);


                if (final_bon1_prepared.remise_ckecked) {
                    walk = "";
                    String total_ttc_apres_remise = "MONTANT BON : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc_remise.toString())) + " DA";
                    for (int a = 0; a < 48 - total_ttc_apres_remise.length(); a++) {
                        walk = walk + " ";
                    }
                    bt.send(walk + total_ttc_apres_remise, true);

                } else {
                    walk = "";
                    String total_ttc_apres_remise = "MONTANT BON : " + nf.format(Double.valueOf(final_bon1_prepared.tot_ttc.toString())) + " DA";
                    for (int a = 0; a < 48 - total_ttc_apres_remise.length(); a++) {
                        walk = walk + " ";
                    }
                    bt.send(walk + total_ttc_apres_remise, true);
                }


                walk = "";
                Double current_solde = (Double.valueOf(final_bon1_prepared.solde_ancien) + Double.valueOf(final_bon1_prepared.montant_bon));
                String solde_actuel = "SOLDE ACTUEL: " + nf.format(current_solde) + " DA";
                for (int a = 0; a < 48 - solde_actuel.length(); a++) {
                    walk = walk + " ";
                }
                bt.send(walk + solde_actuel, true);


                walk = "";
                String versement = "VERSEMENT : " + nf.format(Double.valueOf(final_bon1_prepared.verser)) + " DA";
                for (int a = 0; a < 48 - versement.length(); a++) {
                    walk = walk + " ";
                }
                bt.send(walk + versement, true);


                walk = "";
                String reste = "NOUVEAU SOLDE : " + nf.format(Double.valueOf(final_bon1_prepared.reste)) + " DA";
                for (int a = 0; a < 48 - reste.length(); a++) {
                    walk = walk + " ";
                }
                bt.send(walk + reste, true);
                bt.send("------------------------------------------------", true);
                prefs = getSharedPreferences(PREFS_FOOTER, MODE_PRIVATE);
                if (prefs.getBoolean("FOOTER", false)) {
                    bt.send(right, true);


                    bt.send(prefs.getString("MERCI", "").toUpperCase(), true);
                    bt.send(left, true);
                    format[2] = ((byte) (0x1 | arrayOfByte1[2]));
                    bt.send(format, true);
                    bt.send(prefs.getString("ENCAS", ""), true);


                } else {
                    bt.send(right, true);

                    format[2] = ((byte) (0x1 | arrayOfByte1[2]));
                    bt.send(format, true);
                    bt.send("MERCI", true);

                }
                bt.send("\n", true);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(PRINTERR, 1, 0, null));

            } finally {
                handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(CANCELPROMPT, 1, 0, null));
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister
        bus.unregister(this);
        if (progressDialog != null && !ActivityVersement.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        unregisterReceiver(printReceive);

//        try {
//            ThermalPrinter.stop();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (bt != null)
            bt.stopService();

        super.onDestroy();
    }

    public void setup() {
     /*   Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(etMessage.getText().length() != 0) {
                    bt.send(etMessage.getText().toString(), true);
                    etMessage.setText("");
                }
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_bon_vente_commande, menu);
        // return true so that the menu pop up is opened
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        /*
        else if(item.getItemId() == R.id.menu_device_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);

            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(item.getItemId() == R.id.menu_device_disconnect) {
            if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
                bt.disconnect();
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result",1);
        setResult(RESULT_OK, resultIntent);
        finish();
//        Intent intent = new Intent(ActivityVersement.this, ActivityVentes.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//
//        Sound( R.raw.back);
//        super.onBackPressed();
    }
    public void Sound(int resid){
        mp = MediaPlayer.create(this, resid);
        mp.start();
    }
}
