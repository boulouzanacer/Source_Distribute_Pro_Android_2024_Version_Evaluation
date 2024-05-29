package com.safesoft.proapp.distribute.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;

import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListClient extends BaseAdapter {

  ArrayList<PostData_Client> list_clients = new ArrayList<PostData_Client>();
  ArrayList<PostData_Client> temp_list = new ArrayList<>();
  private static LayoutInflater inflater = null;
  Context mContext;
  private final EventBus bus = EventBus.getDefault();
  SelectedClientEvent event = null;
  AlertDialog dialog;
  String SOURCE;
  private final String PREFS = "ALL_PREFS";
  private SharedPreferences prefs;

  public interface ProduitSelectedEventListener {
    void ProduitSelectedEvent(String s, PostData_Client client);
  }

  ProduitSelectedEventListener produitSelectedListener;

  public ListViewAdapterListClient(Context mainActivity, ArrayList<PostData_Client> itemList, AlertDialog dialog, String SOURCE) {
    // TODO Auto-generated constructor P
    list_clients = itemList;
    temp_list.addAll(list_clients);
    mContext = mainActivity;
    this.dialog = dialog;
    inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.SOURCE = SOURCE;
    prefs = mContext.getSharedPreferences(PREFS, mContext.MODE_PRIVATE);
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return list_clients.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return list_clients.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }

  private class ViewHolder {
    TextView client;
    TextView code_client;
    TextView solde_client;
    ImageView img_client_location;
  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    final ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflater.inflate(R.layout.list_client_row, null);
      holder.client = (TextView) convertView.findViewById(R.id.nom_client);
      holder.code_client = (TextView) convertView.findViewById(R.id.code_client);
      holder.solde_client = (TextView) convertView.findViewById(R.id.sold_client);
      holder.img_client_location = (ImageView) convertView.findViewById(R.id.img_client_location);

      convertView.setTag(holder);

    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.client.setText(list_clients.get(position).client);
    holder.code_client.setText(list_clients.get(position).code_client);

    if (prefs.getBoolean("AFFICHAGE_SOLDE_CLIENT", true)) {
      holder.solde_client.setText(new DecimalFormat("##,##0.00").format(list_clients.get(position).solde_montant));
    }else {
      holder.solde_client.setText("********");
    }



    if(list_clients.get(position).latitude != 0.0){
      holder.img_client_location.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_location_on_24));
    }else {
      holder.img_client_location.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_wrong_location_24));
    }

   // convertView.setBackgroundResource(R.drawable.selector_listview_client_row);
    //On item click event
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(SOURCE.equals("FROM_ROUTING")){
          if(list_clients.get(position).latitude != 0.0){
            event = new SelectedClientEvent(list_clients.get(position));
            // Post the event
            bus.post(event);
            //dialog.dismiss();

            new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Route...")
                    .setContentText("Client bien ajouté dans la liste de route")
                    .show();
          }else {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Route...")
                    .setContentText("Client n'a pas une position géographique, Veuillez introduisez une dans Client -> Détails client")
                    .show();
          }
        }else {
            event = new SelectedClientEvent(list_clients.get(position));
            // Post the event
            bus.post(event);
            dialog.dismiss();
        }
      }
    });

    return convertView;
  }
}