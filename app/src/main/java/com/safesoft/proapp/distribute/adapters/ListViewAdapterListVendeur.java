package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedVendeurEvent;
import com.safesoft.proapp.distribute.fragments.FragmentSelectedVendeur;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Vendeur;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListVendeur extends BaseAdapter {

    ArrayList<PostData_Vendeur> list_vendeur;
    private static LayoutInflater inflater = null;
    Context context;
    private final FragmentSelectedVendeur fragment;
    private final EventBus bus = EventBus.getDefault();
    private SelectedVendeurEvent event = null;

    public interface ProduitSelectedEventListener {
        void ProduitSelectedEvent(String s, PostData_Client client);
    }

    ProduitSelectedEventListener produitSelectedListener;

    public ListViewAdapterListVendeur(Context mainActivity, ArrayList<PostData_Vendeur> itemList, FragmentSelectedVendeur InstnceFrag) {
        // TODO Auto-generated constructor stub
        list_vendeur = itemList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.fragment = InstnceFrag;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_vendeur.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list_vendeur.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder {
        TextView code_vendeur;
        TextView nom_vendeur;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_selected_vendeurs, null);
            holder.code_vendeur = convertView.findViewById(R.id.code_vendeur);
            holder.nom_vendeur = convertView.findViewById(R.id.nom_vendeur);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_1);
        } else {
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_2);
        }

        holder.code_vendeur.setText(list_vendeur.get(position).code_vendeur);
        holder.nom_vendeur.setText(list_vendeur.get(position).nom_vendeur);


        convertView.setBackgroundResource(R.drawable.selector_listview_client_row);
        //On item click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new SelectedVendeurEvent(list_vendeur.get(position).code_vendeur, list_vendeur.get(position).nom_vendeur);
                // Post the event
                bus.post(event);
                fragment.finich_fragment();
            }
        });

        return convertView;
    }

}