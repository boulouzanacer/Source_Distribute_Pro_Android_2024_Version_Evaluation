package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by UK2016 on 23/03/2017.
 */

public class ListViewAdapterPanierInventaire extends ArrayAdapter<PostData_Inv2> {

    NumberFormat nf, nq;

    public ListViewAdapterPanierInventaire(@NonNull Context context, @LayoutRes int resource, @NonNull List<PostData_Inv2> objects) {
        super(context, resource, objects);

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        nq = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nq).applyPattern("####0.##");

    }

    public static class ViewHolder {

        TextView produit;
        TextView qte_theorique;
        TextView lettre_minus;
        TextView qte_physique;
        TextView ecart;
        TextView lettre_equall;
        TextView lettre_x;
        TextView pa_ht;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostData_Inv2 inv2 = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_panier_inventaire, parent, false);

            viewHolder.produit = convertView.findViewById(R.id.produit);
            viewHolder.qte_theorique = convertView.findViewById(R.id.qte_theorique);
            viewHolder.lettre_minus = convertView.findViewById(R.id.lettre_minus);
            viewHolder.qte_physique = convertView.findViewById(R.id.qte_physique);
            viewHolder.ecart = convertView.findViewById(R.id.ecart);
            viewHolder.lettre_equall = convertView.findViewById(R.id.lettre_equal);
            viewHolder.lettre_x = convertView.findViewById(R.id.lettre_x);
            viewHolder.pa_ht = convertView.findViewById(R.id.puv);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        assert inv2 != null;
        viewHolder.produit.setText(inv2.produit);
        viewHolder.qte_theorique.setText(nq.format(inv2.qte_theorique));
        viewHolder.qte_physique.setText(nq.format(inv2.qte_physique));
        viewHolder.ecart.setText(nq.format(inv2.qte_theorique - inv2.qte_physique));
        viewHolder.pa_ht.setText(nf.format(inv2.pa_ht));

        //viewHolder.gratuit.setVisibility(View.GONE);

        // Return the completed view to render on screen
        return convertView;
    }
}
