package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
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

public class RecyclerAdapterRouting extends RecyclerView.Adapter<RecyclerAdapterRouting.MyViewHolder> {

    private final List<PostData_Client> clientList;
    private ItemLongClick itemLongClick;
    private ItemClick itemClick;
    private final Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ClientN;
        CardView cardView;
        ImageView im_state;
        ImageView img_client_location;
        TextView Tel_clientN;
        TextView Sld_clientN;

        MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.item_root);
            ClientN = view.findViewById(R.id.client);
            Tel_clientN = view.findViewById(R.id.tel_client);
            Sld_clientN = view.findViewById(R.id.sld_client);
            im_state = view.findViewById(R.id.imageId);
            img_client_location = view.findViewById(R.id.img_pos_client);
        }
    }


    public RecyclerAdapterRouting(Context context, List<PostData_Client> itemList) {
        this.clientList = itemList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routing, parent, false);
        itemLongClick = (ItemLongClick) parent.getContext();


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        PostData_Client item = clientList.get(position);

        holder.ClientN.setTextSize(17);
        holder.ClientN.setTypeface(null, Typeface.BOLD);
        holder.ClientN.setText(item.client);

        holder.Tel_clientN.setText("TEL : " + item.tel);

        holder.Sld_clientN.setTypeface(null, Typeface.BOLD);
        holder.Sld_clientN.setText("Solde : " + item.solde_montant);

        holder.Sld_clientN.setText("Solde :" + new DecimalFormat("##,##0.00").format(Double.valueOf(item.solde_montant)));

        if (item.latitude == 0.0) {
            holder.img_client_location.setImageResource(R.drawable.ic_baseline_wrong_location_24);
        } else {
            holder.img_client_location.setImageResource(R.drawable.ic_baseline_location_on_24);
        }

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemLongClick.onLongClick(view, holder.getAdapterPosition());
                return true;
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view, holder.getAdapterPosition());
            }
        });

        if (item.state == 0) {  //client n'est pas encore visité
            holder.im_state.setBackgroundColor(mContext.getColor(R.color.blue));
        } else if (item.state == 1) { //client visité sans vente
            holder.im_state.setBackgroundColor(mContext.getColor(R.color.jaune));
        } else if (item.state == 2) { //client visité avec vente
            holder.im_state.setBackgroundColor(mContext.getColor(R.color.tag_green_bg));
        }

    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public interface ItemClick {
        void onClick(View v, int position);
    }

    public interface ItemLongClick {
        void onLongClick(View v, int position);
    }

    public void refresh(List<PostData_Client> new_itemList) {
        clientList.clear();
        clientList.addAll(new_itemList);
        notifyDataSetChanged();
    }
}