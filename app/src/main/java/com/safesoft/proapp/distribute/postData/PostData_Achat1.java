package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 24/08/2016.
 */
public class PostData_Achat1 implements Serializable {
    public int recordid;
    public String num_bon;
    public String date_bon;
    public String heure;
    public String date_f;
    public String heure_f;
    public String mode_rg;
    public String code_depot;
    public String exportation;
    public String blocage;
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

    //////fournisseur///////
    public String code_frs;
    public String fournis;
    public String adresse;
    public String tel;

}
