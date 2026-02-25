package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedFournisseurEvent;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListFournisseur extends BaseAdapter {

    ArrayList<PostData_Fournisseur> list_fournisseurs = new ArrayList<>();
    ArrayList<PostData_Fournisseur> temp_list = new ArrayList<>();
    private static LayoutInflater inflater = null;
    Context mContext;
    private final EventBus bus = EventBus.getDefault();
    SelectedFournisseurEvent event = null;
    AlertDialog dialog;

    private String PREFS = "ALL_PREFS";
    private  SharedPreferences prefs;

    public interface ProduitSelectedEventListener {
        void ProduitSelectedEvent(String s, PostData_Fournisseur client);
    }

    ProduitSelectedEventListener produitSelectedListener;

    public ListViewAdapterListFournisseur(Context mainActivity, ArrayList<PostData_Fournisseur> itemList, AlertDialog dialog) {
        // TODO Auto-generated constructor P
        list_fournisseurs = itemList;
        temp_list.addAll(list_fournisseurs);
        mContext = mainActivity;
        this.dialog = dialog;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_fournisseurs.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list_fournisseurs.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder {
        TextView txtv_fournisseur;
        TextView code_frs;
        TextView txtv_tel;
        TextView txtv_achats_frs;
        TextView txtv_versement_frs;
        TextView txtv_solde_frs;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_fournisseur, null);
            holder.txtv_fournisseur = convertView.findViewById(R.id.fournisseur);
            // holder.code_frs = (TextView) convertView.findViewById(R.id.co);
            holder.txtv_tel = convertView.findViewById(R.id.tel_fournisseur);
            holder.txtv_achats_frs = convertView.findViewById(R.id.achat_fournisseur);
            holder.txtv_versement_frs = convertView.findViewById(R.id.verser_fournisseur);
            holder.txtv_solde_frs = convertView.findViewById(R.id.sold_fournisseur);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtv_fournisseur.setText(list_fournisseurs.get(position).fournis);
        //  holder.code_client.setText(list_clients.get(position).code_client);
        holder.txtv_tel.setText(list_fournisseurs.get(position).tel);

        holder.txtv_achats_frs.setText("ACHATS :" + new DecimalFormat("##,##0.00").format(Double.valueOf(list_fournisseurs.get(position).achat_montant)));
        holder.txtv_versement_frs.setText("VERSER :" + new DecimalFormat("##,##0.00").format(Double.valueOf(list_fournisseurs.get(position).verser_montant)));

        if (prefs.getBoolean("AFFICHAGE_SOLDE_FOURNISSEUR", true)) {
            holder.txtv_solde_frs.setText("SOLDE :" + new DecimalFormat("##,##0.00").format(Double.valueOf(list_fournisseurs.get(position).solde_montant)));
        } else {
            holder.txtv_solde_frs.setText("********");
        }

        // convertView.setBackgroundResource(R.drawable.selector_listview_client_row);
        //On item click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new SelectedFournisseurEvent(list_fournisseurs.get(position));
                // Post the event
                bus.post(event);
                dialog.dismiss();
            }
        });

        return convertView;
    }
}