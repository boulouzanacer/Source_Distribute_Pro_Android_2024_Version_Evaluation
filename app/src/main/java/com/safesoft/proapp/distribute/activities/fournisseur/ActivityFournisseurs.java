package com.safesoft.proapp.distribute.activities.fournisseur;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterFournisseurs;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedFournisseurEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditFournisseur;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityFournisseurs extends AppCompatActivity implements RecyclerAdapterFournisseurs.ItemClick, RecyclerAdapterFournisseurs.ItemLongClick {

    RecyclerView recyclerView;
    private RecyclerAdapterFournisseurs adapter;
    private ArrayList<PostData_Fournisseur> fournisseurs;
    private DATABASE controller;
    private EventBus bus;
    private android.view.View layout_kpi_versement;
    private TextView kpi_total_versement;
    private TextView kpi_total_solde;
    private TextView chip_jour;
    private TextView chip_semaine;
    private TextView chip_mois;
    private int selectedPeriode = 0;
    private final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;

    private final String[] NEED_PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private final List<String> NO_PERMISSION = new ArrayList<String>();

    private void CheckAllPermission() {
        NO_PERMISSION.clear();
        for (String s : NEED_PERMISSION) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                NO_PERMISSION.add(s);
            }
        }
        if (NO_PERMISSION.isEmpty()) {

        } else {
            requestPermissions(NO_PERMISSION.toArray(new String[0]), 3232);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fournisseurs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            //  WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }

        CheckAllPermission();

        controller = new DATABASE(this);
        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Liste Fournisseurs");
            getSupportActionBar().setSubtitle("0 fournisseur");
        }

        initViews();
    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_client);
        layout_kpi_versement = findViewById(R.id.layout_kpi_versement);
        kpi_total_versement = findViewById(R.id.kpi_total_versement);
        kpi_total_solde = findViewById(R.id.kpi_total_solde);

        Runnable goVersed = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityFournisseurs.this, ActivityVersedFournisseur.class);
                intent.putExtra(ActivityVersedFournisseur.EXTRA_PERIODE, selectedPeriode);
                startActivity(intent);
            }
        };

        try {
            if (layout_kpi_versement != null) {
                layout_kpi_versement.setClickable(true);
                layout_kpi_versement.setFocusable(true);
                layout_kpi_versement.setBackgroundResource(R.drawable.bg_web_section_card);
                try {
                    int[] attrs = new int[]{android.R.attr.selectableItemBackground};
                    android.content.res.TypedArray ta = obtainStyledAttributes(attrs);
                    android.graphics.drawable.Drawable d = ta.getDrawable(0);
                    if (d != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            layout_kpi_versement.setForeground(d);
                        }
                    }
                    ta.recycle();
                } catch (Exception ignore) {}
                layout_kpi_versement.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        goVersed.run();
                    }
                });
            }
        } catch (Exception ignore) {}

        try {
            if (kpi_total_versement != null) {
                kpi_total_versement.setClickable(true);
                kpi_total_versement.setFocusable(false);
                kpi_total_versement.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        goVersed.run();
                    }
                });
            }
        } catch (Exception ignore) {}

        chip_jour = findViewById(R.id.chip_jour);
        chip_semaine = findViewById(R.id.chip_semaine);
        chip_mois = findViewById(R.id.chip_mois);

        if (chip_jour != null) chip_jour.setOnClickListener(v -> selectPeriode(0));
        if (chip_semaine != null) chip_semaine.setOnClickListener(v -> selectPeriode(1));
        if (chip_mois != null) chip_mois.setOnClickListener(v -> selectPeriode(2));
        applyChipVisuals(selectedPeriode);
    }

    private void selectPeriode(int periode) {
        selectedPeriode = periode;
        applyChipVisuals(periode);
        refreshKpisPeriode();
    }

    private void applyChipVisuals(int periode) {
        if (chip_jour != null) {
            chip_jour.setBackgroundResource(periode == 0 ? R.drawable.bg_chip_segmented_selected : R.drawable.bg_chip_segmented_unselected);
            chip_jour.setTextColor(periode == 0 ? 0xFF0F172A : 0xFF64748B);
            chip_jour.setTypeface(null, periode == 0 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
        if (chip_semaine != null) {
            chip_semaine.setBackgroundResource(periode == 1 ? R.drawable.bg_chip_segmented_selected : R.drawable.bg_chip_segmented_unselected);
            chip_semaine.setTextColor(periode == 1 ? 0xFF0F172A : 0xFF64748B);
            chip_semaine.setTypeface(null, periode == 1 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
        if (chip_mois != null) {
            chip_mois.setBackgroundResource(periode == 2 ? R.drawable.bg_chip_segmented_selected : R.drawable.bg_chip_segmented_unselected);
            chip_mois.setTextColor(periode == 2 ? 0xFF0F172A : 0xFF64748B);
            chip_mois.setTypeface(null, periode == 2 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
    }

    private String[] computeDateBounds(int periode) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        Calendar today = Calendar.getInstance(Locale.FRANCE);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar start = Calendar.getInstance(Locale.FRANCE);
        start.setTime(today.getTime());
        if (periode == 1) start.add(Calendar.DAY_OF_YEAR, -6);
        else if (periode == 2) start.add(Calendar.DAY_OF_YEAR, -29);
        return new String[]{sdf.format(start.getTime()), sdf.format(today.getTime())};
    }

    @Override
    protected void onResume() {

        setRecycle("");

        super.onResume();
    }

    public void refreshAfterVersement() {
        setRecycle("");
    }

    private void setRecycle(String text_search) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterFournisseurs(this, getItems(text_search));
        recyclerView.setAdapter(adapter);
        int count = fournisseurs.size();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(String.format(Locale.FRANCE, "%,d fournisseur%s", count, (count > 1 ? "s" : "")));
        }
        refreshKpisPeriode();
    }

    private void refreshKpisPeriode() {
        if (controller == null || kpi_total_versement == null || kpi_total_solde == null) return;
        String[] bounds = computeDateBounds(selectedPeriode);
        double totVersement = controller.getFournisseurTotalVersementsPeriode(bounds[0], bounds[1]);
        double totSolde = controller.getFournisseurTotalSoldesPeriode(bounds[0], bounds[1]);
        kpi_total_versement.setText(String.format(Locale.FRANCE, "%,.2f DA", totVersement));
        kpi_total_solde.setText(String.format(Locale.FRANCE, "%,.2f DA", totSolde));
    }

    public ArrayList<PostData_Fournisseur> getItems(String qqry) {

        // 🔥 Escape simple des apostrophes (cause principale de SQLiteException)
        qqry = (qqry == null) ? "" : qqry.replace("'", "''");

        if (qqry.length() > 0) {
            fournisseurs = new ArrayList<>();
            String querry = "SELECT * FROM FOURNIS WHERE CODE_FRS LIKE '%" + qqry + "%' OR FOURNIS LIKE '%" + qqry + "%' OR TEL LIKE '%" + qqry + "%' ORDER BY FOURNIS";
            // querry = "SELECT * FROM Events";
            fournisseurs = controller.select_fournisseurs_from_database(querry);
        } else {
            fournisseurs = new ArrayList<>();
            String querry = "SELECT * FROM FOURNIS ORDER BY FOURNIS";
            // querry = "SELECT * FROM Events";
            fournisseurs = controller.select_fournisseurs_from_database(querry);
        }

        return fournisseurs;
    }


    @Subscribe
    public void onFournisseurSelected(SelectedFournisseurEvent fournisseurEvent) {
        setRecycle("");
    }

    @Override
    public void onClick(View v, int position) {

        if (v.getId() == R.id.item_root) {
            Intent intent = new Intent(ActivityFournisseurs.this, ActivityFournisseurDetail.class);

            intent.putExtra("CODE_FRS", fournisseurs.get(position).code_frs);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    @Override
    public void onLongClick(View v, int position) {
        if (v.getId() == R.id.item_root) {
            final CharSequence[] items = {"Modifier", "Supprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0 -> {
                        FragmentNewEditFournisseur fragmentnewfournisseur = new FragmentNewEditFournisseur();
                        fragmentnewfournisseur.showDialogbox(ActivityFournisseurs.this, getBaseContext(), "EDIT_FOURNISSEUR", fournisseurs.get(position));
                    }
                    case 1 -> {
                        if (fournisseurs.get(position).isImported == 1) {
                            new SweetAlertDialog(ActivityFournisseurs.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Fournisseur importé depuis le serveur, Vous n'avez pas le droit de le supprimer !")
                                    .show();
                            return;
                        }

                        String querry_has_bon1 = "SELECT CODE_FRS FROM ACHAT1 WHERE IS_EXPORTED = 0 AND CODE_FRS = '" + fournisseurs.get(position).code_frs + "'";
                        String querry_has_bon1_temp = "SELECT CODE_FRS FROM ACHAT1_TEMP WHERE IS_EXPORTED = 0 AND CODE_FRS = '" + fournisseurs.get(position).code_frs + "'";

                        if (controller.check_if_has_bon(querry_has_bon1) || controller.check_if_has_bon(querry_has_bon1_temp)) {
                            // you can't delete this fournisseur
                            new SweetAlertDialog(ActivityFournisseurs.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Il exist des bons créer avec ce client")
                                    .show();
                        } else {
                            new SweetAlertDialog(ActivityFournisseurs.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Suppression")
                                    .setContentText("Voulez-vous vraiment supprimer le fournisseur " + fournisseurs.get(position).fournis + " ?!")
                                    .setCancelText("Anuuler")
                                    .setConfirmText("Supprimer")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        controller.delete_fournisseur(fournisseurs.get(position).code_frs);

                                        setRecycle("");
                                        sDialog.dismiss();

                                    }).show();

                        }

                    }
                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fournisseur, menu);

        final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Rechercher");

//////////////////////////////////////////////////////////////////////
///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
//////////////////////////////////////////////////////////////////////

        menu.add(Menu.NONE, Menu.NONE, 0, "Search")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // final Context cntx = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecycle(newText);

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(final String query) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                Toast.makeText(getBaseContext(), "dummy Search", Toast.LENGTH_SHORT).show();
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.new_fournisseur) {
            FragmentNewEditFournisseur fragmentnewfournisseur = new FragmentNewEditFournisseur();
            fragmentnewfournisseur.showDialogbox(ActivityFournisseurs.this, getBaseContext(), "NEW_FOURNISSEUR", null);
        }
        return super.onOptionsItemSelected(item);
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
}
