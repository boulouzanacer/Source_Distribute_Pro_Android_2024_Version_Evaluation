package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;
import com.safesoft.proapp.distribute.utils.MyCardView2;

import java.util.List;

import cn.nekocode.badge.BadgeDrawable;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterInv1 extends RecyclerView.Adapter<RecyclerAdapterInv1.MyViewHolder> {

    private final List<PostData_Inv1> inv1List;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private final Context mContext;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView NumInv;
        TextView NomInventaire;
        TextView Date_exp;
        TextView nbrProduit;
        TextView Date_inv;
        TextView Heure_inv;
        CardView cardView;
        SlantedTextView blocage;

        MyViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.item_root);
            NumInv = view.findViewById(R.id.num_inv);
            NomInventaire = view.findViewById(R.id.nom_inv);
            Date_exp = view.findViewById(R.id.date_exp);
            nbrProduit = view.findViewById(R.id.nbr_p);
            Date_inv = view.findViewById(R.id.date_inv);
            Heure_inv = view.findViewById(R.id.heure_inv);
            blocage = view.findViewById(R.id.blocage);

        }
    }


    public RecyclerAdapterInv1(Context context, List<PostData_Inv1> itemList) {
        this.inv1List = itemList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView2(parent.getContext(), R.layout.item_inv1_list);

        itemClick = (ItemClick) parent.getContext();

        itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PostData_Inv1 item = inv1List.get(position);

        holder.NumInv.setTextSize(17);
        holder.NumInv.setTypeface(null, Typeface.BOLD);
        holder.NumInv.setText(item.num_inv);

        holder.NomInventaire.setText(item.nom_inv);


        final BadgeDrawable drawable1 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .badgeColor(0xff303F9F)
                .textSize(35)
                .number(item.nbr_produit)
                .build();


        SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable1.toSpannable()));

        holder.nbrProduit.setText(spannableString1);

        holder.Date_inv.setText(item.date_inv + " " + item.heure_inv);

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


        if (item.blocage.equals("F")) {
            holder.blocage.setText("Fermé")
                    .setTextColor(Color.WHITE)
                    .setSlantedBackgroundColor(Color.GREEN)
                    .setTextSize(21)
                    .setSlantedLength(80)
                    .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);

        } else {

            holder.blocage.setText("Ouvert")
                    .setTextColor(Color.WHITE)
                    .setSlantedBackgroundColor(Color.RED)
                    .setTextSize(21)
                    .setSlantedLength(80)
                    .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);

        }

        if (item.is_exported == 1) {
            holder.Date_exp.setText(item.date_export_inv);
        } else {
            holder.Date_exp.setText("Pas encore exporté");
        }

    }

    @Override
    public int getItemCount() {
        return inv1List.size();
    }

    public interface ItemClick {
        void onClick(View v, int position);
    }


    public interface ItemLongClick {
        void onLongClick(View v, int position);
    }


    public void refresh(List<PostData_Inv1> new_itemList) {
        inv1List.clear();
        inv1List.addAll(new_itemList);
        notifyDataSetChanged();
    }

}