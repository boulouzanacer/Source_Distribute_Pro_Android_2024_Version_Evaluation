package com.safesoft.proapp.distribute.activities;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.proapp.distribute.R;

public class ActivityInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Info mise à jour");
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        TextView txtv_update = findViewById(R.id.txtv_update);
        ImageView logo_revendeur = findViewById(R.id.imageView2);

        String content = "<h3>Historique des versions</h3><br>" +
                "<pre>" +
                "    <h6><p style=color:red;>Version v2.4.25 (02-04-2025)<br></p></h6>" +
                "    <p style=color:black;>1 - Ajouter button 'Importation bon de commandes' pour importer les bons de commandes depuis le serveur</p>" +
                "    <p style=color:black;>2 - Ajouter une carte pour afficher les clients ayant des bons de commande et suivre leurs validations à travers des marqueurs.</p>" +
                "    <p style=color:black;>3 - Imprimer Etat de vente et commande</p>" +
                "    <p style=color:black;>4 - Exportation PDF en plusieurs pages</p>" +
                "    <p style=color:black;>5 - Ajouter 2 model impression pour la langue latin et 2 pour la langue arabe</p>" +
                "    <p style=color:black;>6 - Ajouter prix limite</p>" +
                "    <p style=color:black;>7 - Possibilité de synchroniser les nouveau clients, fournisseurs, produits et leurs details (prix, photos ...) vers le serveur</p>" +
                "    <br>" +

                "    <h6><p style=color:red;>Version v30.3.24 (30-03-2024)<br></p></h6>" +
                "    <p style=color:black;>1 - Ajouter Mode évaluation ( pour les tests )</p>" +
                "    <p style=color:black;>2 - Nouveau produit avec 3 mode de tarif par defaut</p>" +
                "    <p style=color:black;>3 - Afficher le dernier prix vendu d'un produit pour un client</p>" +
                "    <p style=color:black;>4 - Ajouter solde initial client</p>" +
                "    <p style=color:black;>5 - Paramètres : Autoriser / Empêcher la modification du prix de vente</p>" +
                "    <p style=color:black;>6 - Paramètres : Autoriser / Empêcher la modification du bon de vente, commande et achat</p>" +
                "    <p style=color:black;>7 - Paramètres : Sychroniser tous les clients</p>" +
                "    <p style=color:black;>8 - Paramètres : Afficher / cacher les photos des produits</p>" +
                "    <p style=color:black;>9 - Paramètres : Activer / desactiver le son</p>" +
                "    <p style=color:black;>10 - Paramètres : sauvegarder les filtres de recherche</p>" +
                "    <p style=color:black;>11 - Paramètres : afficher / cacher le remise</p>" +
                "    <p style=color:black;>12 - Paramètres : Afficher / masquer les produits stock 0 ou moins</p>" +
                "    <p style=color:black;>13 - Paramètres : Afficher / masquer le benifice pour chaque bon de vente</p>" +
                "    <p style=color:black;>14 - Paramètres : Afficher / masquer les modules ( vente, achat, commande, inventaire)</p>" +
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
                "        &ensp;5- Details fournisseur<br>" +
                "        &ensp;6- Versement fournisseur (Mode mono-poste)<br>" +
                "        &ensp;7- Imprimer Versement fournisseur (Mode mono-poste)<br>" +

                "    <p style=color:navy;>[AJOUTER] Rubrique achat</p>" +
                "        &ensp;1- List bon achats<br>" +
                "        &ensp;2- Ajouter nouveau bon<br>" +
                "        &ensp;3- Supprimer bon achat<br>" +
                "        &ensp;4- Modifier bon achat<br>" +
                "        &ensp;5- Imprimer bon achat<br>" +
                "        &ensp;6- Exporter bon d'achat vers format PDF<br>" +

                "    <p style=color:green;>[MAJ] Rubrique client</p>" +
                "        &ensp;1- [AJOUTER] Supprimer client<br>" +
                "        &ensp;2- [AJOUTER] Modifier client<br>" +
                "        &ensp;3- [AJOUTER] Maps location clients<br>" +
                "        &ensp;4- [AJOUTER] details client<br>" +
                "        &ensp;5- [AJOUTER] Imprimer versement client<br>" +
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
}