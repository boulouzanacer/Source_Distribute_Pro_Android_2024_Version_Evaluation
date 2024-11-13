package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedBackupEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedDepotEvent;
import com.safesoft.proapp.distribute.fragments.FragmentListDatabases;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterDatabases extends BaseAdapter {

    ArrayList<String> list_databases;
    private static LayoutInflater inflater = null;
    Context context;
    private final FragmentListDatabases fragment;
    private final EventBus bus = EventBus.getDefault();
    private SelectedBackupEvent event = null;

    public interface ProduitSelectedEventListener {
        void ProduitSelectedEvent(String s, PostData_Client client);
    }

    ProduitSelectedEventListener produitSelectedListener;

    public ListViewAdapterDatabases(Context mainActivity, ArrayList<String> itemList, FragmentListDatabases InstnceFrag) {
        // TODO Auto-generated constructor stub
        list_databases = itemList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.fragment = InstnceFrag;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_databases.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list_databases.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder {
        TextView nom_database;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_databases_item, null);
            holder.nom_database = convertView.findViewById(R.id.database_backup_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_1);
        } else {
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_2);
        }

        holder.nom_database.setText(list_databases.get(position));


        convertView.setBackgroundResource(R.drawable.selector_listview_client_row);
        //On item click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new SelectedBackupEvent(list_databases.get(position));
                // Post the event
                bus.post(event);
                fragment.finich_fragment();
            }
        });

        return convertView;
    }

}