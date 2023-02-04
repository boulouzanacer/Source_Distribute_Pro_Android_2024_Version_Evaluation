package com.safesoft.proapp.distribute.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;
import com.safesoft.proapp.distribute.utils.MyCardView2;

import java.text.DecimalFormat;
import java.util.List;

import cn.nekocode.badge.BadgeDrawable;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterBon1 extends RecyclerView.Adapter<RecyclerAdapterBon1.MyViewHolder> {

    private final List<PostData_Bon1> bon1List;
    private int color = 0;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private String SOURCE;


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView NumBon;
        TextView NomClient;
        TextView Montant;
        TextView nbrProduit;
        TextView Date_bon;
        TextView Heure_bon;
        TextView Versement;
        CardView cardView;
        SlantedTextView blocage;
        LinearLayout lnr_versement;

        MyViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.item_root);
            NumBon = view.findViewById(R.id.num_bon);
            NomClient = view.findViewById(R.id.nom_client);
            Montant = view.findViewById(R.id.montant);
            nbrProduit = view.findViewById(R.id.nbr_p);
            Date_bon = view.findViewById(R.id.date_bon);
            Heure_bon = view.findViewById(R.id.heure_bon);
            Versement = view.findViewById(R.id.versement_bon);
            blocage = view.findViewById(R.id.blocage);
            lnr_versement = view.findViewById(R.id.lnr_versement);
        }
    }


    public RecyclerAdapterBon1(Context context, List<PostData_Bon1> itemList, String SOURCE) {
        this.bon1List = itemList;
        this.SOURCE =SOURCE;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView2(parent.getContext(), R.layout.item_bon1_list);
        itemClick = (ItemClick) parent.getContext();

        itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder,int position) {
        PostData_Bon1 item = bon1List.get(position);

        holder.NumBon.setTextSize(17);
        holder.NumBon.setTypeface(null, Typeface.BOLD);
        holder.NumBon.setText(""+item.num_bon);

        holder.NomClient.setText(""+item.client);

        if(item.montant_bon == null){
            item.montant_bon = 0.00;
        }
        if(item.verser == null){
            item.verser = 0.00;
        }

        if(SOURCE.equals("SALE")){
            holder.lnr_versement.setVisibility(View.VISIBLE);
            holder.Versement.setText(""+ new DecimalFormat("##,##0.00").format(item.verser) + " DA");
        }else{
            holder.lnr_versement.setVisibility(View.GONE);
        }

        holder.Montant.setText(""+ new DecimalFormat("##,##0.00").format(item.montant_bon) + " DA");

        if(item.nbr_p == null)
        {
            item.nbr_p = 0;
        }
        final BadgeDrawable drawable1 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .badgeColor(0xff303F9F)
                .textSize(35)
                .number(item.nbr_p)
                .build();


        SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable1.toSpannable()));

        holder.nbrProduit.setText(spannableString1);

        holder.Date_bon.setText(item.date_bon);

        holder.Heure_bon.setText(item.heure);

        holder.cardView.setOnClickListener(view -> itemClick.onClick(view,holder.getAdapterPosition()));

        holder.cardView.setOnLongClickListener(v -> {
            itemLongClick.onLongClick(v , holder.getAdapterPosition());
            return true;
        });


        if(item.blocage.equals("F")){
            holder.blocage.setText("Validé")
                    .setTextColor(Color.WHITE)
                    .setSlantedBackgroundColor(Color.GREEN)
                    .setTextSize(21)
                    .setSlantedLength(80)
                    .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);
        }else {
            holder.blocage.setText("En attente")
                    .setTextColor(Color.WHITE)
                    .setSlantedBackgroundColor(Color.RED)
                    .setTextSize(21)
                    .setSlantedLength(80)
                    .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);
        }


        if (color == 0){
            if (generator!=null)
                color = generator.getColor(bon1List.get(position).num_bon);
        }
    }

    @Override
    public int getItemCount() {
        return bon1List.size();
    }

    public interface ItemClick{
        void onClick(View v, int position);
    }

    public interface ItemLongClick{
        void onLongClick(View v, int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh(List<PostData_Bon1> new_itemList){
        bon1List.clear();
        bon1List.addAll(new_itemList);
        notifyDataSetChanged();
    }
}