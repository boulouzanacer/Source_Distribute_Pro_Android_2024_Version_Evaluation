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

import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by UK2016 on 23/03/2017.
 */

public class ListViewAdapterBon2 extends ArrayAdapter<PostData_Bon2> {

  public ListViewAdapterBon2(@NonNull Context context, @LayoutRes int resource, @NonNull List<PostData_Bon2> objects) {
    super(context, resource, objects);
  }

  public static class ViewHolder{
    TextView produit;
    TextView quantite;
    TextView prix_u;

  }
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    PostData_Bon2 bon2 = getItem(position);
    ViewHolder viewHolder; // view lookup cache stored in tag
    if (convertView == null) {
      // If there's no view to re-use, inflate a brand new view for row
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.bon2_items, parent, false);

      viewHolder.produit = convertView.findViewById(R.id.produit);
      viewHolder.quantite = convertView.findViewById(R.id.quantite);
      viewHolder.prix_u = convertView.findViewById(R.id.prix_u);

      // Cache the viewHolder object inside the fresh view
      convertView.setTag(viewHolder);
    } else {
      // View is being recycled, retrieve the viewHolder object from tag
      viewHolder = (ViewHolder) convertView.getTag();
    }
    // Populate the data from the data object via the viewHolder object
    // into the template view.
    assert bon2 != null;


    viewHolder.produit.setText(bon2.produit);
    viewHolder.quantite.setText(bon2.qte);

    viewHolder.prix_u.setText(new DecimalFormat("###,##0.00").format(Double.valueOf(bon2.p_u)));



    // Return the completed view to render on screen
    return convertView;
  }


}
