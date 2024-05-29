package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by UK2016 on 23/03/2017.
 */

public class ListViewAdapterPanierAchat extends ArrayAdapter<PostData_Achat2> {

  NumberFormat nf,nq;
  String SOURCE_ACTIVITY;
  final String PREFS = "ALL_PREFS";
  SharedPreferences prefs;

  public ListViewAdapterPanierAchat(@NonNull Context context, @LayoutRes int resource, @NonNull List<PostData_Achat2> objects, String SOURCE_ACTIVITY) {
    super(context, resource, objects);

    this.SOURCE_ACTIVITY = SOURCE_ACTIVITY;
    prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

    // Declare US print format
    nf = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nf).applyPattern("####0.00");

    nq = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nq).applyPattern("####0.##");

  }

  public static class ViewHolder{
    TextView produit;
    TextView nbr_colis;
    TextView colissage;
    TextView quantite;
    TextView gratuit;
    TextView pu;
    TextView lettre_xx;
    TextView lettre_equall;

  }
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    PostData_Achat2 achat2 = getItem(position);
    ViewHolder viewHolder; // view lookup cache stored in tag
    if (convertView == null) {
      // If there's no view to re-use, inflate a brand new view for row
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.layout_panier_vente, parent, false);
      viewHolder.produit = (TextView) convertView.findViewById(R.id.produit);
      viewHolder.nbr_colis = (TextView) convertView.findViewById(R.id.nbr_colis);
      viewHolder.colissage = (TextView) convertView.findViewById(R.id.colissage);
      viewHolder.quantite = (TextView) convertView.findViewById(R.id.quantite);
      viewHolder.gratuit = (TextView) convertView.findViewById(R.id.gratuit);
      viewHolder.pu = (TextView) convertView.findViewById(R.id.puv);
      viewHolder.lettre_xx = (TextView) convertView.findViewById(R.id.lettre_x);
      viewHolder.lettre_equall = (TextView) convertView.findViewById(R.id.lettre_equal);

      // Cache the viewHolder object inside the fresh view
      convertView.setTag(viewHolder);
    } else {
      // View is being recycled, retrieve the viewHolder object from tag
      viewHolder = (ViewHolder) convertView.getTag();
    }
    // Populate the data from the data object via the viewHolder object
    // into the template view.

    viewHolder.produit.setText(achat2.produit);
    if  (achat2.nbr_colis == 0.0 ) {
      viewHolder.nbr_colis.setText("");
      viewHolder.colissage.setText("");
      viewHolder.lettre_xx.setText("");
      viewHolder.lettre_equall.setText("");
    } else {
      viewHolder.nbr_colis.setText(nq.format(achat2.nbr_colis));
      viewHolder.colissage.setText(nq.format(achat2.colissage));
      viewHolder.lettre_xx.setText("X");
      viewHolder.lettre_equall.setText("=");
    }
    viewHolder.quantite.setText(nq.format(achat2.qte));
    if  (achat2.gratuit == 0.0 ) {
      viewHolder.gratuit.setText("");
    } else {
      viewHolder.gratuit.setText(nq.format(achat2.gratuit));
    }

    viewHolder.pu.setText(nf.format(achat2.pa_ht));

    if(prefs.getBoolean("AFFICHAGE_HT", false)){
      viewHolder.pu.setText(nf.format(achat2.pa_ht));
    }else{
      viewHolder.pu.setText(nf.format(achat2.pa_ht + (achat2.pa_ht * achat2.tva / 100)));
    }
   /* if(SOURCE_ACTIVITY.equals("NEW_ACHAT") || SOURCE_ACTIVITY.equals("EDIT_ACHAT") || SOURCE_ACTIVITY.equals("NEW_ORDER_ACHAT") || SOURCE_ACTIVITY.equals("EDIT_ORDER_ACHAT")){
      viewHolder.pu.setText(nf.format(bon2.pa_ht));
    }else {
      viewHolder.pu.setText(nf.format(bon2.p_u));
    }*/


    //viewHolder.gratuit.setVisibility(View.GONE);

    // Return the completed view to render on screen
    return convertView;
  }
}
