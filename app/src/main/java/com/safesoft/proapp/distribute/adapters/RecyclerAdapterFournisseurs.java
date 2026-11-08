package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private final Context mContext;
    private int color = 0;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;

    private String PREFS = "ALL_PREFS";
    private SharedPreferences prefs;

    private static String fmtDA(double v) {
        return String.format(java.util.Locale.FRANCE, "%,.2f DA", v);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView FournisseurN;
        CardView cardView;
        ImageView image;
        ImageView img_pos_fournisseur;
        TextView Tel_fournisseurN;
        TextView Code_fournisseur_row;
        TextView Achats_fournisseurN;
        TextView Verser_fournisseurN;
        TextView Sold_fournisseurN;
        TextView Achats_cell;
        TextView Vers_cell;
        LinearLayout CellSoldeFournisseur;

        MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.item_root);
            FournisseurN = view.findViewById(R.id.fournisseur);
            Tel_fournisseurN = view.findViewById(R.id.tel_fournisseur);
            Code_fournisseur_row = view.findViewById(R.id.code_fournisseur_row);
            Achats_fournisseurN = view.findViewById(R.id.achat_fournisseur);
            Verser_fournisseurN = view.findViewById(R.id.verser_fournisseur);
            Sold_fournisseurN = view.findViewById(R.id.sold_fournisseur);
            Achats_cell = view.findViewById(R.id.achats_fournisseur_cell);
            Vers_cell = view.findViewById(R.id.verser_fournisseur_cell);
            CellSoldeFournisseur = view.findViewById(R.id.cell_solde_fournisseur);
            image = view.findViewById(R.id.imageId);
            img_pos_fournisseur = view.findViewById(R.id.img_pos_fournisseur);

        }
    }


    public RecyclerAdapterFournisseurs(Context mContext, List<PostData_Fournisseur> itemList) {
        this.mContext = mContext;
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

        holder.Code_fournisseur_row.setText(item.code_frs != null ? item.code_frs : "-");

        try {
            holder.Achats_cell.setText(fmtDA(item.achat_montant));
            holder.Vers_cell.setText(fmtDA(item.verser_montant));
        } catch (Exception e) {
            holder.Achats_cell.setText("0,00 DA");
            holder.Vers_cell.setText("0,00 DA");
        }

        if (prefs.getBoolean("AFFICHAGE_SOLDE_FOURNISSEUR", true)) {
            try {
                double solde = item.solde_montant;
                holder.Sold_fournisseurN.setText(fmtDA(solde));

                int bgResId;
                int textColor;
                if (solde >= 0.001) {
                    bgResId = R.drawable.bg_cell_red;
                    textColor = ContextCompat.getColor(mContext, R.color.solde_debiteur_text);
                } else if (solde <= -0.001) {
                    bgResId = R.drawable.bg_cell_emerald;
                    textColor = ContextCompat.getColor(mContext, R.color.solde_crediteur_text);
                } else {
                    bgResId = R.drawable.bg_cell_blue;
                    textColor = ContextCompat.getColor(mContext, R.color.solde_zero_text);
                }
                holder.CellSoldeFournisseur.setBackgroundResource(bgResId);
                holder.Sold_fournisseurN.setTextColor(textColor);
                if (holder.CellSoldeFournisseur.getChildCount() > 0 && holder.CellSoldeFournisseur.getChildAt(0) instanceof TextView) {
                    ((TextView) holder.CellSoldeFournisseur.getChildAt(0)).setTextColor(textColor);
                }
                try {
                    GradientDrawable gd = (GradientDrawable) holder.CellSoldeFournisseur.getBackground();
                    if (solde >= 0.001) {
                        gd.setStroke((int) (1 * mContext.getResources().getDisplayMetrics().density),
                                ContextCompat.getColor(mContext, R.color.solde_debiteur_stroke));
                    } else if (solde <= -0.001) {
                        gd.setStroke((int) (1 * mContext.getResources().getDisplayMetrics().density),
                                ContextCompat.getColor(mContext, R.color.solde_crediteur_stroke));
                    } else {
                        gd.setStroke((int) (1 * mContext.getResources().getDisplayMetrics().density),
                                ContextCompat.getColor(mContext, R.color.solde_zero_stroke));
                    }
                } catch (Exception ignore) {}
            } catch (Exception e) {
                holder.Sold_fournisseurN.setText("0,00 DA");
            }
        } else {
            holder.Achats_cell.setText("**** DA");
            holder.Vers_cell.setText("**** DA");
            holder.Sold_fournisseurN.setText("******** DA");
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