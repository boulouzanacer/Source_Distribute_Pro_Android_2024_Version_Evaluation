package com.safesoft.proapp.distribute.adapters;

import android.content.Context;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.safesoft.proapp.distribute.postData.PostData_Transfer2;
import com.safesoft.proapp.distribute.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by UK2016 on 23/03/2017.
 */

public class ListViewAdapterTransfert2 extends ArrayAdapter<PostData_Transfer2> {

    private NumberFormat nf;


    public ListViewAdapterTransfert2(@NonNull Context context, @LayoutRes int resource, @NonNull List<PostData_Transfer2> objects) {
        super(context, resource, objects);
    }

    public static class ViewHolder {
        TextView produit;
        TextView quantite;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostData_Transfer2 transfert2 = getItem(position);
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("###,##0.00");

        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.transfert2_items, parent, false);
            viewHolder.produit = convertView.findViewById(R.id.produit);
            viewHolder.quantite = convertView.findViewById(R.id.quantite);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        viewHolder.produit.setText(transfert2.produit);
        viewHolder.quantite.setText(nf.format(transfert2.qte));

        // Return the completed view to render on screen
        return convertView;
    }
}
