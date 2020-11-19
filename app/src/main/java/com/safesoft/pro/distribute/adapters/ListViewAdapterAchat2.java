package com.safesoft.pro.distribute.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.postData.PostData_Achat2;

import java.util.List;

/**
 * Created by UK2016 on 23/03/2017.
 */

public class ListViewAdapterAchat2 extends ArrayAdapter<PostData_Achat2> {

    public ListViewAdapterAchat2(@NonNull Context context, @LayoutRes int resource, @NonNull List<PostData_Achat2> objects) {
        super(context, resource, objects);
    }

    public static class ViewHolder{
        TextView produit;
        TextView quantite;
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
            convertView = inflater.inflate(R.layout.transfert2_items, parent, false);
            viewHolder.produit = (TextView) convertView.findViewById(R.id.produit);
            viewHolder.quantite = (TextView) convertView.findViewById(R.id.quantite);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.produit.setText(achat2.produit);
        viewHolder.quantite.setText(achat2.qte);
        // Return the completed view to render on screen
        return convertView;
    }
}
