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
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterFournisseurs extends RecyclerView.Adapter<RecyclerAdapterFournisseurs.MyViewHolder> {

    private final List<PostData_Fournisseur> fournisList;
    private int color = 0;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;

    private String PREFS = "ALL_PREFS";
    private SharedPreferences prefs;
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView FournisseurN;
        CardView cardView;
        ImageView image;
        ImageView img_pos_fournisseur;
        TextView Tel_fournisseurN;
        TextView Achats_fournisseurN;
        TextView Verser_fournisseurN;
        TextView Sold_fournisseurN;

        MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.item_root);
            FournisseurN = view.findViewById(R.id.fournisseur);
            Tel_fournisseurN = view.findViewById(R.id.tel_fournisseur);
            Achats_fournisseurN = view.findViewById(R.id.achat_fournisseur);
            Verser_fournisseurN = view.findViewById(R.id.verser_fournisseur);
            Sold_fournisseurN = view.findViewById(R.id.sold_fournisseur);
            image = view.findViewById(R.id.imageId);
            img_pos_fournisseur = view.findViewById(R.id.img_pos_fournisseur);

        }
    }


    public RecyclerAdapterFournisseurs(Context mContext, List<PostData_Fournisseur> itemList) {
        this.fournisList = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;

        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fournisseur, parent, false);

        itemClick = (ItemClick) parent.getContext();
        itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        PostData_Fournisseur item = fournisList.get(position);

        holder.FournisseurN.setTextSize(17);
        holder.FournisseurN.setTypeface(null, Typeface.BOLD);
        holder.FournisseurN.setText(item.fournis);

        holder.Tel_fournisseurN.setText("TEL : " + item.tel);

        holder.Achats_fournisseurN.setTypeface(null, Typeface.BOLD);
        holder.Verser_fournisseurN.setTypeface(null, Typeface.BOLD);
        holder.Sold_fournisseurN.setTypeface(null, Typeface.BOLD);
        //holder.Sld_fournisseurN.setText("Solde : "+item.solde_montant);

        holder.Achats_fournisseurN.setText("Achats :" + new DecimalFormat("##,##0.00").format(Double.valueOf(item.achat_montant)));
        holder.Verser_fournisseurN.setText("Verser :" + new DecimalFormat("##,##0.00").format(Double.valueOf(item.verser_montant)));

        if (prefs.getBoolean("AFFICHAGE_SOLDE_FOURNISSEUR", true)) {
            holder.Sold_fournisseurN.setText("Solde :" + new DecimalFormat("##,##0.00").format(Double.valueOf(item.solde_montant)));
        } else {
            holder.Sold_fournisseurN.setText("********");
        }

        if (item.latitude == 0.0) {
            holder.img_pos_fournisseur.setImageResource(R.drawable.ic_baseline_wrong_location_24);
        }else{
            holder.img_pos_fournisseur.setImageResource(R.drawable.ic_baseline_location_on_24);
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
                return false;
            }
        });


        String firstChar = "NO";
        if (item.fournis != null) {
            if (item.fournis.length() == 1) {
                firstChar = String.valueOf(item.fournis.charAt(0));
            } else if (item.fournis.length() > 0) {
                firstChar = String.valueOf(item.fournis.charAt(0)) + item.fournis.charAt(1);
            } else {
                firstChar = "NO";
            }
        }

        if (color == 0) {
            if (generator != null)
                color = generator.getColor(fournisList.get(position).fournis);
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

    public void refresh(List<PostData_Fournisseur> new_itemList) {
        fournisList.clear();
        fournisList.addAll(new_itemList);
        notifyDataSetChanged();
    }
}