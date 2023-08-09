package com.safesoft.proapp.distribute.postData;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Bon1 {

    /////bon1/////////////
    public Integer recordid;
    public String num_bon;
    public String date_bon;
    public String heure;
    public String date_f;
    public String heure_f;
    public String mode_rg;
    public String mode_tarif = "0";
    ///////////////////////
    public Integer nbr_p;
    public double tot_qte;
    ///////////////////////
    public double tot_ht = 0.00;
    public double tot_tva = 0.00;
    public double timbre = 0.0;
    public double tot_ttc = 0.00;
    public double remise = 0.0;
    public double montant_bon = 0.0;
    ////////////////////
    public double solde_ancien = 0.00;
    public double verser = 0.00;
    public double reste = 0.00;
    ////////////////////
    public double latitude;
    public double longitude;

     //////client///////
    public String code_client;
    public String client;
    public String adresse;
    public String tel;
    public String rc;
    public String ifiscal;
    public String ai;
    public String nis;
    ////////////////////
    public double client_solde;
    public double credit_limit;
    public double latitude_client;
    public double longitude_client;
    /////bon1/////////////
    public String code_depot;
    public String code_vendeur;
    public String exportation;
    public String blocage;



}
