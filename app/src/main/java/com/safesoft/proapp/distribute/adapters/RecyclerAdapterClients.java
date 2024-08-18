package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterClients extends RecyclerView.Adapter<RecyclerAdapterClients.MyViewHolder> {

    private final List<PostData_Client> fournisList;
    private int color = 0;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private final Context mContext;
    private final String PREFS = "ALL_PREFS";
    private final SharedPreferences prefs;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ClientN;
        CardView cardView;
        ImageView image;
        ImageView img_pos_client;
        TextView Tel_clientN;
        TextView Sld_clientN;

        MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.item_root);
            ClientN = view.findViewById(R.id.client);
            Tel_clientN = view.findViewById(R.id.tel_client);
            Sld_clientN = view.findViewById(R.id.sld_client);
            image = view.findViewById(R.id.imageId);
            img_pos_client = view.findViewById(R.id.img_pos_client);
        }
    }


    public RecyclerAdapterClients(Context context, List<PostData_Client> itemList) {
        this.fournisList = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        mContext = context;
        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clients, parent, false);
        itemClick = (ItemClick) parent.getContext();
        itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        PostData_Client item = fournisList.get(position);

        holder.ClientN.setTextSize(17);
        holder.ClientN.setTypeface(null, Typeface.BOLD);
        holder.ClientN.setText(item.client);

        holder.Tel_clientN.setText("TEL : " + item.tel);

        holder.Sld_clientN.setTypeface(null, Typeface.BOLD);
        holder.Sld_clientN.setText("Solde : " + item.solde_montant);

        if (prefs.getBoolean("AFFICHAGE_SOLDE_CLIENT", true)) {
            holder.Sld_clientN.setText("Solde :" + new DecimalFormat("##,##0.00").format(Double.valueOf(item.solde_montant)));
        } else {
            holder.Sld_clientN.setText("********");
        }


        if (item.latitude == 0.0) {
            holder.img_pos_client.setImageResource(R.drawable.ic_baseline_wrong_location_24);
        } else {
            holder.img_pos_client.setImageResource(R.drawable.ic_baseline_location_on_24);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view, holder.getAdapterPosition());
            }
        });


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemLongClick.onLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });


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
                color = generator.getColor(fournisList.get(position).client);
        }

        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar.toUpperCase(), color);
        holder.image.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return fournisList.size();
    }

    public interface ItemClick {
        void onClick(View v, int position);
    }

    public interface ItemLongClick {
        void onLongClick(View v, int position);
    }

    public void refresh(List<PostData_Client> new_itemList) {
        fournisList.clear();
        fournisList.addAll(new_itemList);
        notifyDataSetChanged();
    }
}