package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterClientsVersement extends RecyclerView.Adapter<RecyclerAdapterClientsVersement.MyViewHolder> {

    private final List<PostData_Client> clientsList;
    private int color = 0;
    private ColorGeneratorModified generator;
    private final Context mContext;
    private final String PREFS = "ALL_PREFS";
    private final SharedPreferences prefs;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ClientN;
        CardView cardView;
        ImageView image;
        TextView Tel_clientN;
        TextView Total_versement;

        MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.item_root);
            ClientN = view.findViewById(R.id.client);
            Tel_clientN = view.findViewById(R.id.tel_client);
            Total_versement = view.findViewById(R.id.vrs_client);
            image = view.findViewById(R.id.imageId);
        }
    }


    public RecyclerAdapterClientsVersement(Context context, List<PostData_Client> itemList) {
        this.clientsList = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        mContext = context;
        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clients_versement, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        PostData_Client item = clientsList.get(position);

        holder.ClientN.setTextSize(17);
        holder.ClientN.setTypeface(null, Typeface.BOLD);
        holder.ClientN.setText(item.client);

        holder.Tel_clientN.setText("TEL : " + item.tel);

        holder.Total_versement.setTypeface(null, Typeface.BOLD);
        holder.Total_versement.setText("Versement : " + new DecimalFormat("##,##0.00").format(Double.valueOf(item.verser_montant)));

        String firstChar = "NO";
        if (item.client != null) {
            if (item.client.length() == 1) {
                firstChar = String.valueOf(item.client.charAt(0));
            } else if (!item.client.isEmpty()) {
                firstChar = String.valueOf(item.client.charAt(0)) + item.client.charAt(1);
            } else {
                firstChar = "NO";
            }

        }

        if (color == 0) {
            if (generator != null)
                color = generator.getColor(clientsList.get(position).client);
        }

        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar.toUpperCase(), color);
        holder.image.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return clientsList.size();
    }

}