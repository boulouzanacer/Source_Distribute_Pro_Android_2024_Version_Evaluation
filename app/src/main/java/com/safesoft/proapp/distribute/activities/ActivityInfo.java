package com.safesoft.proapp.distribute.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.proapp.distribute.R;

public class ActivityInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Info mise à jour");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        TextView txtv_update = findViewById(R.id.txtv_update);
        ImageView logo_revendeur = findViewById(R.id.imageView2);

        String content = "<h3>Historique des versions</h3><br>" +
                "<pre>" +
                "    <h6>version v30.3.24 (30-03-2024)<br></h6>" +
                "    <p style=color:black;>[AJOUTER] Mode évaluation ( pour les test )</p>" +
                "    <p style=color:red;>[MAJ] Rubrique produit</p>" +
                "        &ensp;1- [MAJ] Nouveau produit avec 3 mode de tarif par defaut <br>" +
                "    <h6>version v07.2.24 (04-02-2024)<br></h6>" +
                "    <p style=color:teal;>[MAJ] Rubrique vente</p>" +
                "        &ensp;1- Ajouter le dernier prix d'un produit vendu pour un client <br>" +
                "    <p style=color:green;>[MAJ] Rubrique client</p>" +
                "        &ensp;1- [MAJ] Nouveau client - Ajouter solde initial<br>" +
                "    <p style=color:red;>[MAJ] Rubrique produit</p>" +
                "        &ensp;1- [MAJ] Modifier détails produit <br>" +
                "    <br>" +
                "    <h6>version v21.1.24 (21-01-2024)<br></h6>" +
                "    <p style=color:olive;>[MAJ] Rubrique Paramètres</p>" +
                "        &ensp;4- [MAJ][Paramètres imprimantes]<br>" +
                "            &emsp;- [AJOUTER] model ticket (latine / arabe) <br>" +
                "    <br>" +
                "    <h6>version v7.1.24 (07-01-2024)<br></h6>" +
                "    <p style=color:green;>[MAJ] Rubrique client</p>" +
                "        &ensp;1- [AJOUTER] Imprimer versement client<br>" +
                "    <p style=color:red;>[MAJ] Rubrique produit</p>" +
                "        &ensp;1- [Ajouter] Filtrer la recherche par famille <br>" +
                "        &ensp;1- [Ajouter] Suppression produit <br>" +
                "    <p style=color:teal;>[MAJ] Rubrique vente</p>" +
                "        &ensp;1- Dans la séléction de produit, garder la dernière position séléctionnée <br>" +
                "    <p style=color:blue;>[MAJ] Rubrique commande</p>" +
                "        &ensp;1- Dans la séléction de produit, garder la dernière position séléctionnée <br>" +
                "    <p style=color:navy;>[MAJ] Rubrique achat</p>" +
                "        &ensp;1- Dans la séléction de produit, garder la dernière position séléctionnée <br>" +
                "    <br>" +
                "    <h6>version v13.12.23 (13-12-2023)<br></h6>" +
                "    <p style=color:red;>[MAJ] Rubrique produit</p>" +
                "        &ensp;1- [AJOUTER] Ajouter produit<br>" +
                "    [MAJ] Rubrique client<br>" +
                "        &ensp;1- [AJOUTER] Modifier versement<br>" +
                "        &ensp;1- [AJOUTER] Supprimer versement<br>" +
                "    <p style=color:olive;>[MAJ] Rubrique Paramètres</p>" +
                "        &ensp;1- [MAJ] [Sauvegarde des donneées]<br>" +
                "            &emsp;- [AJOUTER] Sauvegarder la base (backup)<br>" +
                "            &emsp;- [AJOUTER] Restoration de la base (Restore) <br>" +
                "        &ensp;1- [MAJ] [Réinitialisation des des donneées]<br>" +
                "            &emsp;- Réinitialiser la base de données<br>" +
                "    <br>" +
                "    <h6>version v30.11.23 (30-11-2023)<br></h6>" +
                "    <p style=color:maroon;>[AJOUTER] Rubrique fournisseur</p>" +
                "        &ensp;1- List fournisseur<br>" +
                "        &ensp;2- Ajouter fournisseur<br>" +
                "        &ensp;3- Supprimer fournisseur<br>" +
                "        &ensp;4- Modifier fournisseur<br>" +
                "    <p style=color:navy;>[AJOUTER] Rubrique achat</p>" +
                "        &ensp;1- List bon achats<br>" +
                "        &ensp;2- Ajouter nouveau bon<br>" +
                "        &ensp;3- Supprimer bon achat<br>" +
                "        &ensp;4- Modifier bon achat<br>" +
                "        &ensp;5- Imprimer bon achat<br>" +
                "    <p style=color:green;>[MAJ] Rubrique client</p>" +
                "        &ensp;1- [AJOUTER] Supprimer client<br>" +
                "        &ensp;2- [AJOUTER] Modifier client<br>" +
                "        &ensp;3- [AJOUTER] Maps location clients<br>" +
                "    <p style=color:purple;>[MAJ] Rubrique Import/export</p>" +
                "        &ensp;1- [AJOUTER] Synchroniser les fournisseur (LOCAL et INTERNET)<br>" +
                "        &ensp;2- [AJOUTER] Synchronisation des paramètres PME PRO / HYPER PRO (LOCAL et INTERNET)<br>" +
                "        &ensp;3- [AJOUTER] Bon de ventes exportés <br>" +
                "        &ensp;4- [AJOUTER] Bon de commandes exportés <br>" +
                "        &ensp;5- [AJOUTER] Bon d'inventaire exportés <br>" +
                "        &ensp;4- [AJOUTER] Etat de vente<br>" +
                "        &ensp;5- [AJOUTER] Etat de commandes<br>" +
                "            &emsp;- Paramètres quantité gratuit<br>" +
                "            &emsp;- Paramètres ftp <br>" +
                "            &emsp;- Paramètres info entreprise <br>" +
                "            &emsp;- Paramètres prix de ventes <br>" +
                "        &ensp;6- [AJOUTER] Exportation (LOCAL et INTERNET) bon d'achat<br>" +
                "        &ensp;7- [MAJ] Exportation (FTP et INTERNET) bon de vente et versement client<br>" +
                "        &ensp;8- [MAJ] Exportation (FTP et INTERNET) bon de comande et versement client<br>" +
                "        &ensp;9- [MAJ] Exportation (LOCAL et INTERNET) bon inventaire <br>" +
                "    <p style=color:olive;>[MAJ] Rubrique Paramètres</p>" +
                "        &ensp;1- [Paramètres connexion]<br>" +
                "            &emsp;- [AJOUTER] Supporter PME PRO et HYPER PRO<br>" +
                "            &emsp;- [AJOUTER] Connexion via internet<br>" +
                "        &ensp;3- [AJOUTER] Paramètres FTP<br>" +
                "        &ensp;4- [MAJ][Paramètres divers]<br>" +
                "            &emsp;- [AJOUTER] Activation Prix HT<br>" +
                "            &emsp;- [AJOUTER] Activation Prix achat<br>" +
                "            &emsp;- [AJOUTER] Masquer les produits stock 0 ou moins<br>" +
                "            &emsp;- [AJOUTER] Affichage remise<br>" +
                "            &emsp;- [AJOUTER] Autoriser la modification de bon<br>" +
                "            &emsp;- [AJOUTER] Désactiver la modification du prix de vente<br>" +
                "            &emsp;- [AJOUTER] Sauvegarder les filtres de recherche<br>" +
                "            &emsp;- [AJOUTER] Vente avec stock moins<br>" +
                "            &emsp;- [AJOUTER] Afficher les achats client<br>" +
                "    <br>" +
                "    <h6>version v15.08.23 (15-08-2023)<br></h6>" +
                "    Version initial<br>" +
                "    <p style=color:green;>[AJOUTER] Rubrique client</p>" +
                "        &ensp;1- List clients<br>" +
                "        &ensp;2- Ajouter versement<br>" +
                "    <p style=color:red;>[AJOUTER] Rubrique produit</p>" +
                "        &ensp;1- List produits<br>" +
                "        &ensp;2- Details produits<br>" +
                "    <p style=color:teal;>[AJOUTER] Rubrique vente</p>" +
                "        &ensp;1- List bon de vente<br>" +
                "        &ensp;2- Ajouter nouveau bon<br>" +
                "        &ensp;3- Supprimer bon de vente<br>" +
                "        &ensp;4- Modifier bon de vente<br>" +
                "        &ensp;5- Imprimer bon de vente<br>" +
                "    <p style=color:blue;>[AJOUTER] Rubrique commande</p>" +
                "        &ensp;1- List bon de commandes<br>" +
                "        &ensp;2- Ajouter nouveau commandes<br>" +
                "        &ensp;3- Supprimer bon de commandes<br>" +
                "        &ensp;4- Modifier bon de commandes<br>" +
                "        &ensp;5- Imprimer bon de commandes<br>" +
                "    <p style=color:fuchsia;>[AJOUTER] Rubrique inventaire</p>" +
                "        &ensp;1- List bon inventaires<br>" +
                "        &ensp;2- Ajouter nouveau inventaire<br>" +
                "        &ensp;3- Supprimer bon inventaire<br>" +
                "        &ensp;4- Modifier bon inventaire<br>" +
                "        &ensp;5- Imprimer bon inventaire<br>" +
                "    <p style=color:purple;>[AJOUTER] Rubrique Import/export</p>" +
                "        &ensp;1- Synchroniser les clients<br>" +
                "        &ensp;2- Synchroniser les produits<br>" +
                "        &ensp;3- Exportation (LOCAL) bon de ventes et versement client<br>" +
                "        &ensp;4- Exportation (LOCAL) bon commandes et versement client<br>" +
                "        &ensp;5- Exportation (LOCAL) bon inventaire <br>" +
                "    <p style=color:olive;>[AJOUTER] Rubrique Paramètres</p>" +
                "        &ensp;1- [Paramètres connexion]<br>" +
                "            &emsp;- Connexion local avec le serveur BDD<br>" +
                "        &ensp;2- [Paramètres imprimente]<br>" +
                "        &ensp;2- [Réinitialisation des données]<br>" +
                "            &emsp;- Modifier mot de passe de paramètres <br>" +
                "        &ensp;3- [Paramètres divers]<br>" +
                "            &emsp;- Paramètres system objectif<br>" +
                "            &emsp;- Paramètres localisation des bons <br>" +
                "            &emsp;- Afficher photo produit <br>" +
                "</pre>";
        txtv_update.setText(Html.fromHtml(
                content,
                Html.FROM_HTML_MODE_COMPACT));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}