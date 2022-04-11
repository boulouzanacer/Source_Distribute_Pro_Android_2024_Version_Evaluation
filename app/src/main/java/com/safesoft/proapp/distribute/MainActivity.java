package com.safesoft.proapp.distribute;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.safesoft.proapp.distribute.activities.ActivityClients;
import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.activities.ActivityInventaireAchat;
import com.safesoft.proapp.distribute.activities.ActivityProduits;
import com.safesoft.proapp.distribute.activities.ActivityTransfer1;
import com.safesoft.proapp.distribute.activities.commande.ActivityCommandes;
import com.safesoft.proapp.distribute.activities.login.ActivityLogin;
import com.safesoft.proapp.distribute.activities.vente.ActivityVentes;

import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_BON_RECEPTION;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_CLIENTS;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_COMMANDE;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_IMPORT_EXPORT;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_INVENTAIRE_ACHAT;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_PARAMETRES;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_PRODUITS;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_VENTES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
  Fragment objFrgment;
  FragmentManager fragmentManager;
  private  MediaPlayer mp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbardrawer);
    setSupportActionBar(toolbar);



    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    objFrgment = new mainActivity_Distribute();
    fragmentManager = getSupportFragmentManager();
    if(objFrgment != null)
    {
      fragmentManager.beginTransaction().replace(R.id.drawer_layoutt,objFrgment).commit();
      drawer.closeDrawer(GravityCompat.START);

    }

  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {

    //((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(800);

    mp = MediaPlayer.create(this, R.raw.pellet);
    mp.start();
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.vente) {
      startActivity(ActivityVentes.class, REQUEST_ACTIVITY_VENTES);
    } else if (id == R.id.transferts) {
      startActivity(ActivityTransfer1.class, REQUEST_ACTIVITY_BON_RECEPTION);

    } else if (id == R.id.produits) {
      startActivity(ActivityProduits.class, REQUEST_ACTIVITY_PRODUITS);

    } else if (id == R.id.clients) {
      startActivity(ActivityClients.class, REQUEST_ACTIVITY_CLIENTS);

    } else if (id == R.id.import_export) {
      startActivity(ActivityImportsExport.class, REQUEST_ACTIVITY_IMPORT_EXPORT);

    } else if (id == R.id.commande) {
      startActivity(ActivityCommandes.class, REQUEST_ACTIVITY_COMMANDE);

    } else if (id == R.id.inventaire) {
      startActivity(ActivityInventaireAchat.class, REQUEST_ACTIVITY_INVENTAIRE_ACHAT);

    } else if (id == R.id.parametres)
    {
      startActivity(ActivityLogin.class, REQUEST_ACTIVITY_PARAMETRES);

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
  public void startActivity(Class clss, int request)
  {
    Intent intent = new Intent(this, clss);
    startActivityForResult(intent, request);
  }
}
