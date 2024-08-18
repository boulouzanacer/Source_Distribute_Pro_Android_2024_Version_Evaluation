package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedDepotEvent;
import com.safesoft.proapp.distribute.fragments.FragmentSelectedDepot;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Depot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListDepot extends BaseAdapter {

    ArrayList<PostData_Depot> list_depot;
    private static LayoutInflater inflater = null;
    Context context;
    private final FragmentSelectedDepot fragment;
    private final EventBus bus = EventBus.getDefault();
    private SelectedDepotEvent event = null;

    public interface ProduitSelectedEventListener {
        void ProduitSelectedEvent(String s, PostData_Client client);
    }

    ProduitSelectedEventListener produitSelectedListener;

    public ListViewAdapterListDepot(Context mainActivity, ArrayList<PostData_Depot> itemList, FragmentSelectedDepot InstnceFrag) {
        // TODO Auto-generated constructor stub
        list_depot = itemList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.fragment = InstnceFrag;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_depot.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list_depot.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder {
        TextView code_depot;
        TextView nom_depot;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_selected_depots, null);
            holder.code_depot = convertView.findViewById(R.id.code_depot);
            holder.nom_depot = convertView.findViewById(R.id.nom_depot);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_1);
        } else {
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_2);
        }

        holder.code_depot.setText(list_depot.get(position).code_depot);
        holder.nom_depot.setText(list_depot.get(position).nom_depot);


        convertView.setBackgroundResource(R.drawable.selector_listview_client_row);
        //On item click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new SelectedDepotEvent(list_depot.get(position).code_depot, list_depot.get(position).nom_depot);
                // Post the event
                bus.post(event);
                fragment.finich_fragment();
            }
        });

        return convertView;
    }

}