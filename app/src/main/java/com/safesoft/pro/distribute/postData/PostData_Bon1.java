package com.safesoft.pro.distribute.postData;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Bon1 {
    public String recordid;
    public String num_bon;
    public String date_bon;
    public String heure;
    public String code_client;
    public String client;
    public Double credit_limit;
    public String nbr_p;
    public String timbre;
    public String remise;
    public String mode_rg;
    public String code_vendeur;
    public String mode_tarif = "0";
    public String code_depot;
    public String montant_bon;
    public String adresse;
    public String tel;
    public String longitude_client;
    public String latitude_client;
    public String rc;
    public String ifiscal;


    public String solde_ancien = "0.00";
    public String verser = "0.00";
    public String reste = "0.00";

    public String tot_ht = "0.00";
    public String tot_tva = "0.00";
    public String tot_ttc = "0.00";
    public String tot_ttc_remise = "0.00";

    public Double latitude;
    public Double longitude;

    public Boolean timbre_ckecked = false;
    public Boolean remise_ckecked = false;

    public String exportation;
    public String blocage;
}
