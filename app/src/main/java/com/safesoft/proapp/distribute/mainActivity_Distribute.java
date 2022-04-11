package com.safesoft.proapp.distribute;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.media.MediaPlayer;

import com.safesoft.proapp.distribute.activities.ActivityClients;
import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.activities.ActivityInventaireAchat;
import com.safesoft.proapp.distribute.activities.ActivityProduits;
import com.safesoft.proapp.distribute.activities.ActivityTransfer1;
import com.safesoft.proapp.distribute.activities.commande.ActivityCommandes;
import com.safesoft.proapp.distribute.activities.login.ActivityLogin;
import com.safesoft.proapp.distribute.activities.vente.ActivityVentes;

public class mainActivity_Distribute extends Fragment implements View.OnClickListener{

  public static int REQUEST_ACTIVITY_VENTES = 1000;
  public static int REQUEST_ACTIVITY_BON_RECEPTION = 2000;
  public static int REQUEST_ACTIVITY_PRODUITS = 3000;
  public static int REQUEST_ACTIVITY_CLIENTS = 4000;
  public static int REQUEST_ACTIVITY_IMPORT_EXPORT = 5000;
  public static int REQUEST_ACTIVITY_COMMANDE = 6000;
  public static int REQUEST_ACTIVITY_INVENTAIRE_ACHAT = 7000;
  public static int REQUEST_ACTIVITY_PARAMETRES = 8000;

  View v ;
  private ImageButton BtnVente, BtnProduit, BtnClient, BtnBonReception, BtnImportExport, BtnCommande, BtnInventaireAchat, BtnParametre;

  public mainActivity_Distribute() {

    // Required empty public constructor
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    v= inflater.inflate(R.layout.activity_main__distribute,container,false);

    BtnVente  = v.findViewById(R.id.btnventes);
    BtnBonReception= v.findViewById(R.id.btn_b_reception);
    BtnProduit= v.findViewById(R.id.btnproduits);
    BtnClient= v.findViewById(R.id.btnclients);
    BtnImportExport= v.findViewById(R.id.btnimport_export);
    BtnCommande= v.findViewById(R.id.btncommande);
    BtnInventaireAchat= v.findViewById(R.id.btn_inventaire_achat);
    BtnParametre= v.findViewById(R.id.btnparametres);
    BtnVente.setOnClickListener(this);
      BtnBonReception.setOnClickListener(this);
    BtnProduit.setOnClickListener(this);
    BtnClient.setOnClickListener(this);
    BtnImportExport.setOnClickListener(this);
     BtnCommande.setOnClickListener(this);
    BtnInventaireAchat.setOnClickListener(this);
    BtnParametre.setOnClickListener(this);
    return v;
  }
  @SuppressLint("NonConstantResourceId")
  public void onClick(View v){

    Animation fadeIn = new AlphaAnimation(0, 1);
    fadeIn.setInterpolator(new AccelerateInterpolator()); //add this
    fadeIn.setDuration(300);

    //((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(800);

    MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.pellet);
    mp.start();

    switch (v.getId()){
      case R.id.btnventes:
        BtnVente.startAnimation(fadeIn);
        startActivity(ActivityVentes.class, REQUEST_ACTIVITY_VENTES);
        break;
      case R.id.btn_b_reception:
        BtnBonReception.startAnimation(fadeIn);
        BtnBonReception.playSoundEffect(0);
        startActivity(ActivityTransfer1.class, REQUEST_ACTIVITY_BON_RECEPTION);
        break;
      case R.id.btnproduits:
        BtnProduit.startAnimation(fadeIn);
        startActivity(ActivityProduits.class, REQUEST_ACTIVITY_PRODUITS);
        break;
      case R.id.btnclients:
        BtnClient.startAnimation(fadeIn);
        startActivity(ActivityClients.class, REQUEST_ACTIVITY_CLIENTS);
        break;
      case R.id.btnimport_export:
        BtnImportExport.startAnimation(fadeIn);
        startActivity(ActivityImportsExport.class, REQUEST_ACTIVITY_IMPORT_EXPORT);
        break;
      case R.id.btncommande:
        BtnCommande.startAnimation(fadeIn);
        startActivity(ActivityCommandes.class, REQUEST_ACTIVITY_COMMANDE);
        break;
      case R.id.btn_inventaire_achat:
        BtnInventaireAchat.startAnimation(fadeIn);
        // startActivity(ActivityEtatV.class, REQUEST_ACTIVITY_INVENTAIRE);
        startActivity(ActivityInventaireAchat.class, REQUEST_ACTIVITY_INVENTAIRE_ACHAT);
        break;
      case R.id.btnparametres:
        BtnParametre.startAnimation(fadeIn);
        startActivity(ActivityLogin.class, REQUEST_ACTIVITY_PARAMETRES);
        break;
    }
  }


  public void startActivity(Class clss, int request)
  {
    Intent intent = new Intent(getActivity(), clss);
    startActivityForResult(intent, request);
  }
}
