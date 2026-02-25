package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.media.MediaPlayer;
import android.widget.LinearLayout;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.achats.ActivityAchats;
import com.safesoft.proapp.distribute.activities.client.ActivityClients;
import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.activities.commande_achat.ActivityOrdersFournisseur;
import com.safesoft.proapp.distribute.activities.fournisseur.ActivityFournisseurs;
import com.safesoft.proapp.distribute.activities.inventaire.ActivityInventaires;
import com.safesoft.proapp.distribute.activities.login.ActivityLogin;
import com.safesoft.proapp.distribute.activities.product.ActivityProduits;
import com.safesoft.proapp.distribute.activities.commande_vente.ActivityOrdersClient;
import com.safesoft.proapp.distribute.activities.tournee_clients.ActivityTourneesClient;
import com.safesoft.proapp.distribute.activities.vente.ActivityVentes;
import com.safesoft.proapp.distribute.databases.DATABASE;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentMain extends Fragment implements View.OnClickListener {

    SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";
    String CODE_DEPOT, CODE_VENDEUR;
    View v;
    LinearLayout lnr_achat, lnr_vente_commande;
    DATABASE controller;
    private ImageButton BtnClient,
            BtnVente,
            BtnCommandeClient,
            BtnFournisseur,
            BtnAchat,
            BtnCommandeFournisseur,
            BtnProduit,
            BtnInventaire,
            BtnTournee,
            BtnImportExport,
            BtnParametre;

    public FragmentMain() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_main_distribute, container, false);

        initView(v);

        BtnClient.setOnClickListener(this);
        BtnVente.setOnClickListener(this);
        BtnCommandeClient.setOnClickListener(this);

        BtnFournisseur.setOnClickListener(this);
        BtnAchat.setOnClickListener(this);
        BtnCommandeFournisseur.setOnClickListener(this);

        BtnProduit.setOnClickListener(this);
        BtnInventaire.setOnClickListener(this);
        BtnTournee.setOnClickListener(this);

        BtnImportExport.setOnClickListener(this);
        BtnParametre.setOnClickListener(this);

        return v;
    }

    private void initView(View v) {

        lnr_achat = v.findViewById(R.id.lnr_achat);
        lnr_vente_commande = v.findViewById(R.id.lnr_vente_commande);

        BtnClient = v.findViewById(R.id.btn_clients);
        BtnVente = v.findViewById(R.id.btn_ventes);
        BtnCommandeClient = v.findViewById(R.id.btn_commande_client);

        BtnFournisseur = v.findViewById(R.id.btn_fournisseur);
        BtnAchat = v.findViewById(R.id.btn_achat);
        BtnCommandeFournisseur = v.findViewById(R.id.btn_commande_fournisseur);

        BtnProduit = v.findViewById(R.id.btn_produits);
        BtnInventaire = v.findViewById(R.id.btn_inventaire);
        BtnTournee = v.findViewById(R.id.btn_tournee);

        BtnImportExport = v.findViewById(R.id.btn_import_export);
        BtnParametre = v.findViewById(R.id.btn_parametres);
    }

    @Override
    public void onResume() {
        super.onResume();
        controller = new DATABASE(requireContext());
        prefs = requireActivity().getSharedPreferences(PREFS, MODE_PRIVATE);

        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");
        CODE_VENDEUR = prefs.getString("CODE_VENDEUR", "000000");


        //Show btn
        if (prefs.getBoolean("MODULE_ACHAT", true)) {
            lnr_achat.setVisibility(View.VISIBLE);
        } else {
            lnr_achat.setVisibility(View.GONE);
        }


        if (prefs.getBoolean("MODULE_VENTE", true)) {
            BtnVente.setVisibility(View.VISIBLE);
            lnr_vente_commande.setVisibility(View.VISIBLE);
        } else {
            BtnVente.setVisibility(View.GONE);
            lnr_vente_commande.setVisibility(View.GONE);
        }

        if (prefs.getBoolean("MODULE_COMMANDE", true)) {
            BtnCommandeClient.setVisibility(View.VISIBLE);
            lnr_vente_commande.setVisibility(View.VISIBLE);
        } else {
            BtnCommandeClient.setVisibility(View.GONE);
            // lnr_vente_commande.setVisibility(View.GONE);
        }

        if (prefs.getBoolean("MODULE_INVENTAIRE", true)) {
            //BtnClient.setVisibility(View.VISIBLE);
            BtnInventaire.setVisibility(View.VISIBLE);
        } else {
            //BtnClient.setVisibility(View.GONE);
            BtnInventaire.setVisibility(View.GONE);
        }
    }

    public void onClick(View v) {

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(300);

        int viewId = v.getId();

        if (viewId == R.id.btn_clients) {
            BtnClient.startAnimation(fadeIn);
            startActivity(ActivityClients.class, 1);

        } else if (viewId == R.id.btn_ventes) {
            if (CODE_DEPOT.equals("000000") || CODE_DEPOT.equals("")) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Important !")
                        .setContentText("Veuillez régler les paramètres de VAN ( code, nom depot ) !")
                        .show();
            } else {
                BtnVente.startAnimation(fadeIn);
                startActivity(ActivityVentes.class, 2);
            }

        } else if (viewId == R.id.btn_commande_client) {
            if (CODE_VENDEUR.equals("000000") && CODE_DEPOT.equals("000000")) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Important !")
                        .setContentText(" Veuillez régler les paramètres de VENDEUR ( code, nom vendeur ) !")
                        .show();
            } else {
                BtnCommandeClient.startAnimation(fadeIn);
                startActivity(ActivityOrdersClient.class, 3);
            }

        } else if (viewId == R.id.btn_fournisseur) {
            BtnFournisseur.startAnimation(fadeIn);
            startActivity(ActivityFournisseurs.class, 4);

        } else if (viewId == R.id.btn_achat) {
            BtnAchat.startAnimation(fadeIn);
            startActivity(ActivityAchats.class, 5);

        } else if (viewId == R.id.btn_commande_fournisseur) {
            BtnCommandeFournisseur.startAnimation(fadeIn);
            startActivity(ActivityOrdersFournisseur.class, 6);

        } else if (viewId == R.id.btn_produits) {
            BtnProduit.startAnimation(fadeIn);
            startActivity(ActivityProduits.class, 7);

        } else if (viewId == R.id.btn_inventaire) {
            BtnInventaire.startAnimation(fadeIn);
            startActivity(ActivityInventaires.class, 8);

        } else if (viewId == R.id.btn_tournee) {
            BtnTournee.startAnimation(fadeIn);
            startActivity(ActivityTourneesClient.class, 8);

        } else if (viewId == R.id.btn_import_export) {
            BtnImportExport.startAnimation(fadeIn);
            startActivity(ActivityImportsExport.class, 9);

        } else if (viewId == R.id.btn_parametres) {
            BtnParametre.startAnimation(fadeIn);
            startActivity(ActivityLogin.class, 10);
        }
    }


    public void startActivity(Class clss, int request) {
        Intent intent = new Intent(getActivity(), clss);
        intent.putExtra("SOURCE_EXPORT", "NOTEXPORTED");
        startActivityForResult(intent, request);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
