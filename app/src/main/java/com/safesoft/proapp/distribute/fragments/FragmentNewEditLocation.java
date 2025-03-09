package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.tournee_clients.ORSDistanceCalculator;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.ByteDataEvent;
import com.safesoft.proapp.distribute.eventsClasses.PassageEvent;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Tournee2;
import com.safesoft.proapp.distribute.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentNewEditLocation {
    MaterialButton btn_valider, btn_cancel;
    TextInputEditText edt_num_tournee, edt_date_passage, edt_heure_passage, edt_code_client, edt_observation;
    TextView txtv_latitude, txtv_longitude, txtv_distance_value;
    EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    ImageView img_statusIndicator, img_mapView;
    private final String PREFS = "ALL_PREFS";
    PostData_Tournee2 created_passage;
    PostData_Tournee2 old_passage;
    private DATABASE controller;
    NumberFormat nf, nq;
    private Barcode barcodeResult;
    private PostData_Params params;
    SharedPreferences prefs;
    String url_static_map;
    //PopupWindow display method

    public void showDialogbox(Activity activity, String SOURCE_ACTIVITY, PostData_Tournee2 old_passage, double latitude, double  longitude) {

        this.activity = activity;
        this.controller = new DATABASE(activity);
        this.old_passage = old_passage;

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        nq = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nq).applyPattern("####0.##");

        prefs = activity.getSharedPreferences(PREFS, MODE_PRIVATE);
        created_passage = new PostData_Tournee2();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_add_edit_passage, null);
        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();


        //Specify the length and width through constants
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);


        btn_valider = dialogview.findViewById(R.id.btnSave);
        btn_valider.setBackgroundColor(Color.parseColor("#3498db"));
        btn_valider.setTextSize(15);


        btn_cancel = dialogview.findViewById(R.id.btnCancel);
        //btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setTextSize(15);

        img_statusIndicator = dialogview.findViewById(R.id.img_statusIndicator);
        img_mapView = dialogview.findViewById(R.id.img_mapView);

        edt_num_tournee = dialogview.findViewById(R.id.numeroTournee);
        edt_date_passage = dialogview.findViewById(R.id.datePassage);
        edt_heure_passage = dialogview.findViewById(R.id.heurePassage);

        edt_code_client = dialogview.findViewById(R.id.codeClient);
        edt_observation = dialogview.findViewById(R.id.observation);


        txtv_latitude = dialogview.findViewById(R.id.txtv_latitude);
        txtv_longitude = dialogview.findViewById(R.id.txtv_longitude);
        txtv_distance_value = dialogview.findViewById(R.id.txtv_distance_value);


        created_passage.latitude = latitude;
        created_passage.longitude = longitude;

        txtv_latitude.setText("Latitude   : " + created_passage.latitude);
        txtv_longitude.setText("Longitude : " + created_passage.longitude);

        edt_num_tournee.setText(old_passage.num_tournee);
        edt_num_tournee.setEnabled(false);

        edt_code_client.setText(old_passage.code_client);
        edt_code_client.setEnabled(false);

        if(latitude != 0 && longitude != 0){

            img_statusIndicator.setImageResource(R.drawable.ic_status_green);
            setStaticMap(img_mapView, latitude, longitude);

            ORSDistanceCalculator calculator = new ORSDistanceCalculator();
            calculator.getDistance(36.899535, -121.585938, created_passage.latitude, created_passage.longitude, new ORSDistanceCalculator.DistanceCallback() {
                @Override
                public void onSuccess(String distanceText, int distanceValue) {
                    Log.e("DISTANCE", "Distance: " + distanceText + " (" + distanceValue + " mètres)");
                    txtv_distance_value.setText(distanceText);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("DISTANCE", "Erreur: " + errorMessage);
                }
            });


        }

        if (SOURCE_ACTIVITY.equals("EDIT_PASSAGE")) {

            created_passage.num_tournee = old_passage.num_tournee;

            edt_date_passage.setText(old_passage.date_passage);
            edt_date_passage.setEnabled(false);

            edt_heure_passage.setText(old_passage.heure_passage);
            edt_heure_passage.setEnabled(false);

            edt_observation.setText(old_passage.observation);

            created_passage.latitude = old_passage.latitude;
            created_passage.longitude = old_passage.longitude;

        }else{
            // get date and time
            Calendar c = Calendar.getInstance();
            SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate_Show = date_format.format(c.getTime());
            edt_date_passage.setText(formattedDate_Show);
            edt_date_passage.setEnabled(false);

            // get date and time
            SimpleDateFormat heure_format = new SimpleDateFormat("HH:mm:ss");
            String formattedHeure_Show = heure_format.format(c.getTime());
            edt_heure_passage.setText(formattedHeure_Show);
            edt_heure_passage.setEnabled(false);

        }

        ///////////////////
        prefs = activity.getSharedPreferences(PREFS, MODE_PRIVATE);

        btn_valider.setOnClickListener(v -> {
            boolean hasError = false;

            if (edt_observation.getText().length() <= 0) {
                edt_observation.setError("Observation est obligatoire!!");
                hasError = true;
            }

            if (edt_date_passage.getText().length() <= 0) {
                edt_date_passage.setError("Date de passage est est obligatoire!!");
                hasError = true;
            }

            if (edt_heure_passage.getText().length() <= 0) {

                edt_heure_passage.setError("heure de passage est obligatoire!!");
                hasError = true;
            }

            //===================================================================


            if (!hasError) {

                created_passage.num_tournee = edt_num_tournee.getText().toString();
                created_passage.date_passage = edt_date_passage.getText().toString();
                created_passage.heure_passage = edt_heure_passage.getText().toString();
                created_passage.code_client = edt_code_client.getText().toString();
                created_passage.observation = edt_observation.getText().toString();

                if (SOURCE_ACTIVITY.equals("EDIT_PRODUCT")) {

                    /*try {

                        //update client into database,
                        controller.update_into_tournee2(created_passage);
                        Crouton.makeText(activity, "Produit bien modifier", Style.INFO).show();
                        ProductEvent added_product_event = new ProductEvent(created_produit);
                        bus.post(added_product_event);

                        dialog.dismiss();
                    }catch (Exception e){
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Problème mise à jour produit : " + e.getMessage())
                                .show();
                    }*/
                } else {

                    try {

                        controller.insert_into_tournee2(created_passage);
                        Crouton.makeText(activity, "Passage bien ajouté", Style.INFO).show();
                        PassageEvent added_passage_event = new PassageEvent(created_passage);
                        bus.post(added_passage_event);

                        dialog.dismiss();
                    }catch (Exception e){
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Problème insertion : " + e.getMessage())
                                .show();
                    }

                }

                EventBus.getDefault().unregister(this);

            }

        });


        btn_cancel.setOnClickListener(v -> {
            EventBus.getDefault().unregister(this);
            dialog.dismiss();
        });


        EventBus.getDefault().register(this);
    }



    private void startScan(View view) {

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(activity)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(barcode -> {
                    barcodeResult = barcode;

                    if (view.getId() == R.id.scan_codebarre) {
                        // check if barcode is exist in database
                        String querry = "SELECT * FROM PRODUIT WHERE CODE_BARRE = '" + barcodeResult.rawValue + "'";
                        if(!controller.check_product_if_exist(querry)){
                            //edt_codebarre.setText(barcodeResult.rawValue);
                        }else {
                            //edt_codebarre.setHint("Produit / Codebarre exist ");
                        }
                    } else if (view.getId() == R.id.scan_reference) {
                        //edt_reference.setText(barcodeResult.rawValue);
                    }

                })
                .build();
        materialBarcodeScanner.startScan();
    }

    private String getRandomString(String allowed_caracters) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(13);
        for (int i = 0; i < 13; ++i)
            sb.append(allowed_caracters.charAt(random.nextInt(allowed_caracters.length())));
        return sb.toString();
    }

    void imageChooserCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(takePicture, 3000);
    }

    void imageChooserGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto, 4000);
    }

    public void setImageFromActivity(byte[] inputData) {
        byte[] inputData_1 = null;
        inputData_1 = ImageUtils.getInstant().getCompressedBitmap(inputData);
        Bitmap bitmap = BitmapFactory.decodeByteArray(inputData_1, 0, inputData_1.length);
        //img_product.setImageBitmap(bitmap);
        //created_produit.photo = inputData_1;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onByteDataRecieved(ByteDataEvent byteDataEvent) {
        byte[] inputData_1 = null;
        inputData_1 = ImageUtils.getInstant().getCompressedBitmap(byteDataEvent.getByteData());
        Bitmap bitmap = BitmapFactory.decodeByteArray(inputData_1, 0, inputData_1.length);
        //img_product.setImageBitmap(bitmap);
        //created_produit.photo = inputData_1;
    }

    private void setStaticMap(ImageView static_image_map, double latitude, double longitude) {
        int zoom = 16;
        String label = "P";
        url_static_map = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=" + zoom + "&size=1200x300&markers=color:red%7Clabel:" + label + "%7C" + latitude + "," + longitude + "&key=AIzaSyAzMUqTnhsnrXuog5ZjSrnSYMPM-XShfRA";
        if (latitude != 0 && longitude != 0) {
            new DownloadImageTask(static_image_map).execute();
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
                mIcon11 = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.noimg);
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}