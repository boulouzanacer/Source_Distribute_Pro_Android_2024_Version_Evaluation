package com.safesoft.proapp.distribute.activities.product;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.safesoft.proapp.distribute.activities.pdf.PdfViewerActivity;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterProduits;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.ProductEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditProduct;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityProduits extends AppCompatActivity implements RecyclerAdapterProduits.ItemClick, RecyclerAdapterProduits.ItemLongClick {

    private RecyclerView recyclerView;
    private RecyclerAdapterProduits adapter;
    public static ArrayList<PostData_Produit> produits;
    private DATABASE controller;
    public static final String BARCODE_KEY = "BARCODE";
    private SearchView searchView;
    private TextView nbr_produit, total_prix;
    private AutoCompleteTextView famille_dropdown;
    private String selected_famile = "Toutes";
    private EventBus bus;
    private FragmentNewEditProduct fragmentnewproduct;
    private SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";
    private boolean hide_stock_moins = true;
    private boolean show_picture_prod = false;
    private boolean is_scan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_produits);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityProduits.this, restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
            }
        }

        //toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        // setSupportActionBar(toolbar);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("List Produits");
        }

        controller = new DATABASE(this);
        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        hide_stock_moins = prefs.getBoolean("AFFICHAGE_STOCK_MOINS", false);
        show_picture_prod = prefs.getBoolean("SHOW_PROD_PIC", false);
        initViews();

        setRecycle("", false);

    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_produit);
        nbr_produit = findViewById(R.id.list_produit_nbr_produit);
        total_prix = findViewById(R.id.list_produit_total);
        famille_dropdown = findViewById(R.id.famille_dropdown);

        ArrayList<String> familles = new ArrayList<>();
        familles = controller.select_familles_from_database("SELECT * FROM FAMILLES");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.dropdown_famille_item, familles);
        famille_dropdown.setAdapter(adapter);

        famille_dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_famile = (String) adapterView.getItemAtPosition(i);
                if (selected_famile.equals("<Aucune>")) {
                    selected_famile = "";
                }
                setRecycle("", false);
            }
        });
    }

    private void setRecycle(String text_search, boolean isscan) {
        try {

            PostData_Params params;
            params = controller.select_params_from_database("SELECT * FROM PARAMS");
            String prix_revendeur = prefs.getString("PRIX_REVENDEUR", "Libre");

            if (isscan) {
                is_scan = true;
                searchView.setQuery(text_search, false);
                is_scan = false;
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerAdapterProduits(this, getItems(text_search, isscan), params, prix_revendeur);
            recyclerView.setAdapter(adapter);

            nbr_produit.setText("Nombre de produit : " + produits.size());
            if (prefs.getBoolean("AFFICHAGE_PA_HT", false)) {
                total_prix.setText("Total achats : " + new DecimalFormat("##,##0.00").format(calcule_total()) + " DA");
                total_prix.setVisibility(View.VISIBLE);
            } else {
                total_prix.setVisibility(View.GONE);
            }

        }catch (Exception e){
            new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Erreur. !")
                    .setContentText("" + e.getMessage())
                    .show();
        }

    }

    private double calcule_total() {
        double total = 0;
        for (int i = 0; i < produits.size(); i++) {
            if (produits.get(i).stock > 0) {
                total = total + (produits.get(i).stock * produits.get(i).pamp);
            }
        }
        return total;
    }

    public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {

        ///////////////////////////////////CODE BARRE //////////////////////////////////////
        ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

        String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN FROM CODEBARRE WHERE CODE_BARRE != '" + querry_search + "' AND CODE_BARRE_SYN = '" + querry_search + "' ";
        codebarres = controller.select_all_codebarre_from_database(querry_codebarre);
        if(!codebarres.isEmpty()){
            querry_search = codebarres.get(0).code_barre;
        }
        ///////////////////////////////////CODE BARRE //////////////////////////////////////

        String[] words = querry_search.split(" ");
        StringBuilder queryPattern = new StringBuilder();

        for (String word : words) {
                queryPattern.append(word).append("% "); // Add 3-letter prefix with wildcard
        }

        // Trim the trailing space and create the LIKE pattern
        String pattern = queryPattern.toString().trim();

        StringBuilder querry = new StringBuilder();

        if(show_picture_prod){
            // Initialize StringBuilder for dynamic query construction
            querry = new StringBuilder("SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS, DESTOCK_CODE_BARRE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT ");
        }else{
            // Initialize StringBuilder for dynamic query construction
            querry = new StringBuilder("SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, STOCK_INI, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS, DESTOCK_CODE_BARRE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT ");
        }


        // List to hold query conditions
        List<String> conditions = new ArrayList<>();

        // Add condition for selected family
        if (!selected_famile.equals("Toutes")) {
            conditions.add("FAMILLE = '" + selected_famile + "'");
        }

        // Add conditions based on hide_stock_moins and isScan
        if (hide_stock_moins) {
            if (isScan) {
                conditions.add("(CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "') AND STOCK > 0");
            } else if (!querry_search.isEmpty()) {
                conditions.add("(PRODUIT LIKE '%" + pattern + "' OR CODE_BARRE LIKE '%" + pattern + "' OR REF_PRODUIT LIKE '%" + pattern + "') AND STOCK > 0");
            } else {
                conditions.add("STOCK > 0");
            }
        } else {
            if (isScan) {
                conditions.add("(CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "')");
            } else if (!querry_search.isEmpty()) {
                conditions.add("(PRODUIT LIKE '%" + pattern + "' OR CODE_BARRE LIKE '%" + pattern + "' OR REF_PRODUIT LIKE '%" + pattern + "')");
            }
        }

        // Append WHERE clause if there are conditions
        if (!conditions.isEmpty()) {
            querry.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // Append ORDER BY clause
        querry.append(" ORDER BY PRODUIT");

        // Execute the constructed query
        produits = controller.select_produits_from_database(querry.toString(), show_picture_prod);

        return produits;
    }

    @Subscribe
    public void onProductAdded(ProductEvent productEvent) {
        setRecycle("", false);
    }

    @Override
    public void onClick(View v, int position) {
        Intent intent = new Intent(ActivityProduits.this, ActivityProduitDetail.class);

        intent.putExtra("CODE_BARRE", produits.get(position).code_barre);
        intent.putExtra("REF_PRODUIT", produits.get(position).ref_produit);
        intent.putExtra("PRODUIT", produits.get(position).produit);
        intent.putExtra("PA_HT", produits.get(position).pa_ht);
        intent.putExtra("TVA", produits.get(position).tva);
        intent.putExtra("PAMP", produits.get(position).pamp);
        intent.putExtra("PV1_HT", produits.get(position).pv1_ht);
        intent.putExtra("PV2_HT", produits.get(position).pv2_ht);
        intent.putExtra("PV3_HT", produits.get(position).pv3_ht);
        intent.putExtra("PV4_HT", produits.get(position).pv4_ht);
        intent.putExtra("PV5_HT", produits.get(position).pv5_ht);
        intent.putExtra("PV6_HT", produits.get(position).pv6_ht);
        intent.putExtra("STOCK", produits.get(position).stock);
        intent.putExtra("COLISSAGE", produits.get(position).colissage);
        intent.putExtra("STOCK_INI", produits.get(position).stock_ini);
        intent.putExtra("STOCK_COLIS", produits.get(position).stock_colis);
        intent.putExtra("STOCK_VRAC", produits.get(position).stock_vrac);
        intent.putExtra("PHOTO", produits.get(position).photo);
        intent.putExtra("DESCRIPTION", produits.get(position).description);

        intent.putExtra("PROMO", produits.get(position).promo);
        intent.putExtra("D1", produits.get(position).d1);
        intent.putExtra("D2", produits.get(position).d2);
        intent.putExtra("PP1_HT", produits.get(position).pp1_ht);
        intent.putExtra("QTE_PROMO", produits.get(position).qte_promo);
        intent.putExtra("POSITION_ITEM", position);



        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onLongClick(View v, int position) {
        final CharSequence[] items = {"Modifier", "Supprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setIcon(R.drawable.blue_circle_24);
        builder.setTitle("Choisissez une action");
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
                case 0 -> {

                    new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Modification")
                            .setContentText("Voulez-vous vraiment modifier le produit :  " + produits.get(position).produit + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Modifier")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                if (fragmentnewproduct == null)
                                    fragmentnewproduct = new FragmentNewEditProduct();

                                fragmentnewproduct.showDialogbox(ActivityProduits.this, "EDIT_PRODUCT", produits.get(position));
                                sDialog.dismiss();

                            }).show();
                }
                case 1 -> {
                    if (produits.get(position).isNew == 1) {
                        ///// delete product
                        String querry_has_bon2 = "SELECT BON2.CODE_BARRE FROM BON2 LEFT JOIN BON1 ON BON1.NUM_BON == BON2.NUM_BON WHERE BON1.IS_EXPORTED = 0 AND BON2.CODE_BARRE = '" + produits.get(position).code_barre + "'";
                        String querry_has_bon2_temp = "SELECT BON2_TEMP.CODE_BARRE FROM BON2_TEMP LEFT JOIN BON1_TEMP ON BON1_TEMP.NUM_BON == BON2_TEMP.NUM_BON WHERE BON1_TEMP.IS_EXPORTED = 0 AND BON2_TEMP.CODE_BARRE = '" + produits.get(position).code_barre + "'";
                        String querry_has_achat2 = "SELECT ACHAT2.CODE_BARRE FROM ACHAT2 LEFT JOIN ACHAT1 ON ACHAT1.NUM_BON == ACHAT2.NUM_BON WHERE ACHAT1.IS_EXPORTED = 0 AND ACHAT2.CODE_BARRE = '" + produits.get(position).code_barre + "'";

                        if (controller.check_if_has_bon(querry_has_bon2) || controller.check_if_has_bon(querry_has_bon2_temp) || controller.check_if_has_bon(querry_has_achat2)) {
                            // you can't delete this client
                            new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Il exist des bons créer avec ce produit, Suppression impossible")
                                    .show();
                        } else {
                            new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Suppression")
                                    .setContentText("Voulez-vous vraiment supprimer le produit :  " + produits.get(position).produit + " ?!")
                                    .setCancelText("Anuuler")
                                    .setConfirmText("Supprimer")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        controller.delete_produit(produits.get(position).code_barre);

                                        setRecycle("", false);
                                        sDialog.dismiss();

                                    }).show();

                        }
                    } else {
                        new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Produit exist sur le serveur, Suppression impossible")
                                .show();
                    }

                }
            }
        });
        builder.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        searchView = new SearchView(Objects.requireNonNull(getSupportActionBar()).getThemedContext());

        menu.add(Menu.NONE, Menu.NONE, 0, "Rechercher")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        searchView.setQueryHint("Rechercher");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_products, menu);

        //////////////////////////////////////////////////////////////////////
        ///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
        //////////////////////////////////////////////////////////////////////


        // final Context cntx = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!is_scan){
                    setRecycle(newText, false);
                }

                return false;
            }


            @Override
            public boolean onQueryTextSubmit(final String query) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                //Toast.makeText(getBaseContext(), "dummy Search", Toast.LENGTH_SHORT).show();
                setProgressBarIndeterminateVisibility(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //=======
                        setProgressBarIndeterminateVisibility(false);
                    }
                }, 2000);

                return false;
            }
        });

        // searchView.setIconified(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.scan) {

            startScan();

        } else if (item.getItemId() == R.id.new_product) {
            if (fragmentnewproduct == null)
                fragmentnewproduct = new FragmentNewEditProduct();

            fragmentnewproduct.showDialogbox(ActivityProduits.this, "NEW_PRODUCT", null);
        }else if (item.getItemId() == R.id.etat_stock) {

            ArrayList<PostData_Produit> produitsWithStock = new ArrayList<>();
            for (PostData_Produit p : produits) {
                // stock could be calculated as p.stock, or from (stock_ini, stock_colis, stock_vrac)
                if (p.stock != 0) {
                    produitsWithStock.add(p);
                }
            }

            generatePDF(produitsWithStock);
        }
        return super.onOptionsItemSelected(item);
    }

    private void generatePDF(ArrayList<PostData_Produit> productList) {
        try {

            // Prepare file
            File docsFolder = new File(getExternalFilesDir(null), "documents");
            if (!docsFolder.exists()) docsFolder.mkdirs();
            File pdfFile = new File(docsFolder, "receipt_invoice.pdf");

            // Document
            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Load Arabic-supporting font from assets (identity-H for unicode)
            String fontPath = "assets/fonts/NotoNaskhArabic-VariableFont.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font normal_bold_Font = new Font(baseFont, 12, Font.BOLD);
            Font smallFont = new Font(baseFont, 10, Font.NORMAL);

            // Header: Company name (example) - you can change it
            Paragraph company = new Paragraph("Etat de stock", titleFont);
            company.setAlignment(Element.ALIGN_CENTER);
            document.add(company);

            // Invoice meta: number + date
            String depotNumber = prefs.getString("NOM_DEPOT", "DEPOT");
            String famille = selected_famile;
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            String nbrProduitStr = String.valueOf(productList.size());

            // Create a two-column table for meta (right = Arabic side)
            PdfPTable metaTable = new PdfPTable(new float[]{1f, 2f});
            metaTable.setWidthPercentage(60f);
            metaTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            metaTable.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);

            PdfPCell depotLabel = new PdfPCell(new Phrase("DEPOT : ", normalFont));
            depotLabel.setBorder(PdfPCell.NO_BORDER);
            depotLabel.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            PdfPCell depotValue = new PdfPCell(new Phrase(depotNumber, normalFont));
            depotValue.setBorder(PdfPCell.NO_BORDER);
            depotValue.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);

            PdfPCell familleLabel = new PdfPCell(new Phrase("Famille : ", normalFont));
            familleLabel.setBorder(PdfPCell.NO_BORDER);
            familleLabel.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            PdfPCell familleValue = new PdfPCell(new Phrase(famille, normalFont));
            familleValue.setBorder(PdfPCell.NO_BORDER);
            familleValue.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);

            PdfPCell dateLabel = new PdfPCell(new Phrase("Date : ", normalFont));
            dateLabel.setBorder(PdfPCell.NO_BORDER);
            dateLabel.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            PdfPCell dateValue = new PdfPCell(new Phrase(dateStr, normalFont));
            dateValue.setBorder(PdfPCell.NO_BORDER);
            dateValue.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);


            PdfPCell nbrProduitLabel = new PdfPCell(new Phrase("Nbre produit : ", normalFont));
            nbrProduitLabel.setBorder(PdfPCell.NO_BORDER);
            nbrProduitLabel.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);

            PdfPCell nbrProduitValue = new PdfPCell(new Phrase(nbrProduitStr, normalFont));
            nbrProduitValue.setBorder(PdfPCell.NO_BORDER);
            nbrProduitValue.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);

            metaTable.addCell(depotLabel);
            metaTable.addCell(depotValue);

            metaTable.addCell(familleLabel);
            metaTable.addCell(familleValue);

            metaTable.addCell(dateLabel);
            metaTable.addCell(dateValue);

            metaTable.addCell(nbrProduitLabel);
            metaTable.addCell(nbrProduitValue);

            document.add(metaTable);

            // Spacer
            document.add(new Paragraph("\n"));

            // Product table header: Product | Qty | Achat | Vente | Total Vente (per row) | Total Achat (per row)
            PdfPTable table = new PdfPTable(new float[]{7f, 2f, 1.4f});
            table.setWidthPercentage(100f);
            table.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            table.setHeaderRows(1);

            // Header cells (Arabic)
            PdfPCell hProduct = createHeaderCell("Produit", normalFont);
            PdfPCell hStock = createHeaderCell("stock", normalFont);
            //PdfPCell hAchat = createHeaderCell("achat", normalFont);
            PdfPCell hVente = createHeaderCell("prix", normalFont);
            //PdfPCell hTotalVente = createHeaderCell("إجمالي البيع", normalFont);
            //PdfPCell hTotalAchat = createHeaderCell("إجمالي الشراء", normalFont);

            table.addCell(hProduct);
            table.addCell(hStock);
            //table.addCell(hAchat);
            table.addCell(hVente);
            //table.addCell(hTotalVente);
            //table.addCell(hTotalAchat);

            // Totals accumulation (use double; formatted when displayed)
            double totalAchat = 0.0;
            double totalVente = 0.0;
            double totalStock = 0.0;

            // Add product rows
            for(int i = 0; i < productList.size(); i++) {
                PostData_Produit p = productList.get(i);
                // Product cell (mixed Arabic + English permitted)
                PdfPCell nameCell = new PdfPCell(new Phrase(p.produit, normalFont));
                nameCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
                nameCell.setPadding(6f);
                table.addCell(nameCell);

                // Qty
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(p.stock), normalFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qtyCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
                table.addCell(qtyCell);

                // Achat price per unit
                /*PdfPCell achatCell = new PdfPCell(new Phrase(String.format(Locale.US, "%.2f", p.pa_ttc), normalFont));
                achatCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                achatCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
                table.addCell(achatCell);*/

                // Vente price per unit
                double prix1_ttc = p.pv1_ht * (1 + p.tva / 100);
                PdfPCell venteCell = new PdfPCell(new Phrase(String.format(Locale.US, "%.2f DA", prix1_ttc), normalFont));
                venteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                venteCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
                table.addCell(venteCell);

                // Total vente for this row = qty * ventePrice
                /*double rowVente = p.stock * p.pv1_ttc;
                PdfPCell rowVenteCell = new PdfPCell(new Phrase(String.format(Locale.US, "%.2f دج", rowVente), normalFont));
                rowVenteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                rowVenteCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
                table.addCell(rowVenteCell);

                // Total achat for this row = qty * achatPrice
                double rowAchat = p.stock * p.pa_ttc;
                PdfPCell rowAchatCell = new PdfPCell(new Phrase(String.format(Locale.US, "%.2f دج", rowAchat), normalFont));
                rowAchatCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                rowAchatCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
                table.addCell(rowAchatCell);*/

            }



            for(int i = 0; i < productList.size(); i++) {
                double rowVente = (productList.get(i).pv1_ht * (1 + productList.get(i).tva / 100)) * productList.get(i).stock;
                double rowAchat = productList.get(i).pamp * productList.get(i).stock;
                double rowStock = productList.get(i).stock;
                totalVente += rowVente;
                totalAchat += rowAchat;
                totalStock += rowStock;
            }

            /// add one ligne for total stock
            //------------------------------------------------------------------------------------

            // TOTAL STOCK title
            PdfPCell nameCell = new PdfPCell(new Phrase("TOTAL STOCK", normal_bold_Font));
            nameCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            nameCell.setPadding(6f);
            table.addCell(nameCell);

            // total stock
            PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(totalStock), normal_bold_Font));
            qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qtyCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            table.addCell(qtyCell);

            // -
            PdfPCell venteCell = new PdfPCell(new Phrase(String.format(Locale.US, "%.2s", "-"), normal_bold_Font));
            venteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            venteCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            table.addCell(venteCell);

            //-------------------------------------------------------------------------------------


            // Add product table to document
            document.add(table);

            // Spacer
            document.add(new Paragraph("\n"));

            // Totals table (right aligned)
            PdfPTable totalsTable = new PdfPTable(new float[]{2f, 1f});
            totalsTable.setWidthPercentage(50f);
            totalsTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalsTable.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);


            PdfPCell totalAchatLabel = new PdfPCell(new Phrase("Total (achat):", normalFont));
            totalAchatLabel.setBorder(PdfPCell.NO_BORDER);
            totalAchatLabel.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            PdfPCell totalAchatValue = new PdfPCell(new Phrase(String.format(Locale.US, "%.2f DA", totalAchat), normalFont));
            totalAchatValue.setBorder(PdfPCell.NO_BORDER);
            totalAchatValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalAchatValue.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);


            PdfPCell totalVenteLabel = new PdfPCell(new Phrase("Total (vente):", normalFont));
            totalVenteLabel.setBorder(PdfPCell.NO_BORDER);
            totalVenteLabel.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            PdfPCell totalVenteValue = new PdfPCell(new Phrase(String.format(Locale.US, "%.2f DA", totalVente), normalFont));
            totalVenteValue.setBorder(PdfPCell.NO_BORDER);
            totalVenteValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalVenteValue.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);


            if (prefs.getBoolean("SHOW_ACHAT_CLIENT", false)) {
                totalsTable.addCell(totalAchatLabel);
                totalsTable.addCell(totalAchatValue);
            }
            totalsTable.addCell(totalVenteLabel);
            totalsTable.addCell(totalVenteValue);


            document.add(totalsTable);

            // Spacer
            document.add(new Paragraph("\n"));

            // Barcode (Code128) for invoice number
            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(depotNumber);
            barcode128.setCodeType(Barcode128.CODE128);
            Image barcodeImage = barcode128.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
            barcodeImage.scaleToFit(250f, 60f); // scale as needed
            // center barcode
            PdfPTable barcodeTable = new PdfPTable(1);
            barcodeTable.setWidthPercentage(100f);
            PdfPCell bcCell = new PdfPCell(barcodeImage, false);
            bcCell.setBorder(PdfPCell.NO_BORDER);
            bcCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bcCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            barcodeTable.addCell(bcCell);
            document.add(barcodeTable);

            // QR code with invoice summary
            // Build QR content (compact but informative)
            StringBuilder itemsSummary = new StringBuilder();

            for(int i = 0; i < productList.size(); i++) {
                PostData_Produit p = productList.get(i);
                // Escape semicolons in names if present
                String safeName = p.produit.replace(";", ",");
                itemsSummary.append(String.format(Locale.US, "%s|%f|%.2f;", safeName, p.stock, p.pv1_ttc));
            }

            String qrContent = String.format(Locale.US, "INVOICE:%s;DATE:%s;TOTAL_ACHAT:%.2f;TOTAL_VENTE:%.2f;ITEMS:%s", depotNumber, dateStr, totalAchat, totalVente, itemsSummary.toString());

            BarcodeQRCode barcodeQRCode = new BarcodeQRCode(qrContent, 200, 200, null);
            Image qrImage = barcodeQRCode.getImage();
            qrImage.scaleToFit(120f, 120f);

            PdfPTable qrTable = new PdfPTable(1);
            qrTable.setWidthPercentage(100f);
            PdfPCell qrCell = new PdfPCell(qrImage, false);
            qrCell.setBorder(PdfPCell.NO_BORDER);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
            qrTable.addCell(qrCell);

            // Add small label under QR
            PdfPCell qrLabelCell = new PdfPCell(new Phrase("رمز الاستجابة السريعة (QR) - Invoice QR", smallFont));
            qrLabelCell.setBorder(PdfPCell.NO_BORDER);
            qrLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrLabelCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            qrTable.addCell(qrLabelCell);

            document.add(qrTable);

            document.close();

            //Toast.makeText(this, "PDF created: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Open viewer
            openPdfViewer(pdfFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(230, 230, 230));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6f);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        return cell;
    }

    private void openPdfViewer(File file) {
        Intent intent = new Intent(this, PdfViewerActivity.class);
        intent.putExtra("path", file.getAbsolutePath());
        intent.putExtra("selected_famile", selected_famile);
        startActivity(intent);
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ActivityProduits.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        // result.setText(barcode.rawValue);
                        // Toast.makeText(ActivityProduits.this, ""+barcode.rawValue, Toast.LENGTH_SHORT).show();
                        // Do search after barcode scanned
                        setRecycle(barcode.rawValue, true);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3000) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    assert imageBitmap != null;
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100 /* Ignored for PNGs */, blob);
                    byte[] inputData = blob.toByteArray();
                    fragmentnewproduct.setImageFromActivity(inputData);
                }
            }
        } else if (requestCode == 4000) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    InputStream iStream;
                    try {
                        iStream = getContentResolver().openInputStream(selectedImage);
                        byte[] inputData = ImageUtils.getBytes(iStream);
                        fragmentnewproduct.setImageFromActivity(inputData);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "An error occured!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


}
