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
    public String mode_rg;
    public String mode_tarif = "0";
    ///////////////////////
    public Integer nbr_p;
    public Double tot_qte;
    ///////////////////////
    public Double tot_ht = 0.00;
    public Double tot_tva = 0.00;
    public Double timbre = 0.0;
    public Double tot_ttc = 0.00;
    public Double remise = 0.0;
    public Double montant_bon = 0.0;
    ////////////////////
    public Double solde_ancien = 0.00;
    public Double verser = 0.00;
    public Double reste = 0.00;
    ////////////////////
    public Double latitude;
    public Double longitude;
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
    public Double client_solde;
    public Double credit_limit;
    public Double latitude_client;
    public Double longitude_client;
    /////bon1/////////////
    public String code_depot;
    public String code_vendeur;
    public String exportation;
    public String blocage;



}
