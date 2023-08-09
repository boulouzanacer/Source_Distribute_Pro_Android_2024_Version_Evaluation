package com.safesoft.proapp.distribute.printing;


import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.rt.printerlibrary.bean.BluetoothEdrConfigBean;
import com.rt.printerlibrary.bean.WiFiConfigBean;
import com.rt.printerlibrary.cmd.Cmd;
import com.rt.printerlibrary.cmd.EscFactory;
import com.rt.printerlibrary.connect.PrinterInterface;
import com.rt.printerlibrary.enumerate.BarcodeStringPosition;
import com.rt.printerlibrary.enumerate.BarcodeType;
import com.rt.printerlibrary.enumerate.BmpPrintMode;
import com.rt.printerlibrary.enumerate.CommonEnum;
import com.rt.printerlibrary.enumerate.ESCFontTypeEnum;
import com.rt.printerlibrary.enumerate.SettingEnum;
import com.rt.printerlibrary.exception.SdkException;
import com.rt.printerlibrary.factory.cmd.CmdFactory;
import com.rt.printerlibrary.factory.connect.BluetoothFactory;
import com.rt.printerlibrary.factory.connect.PIFactory;
import com.rt.printerlibrary.factory.connect.WiFiFactory;
import com.rt.printerlibrary.factory.printer.PrinterFactory;
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory;
import com.rt.printerlibrary.printer.RTPrinter;
import com.rt.printerlibrary.setting.BarcodeSetting;
import com.rt.printerlibrary.setting.BitmapSetting;
import com.rt.printerlibrary.setting.CommonSetting;
import com.rt.printerlibrary.setting.TextSetting;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.utils.BaseEnum;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PrinterInventaire {

    private static final int BLUETOOTH_PERMISSION = 3;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog_wait_connecte;
    private final String PREFS = "ALL_PREFS";
    private Activity mActivity;

    SharedPreferences prefs;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> pairedDeviceList;

    private RTPrinter rtPrinter;
    private final ArrayList<PrinterInterface> printerInterfaceArrayList = new ArrayList<>();
    private final PrinterInterface curPrinterInterface = null;

    private String printStr;
    private PrinterFactory printerFactory;
    private TextSetting textSetting;
    private final String mChartsetName = "UTF-8";
    private Object configObj;
    private final ESCFontTypeEnum curESCFontType = null;
    private ArrayList<PostData_Inv2> final_panier;
    private PostData_Produit produit;
    private PostData_Inv1 inv1;
    private DATABASE controller;

    public void start_print_inv(Activity activity, ArrayList<PostData_Inv2> final_panier, PostData_Inv1 inv1)  throws UnsupportedEncodingException {

        mActivity = activity;
        this.final_panier = final_panier;
        this.inv1 = inv1;

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }

        }

        controller =  new DATABASE(mActivity);

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();


        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if(Objects.equals(prefs.getString("PRINTER", "BLUETOOTH"), "BLUETOOTH")){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;

            Log.v("PRINTER", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for(int i = 0; i< pairedDeviceList.size() ; i++){
                if(pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))){
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }

            if(isfound){
                Log.v("PRINTER", "Device found");
                if(device != null){
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            }else {
                Log.v("PRINTER", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        }else if(Objects.equals(prefs.getString("PRINTER", "BLUETOOTH"), "WIFI")){

            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1") , Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }

    private final class LongOperation extends AsyncTask<Void, Void, Boolean> {
        BluetoothEdrConfigBean bluetoothEdrConfigBean;
        WiFiConfigBean wiFiConfigBean;
        ProgressDialog mProgressDialog;

        public LongOperation(BluetoothEdrConfigBean bluetoothEdrConfigBean) {
            this.bluetoothEdrConfigBean = bluetoothEdrConfigBean;
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Preparation...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        public LongOperation(WiFiConfigBean wiFiConfigBean) {
            this.wiFiConfigBean = wiFiConfigBean;
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Preparation...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {



                if(!BaseApplication.getInstance().getIsConnected()){

                    if(Objects.equals(prefs.getString("PRINTER", "BLUETOOTH"), "BLUETOOTH")){
                        PIFactory piFactory = new BluetoothFactory();
                        PrinterInterface printerInterface = piFactory.create();
                        printerInterface.setConfigObject(bluetoothEdrConfigBean);
                        rtPrinter.setPrinterInterface(printerInterface);
                        rtPrinter.connect(bluetoothEdrConfigBean);

                    }else if(Objects.equals(prefs.getString("PRINTER", "BLUETOOTH"), "WIFI")){

                        PIFactory piFactory = new WiFiFactory();
                        PrinterInterface printerInterface = piFactory.create();
                        printerInterface.setConfigObject(wiFiConfigBean);
                        rtPrinter.setPrinterInterface(printerInterface);
                        rtPrinter.connect(wiFiConfigBean);
                    }

                    Thread.sleep(1500);
                }else {
                    rtPrinter = BaseApplication.getInstance().getRtPrinter();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.hide();
            if(result){
                try {
                    print_bon();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void print_bon()  throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                BitmapSetting bitmapSetting = new BitmapSetting();
                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);
                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);

                try {

                    Bitmap mBitmap = null;
                    String preBlank = "        ";

                    String img_str= prefs.getString("COMPANY_LOGO", "");
                    if (!img_str.equals("")){
                        //decode string to image
                        String base = img_str;
                        byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
                        mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap));
                        cmd.append(cmd.getLFCRCmd());
                    }


                    /*if (bmpPrintWidth > 72) {
                        bmpPrintWidth = 72;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                et_pic_width.setText(bmpPrintWidth + "");
                            }
                        });
                    }
*/
                   // bitmapSetting.setBimtapLimitWidth(40 * 8);


                    //textSetting.setIsEscSmallCharactor(SettingEnum.Enable);
                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    if(!prefs.getString("ACTIVITY_NAME", "").equals("")){
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("ACTIVITY_NAME", "")));
                    }
                    if(!prefs.getString("COMPANY_ADRESSE", "").equals("")){
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    }
                    if(!prefs.getString("COMPANY_TEL", "").equals("")){
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_TEL", "")));
                    }
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    ////////////////////////////////////// INFO CLIENT ///////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_RIGHT);
                    cmd.append(cmd.getTextCmd(textSetting, "Date :" + inv1.date_inv + " " + inv1.heure_inv));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);

                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "CLIENT  :" + inv1.nom_inv));
                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //cmd.append(cmd.getTextCmd(textSetting, "ADRESSE :" + inv1.adresse));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                   // cmd.append(cmd.getTextCmd(textSetting, "TEL     :" + bon1.tel));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "INVENTAIRE N :" + inv1.num_inv));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    //cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    textSetting.setBold(SettingEnum.Enable);
                    String format0 = "%1$-19s %2$-9s %3$-5s %4$12s";
                    cmd.append(cmd.getTextCmd(textSetting,  String.format(format0, "PRODUIT" ,  StringUtils.center("QTE",9) , StringUtils.center("VRAC",5) , "P.U"   )));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    Double nbr_colis,colissage,qte,gte_gratuit,prix_unit;
                    String nbr_colis_Str,colissage_Str,qte_Str,gte_gratuit_Str,prix_unit_Str,X1_Str ,X2_Str, eq1_Str, plus_Str  ;
                    //nbr_colis = 10.0; colissage =23.0 ; qte = 120.00 ; gte_gratuit = 1.0;  prix_unit = 12345.33 ;


                    for(int i=0; i< final_panier.size() ; i++ ){


                        cmd.append(cmd.getTextCmd(textSetting, final_panier.get(i).produit));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        nbr_colis = final_panier.get(i).nbr_colis;
                        nbr_colis_Str   =  new DecimalFormat("####0.##").format(nbr_colis);

                        colissage = final_panier.get(i).colissage;
                        colissage_Str   =  new DecimalFormat("####0.##").format(colissage);

                        qte = final_panier.get(i).qte_physique;
                        qte_Str         =  new DecimalFormat("####0.##").format(qte);

                        gte_gratuit = final_panier.get(i).vrac;
                        gte_gratuit_Str =  new DecimalFormat( "####0.##").format(gte_gratuit);

                        prix_unit = final_panier.get(i).pa_ht;
                        prix_unit_Str   =  new DecimalFormat("####0.00").format(prix_unit);


                        X1_Str = "X";
                        eq1_Str = "=";
                        plus_Str = "+";

                        if (gte_gratuit == 0.0) {
                            gte_gratuit_Str="";
                            plus_Str = " ";
                        }

                        if (  colissage == 0.0 ) {  nbr_colis_Str ="";  colissage_Str = "";  X1_Str = " "; eq1_Str = " "; }
                        String format1 = "  %1$-6s "+X1_Str+" %2$-6s "+eq1_Str+" %3$-9s"+plus_Str+"%4$-5sX%5$12s";
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format1, StringUtils.center(nbr_colis_Str,6), StringUtils.center(colissage_Str,6), StringUtils.center(qte_Str,9) , StringUtils.center(gte_gratuit_Str,5) , prix_unit_Str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space


                    }
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    int nbr_produit;
                    String nbr_produit_str;

                    nbr_produit = final_panier.size();
                    nbr_produit_str =  new DecimalFormat( "####0.##").format(Double.valueOf(nbr_produit));
                    cmd.append(cmd.getTextCmd(textSetting, "NBR PRODUIT :" + nbr_produit_str));

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_FOOTER", "")));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////
                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setHeightInDot(50);//accept value:1~255
                    barcodeSetting.setBarcodeWidth(3);//accept value:2~6
                    barcodeSetting.setQrcodeDotSize(5);//accept value: Esc(1~15), Tsc(1~10);

                    try {
                        cmd.append(cmd.getBarcodeCmd(BarcodeType.CODE39, barcodeSetting, inv1.num_inv));
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }


                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////


                    //cmd.append(cmd.getLFCRCmd());  // one line space
                    //cmd.append(cmd.getLFCRCmd());  // one line space
                   // cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getHeaderCmd());//初始化, Initial
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (SdkException | IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }


    void print_etiquette()  throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));
                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    cmd.append(cmd.getLFCRCmd()); // one line space

                    Double prix_vente;
                    String prix_vente_str;

                    prix_vente = produit.pv1_ht * (1+(produit.tva/100));
                    prix_vente_str   =  new DecimalFormat("##,##0.00").format(prix_vente);

                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prix_vente_str + " DA"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////
                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setHeightInDot(100);//accept value:1~255
                    barcodeSetting.setBarcodeWidth(3);//accept value:2~6
                    barcodeSetting.setQrcodeDotSize(5);//accept value: Esc(1~15), Tsc(1~10);

                    try {
                        cmd.append(cmd.getBarcodeCmd(BarcodeType.CODE39, barcodeSetting, produit.code_barre));
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }


                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////


                    //cmd.append(cmd.getLFCRCmd());  // one line space
                    //cmd.append(cmd.getLFCRCmd());  // one line space
                    // cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getHeaderCmd());//初始化, Initial
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }

    static class StringUtils {

        public static String center(String s, int size) {
            return center(s, size, ' ');
        }

        public static String center(String s, int size, char pad) {
            if (s == null || size <= s.length())
                return s;

            StringBuilder sb = new StringBuilder(size);
            for (int i = 0; i < (size - s.length()) / 2; i++) {
                sb.append(pad);
            }
            sb.append(s);
            while (sb.length() < size) {
                sb.append(pad);
            }
            return sb.toString();
        }
    }

    public void showProgressDialog(final String str){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog == null){
                    progressDialog = new ProgressDialog( mActivity);
                }
                if(!TextUtils.isEmpty(str)){
                    progressDialog.setMessage(str);
                }else{
                    progressDialog.setMessage("Impression...");
                }
                progressDialog.show();
            }
        });

    }

    public void hideProgressDialog(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

    }
}
