package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Bon1 implements Serializable {

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
    public double montant_achat = 0.0;
    public double benifice_par_bon = 0.0;
    ////////////////////
    public double ancien_solde = 0.00;
    public double verser = 0.00;
    public double reste = 0.00;
    ////////////////////
    public double latitude;
    public double longitude;

    public int livrer;
    public String date_liv;
    public int is_imported = 0;

    //////client///////
    public String code_client;
    public String client;
    public String adresse;
    public String wilaya;
    public String commune;
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
