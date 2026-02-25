package com.safesoft.proapp.distribute.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;
import com.safesoft.proapp.distribute.utils.MyCardView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterProduits extends RecyclerView.Adapter<RecyclerAdapterProduits.MyViewHolder> {

    private final List<PostData_Produit> produitList;
    private final PostData_Params params;
    private int color = 0;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private final Context mContext;
    private SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";
    private String prix_revendeur;
    DATABASE controller;


    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView Produit;
        ImageView image;
        TextView stock;
        TextView colissage, colissage_title;
        TextView stock_colis, colis_title;
        TextView stock_vrac, vrac_title;
        TextView prix_unit;

        MyViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.item_root_produit);
            Produit = view.findViewById(R.id.produit);
            image = view.findViewById(R.id.img_product);
            stock = view.findViewById(R.id.qte_r);
            stock_colis = view.findViewById(R.id.stock_colis);
            stock_vrac = view.findViewById(R.id.vrac);
            colissage = view.findViewById(R.id.colissage);
            prix_unit = view.findViewById(R.id.pu);
            colissage_title = view.findViewById(R.id.colissage_title);
            colis_title = view.findViewById(R.id.colis_title);
            vrac_title = view.findViewById(R.id.vrac_title);

        }
    }


    public RecyclerAdapterProduits(Context context, List<PostData_Produit> itemList, PostData_Params params, String prix_revendeur) {
        this.produitList = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        mContext = context;
        this.params = params;
        this.prix_revendeur = prix_revendeur;
        this.controller = new DATABASE(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView(parent.getContext(), R.layout.item_product_select);

        itemClick = (ItemClick) parent.getContext();
        itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PostData_Produit item = produitList.get(position);

        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        holder.Produit.setTextSize(17);
        holder.Produit.setTypeface(null, Typeface.BOLD);
        holder.Produit.setText(item.produit);

        if (item.colissage == 0.0) {
            holder.colissage_title.setText("");
            holder.colissage.setText("");

            holder.stock_colis.setText("");
            holder.colis_title.setText("");

            holder.stock_vrac.setText("");
            holder.vrac_title.setText("");
        } else {

            holder.colissage_title.setText(R.string.colis);
            holder.colissage.setText(" " + new DecimalFormat("##,##0.##").format(item.colissage));

            holder.stock_colis.setText(" " + new DecimalFormat("##,##0.##").format(item.stock_colis));
            holder.colis_title.setText(R.string.Stock_colis);

            holder.stock_vrac.setText(" " + new DecimalFormat("##,##0.##").format(item.stock_vrac));
            holder.vrac_title.setText(R.string.Stock_vrac);

        }
        if (item.stock_vrac == 0.0) {
            holder.stock_vrac.setText("");

        } else {
            holder.stock_vrac.setText(" " + new DecimalFormat("##,##0.##").format(item.stock_vrac));
        }
        holder.stock.setText(" " + new DecimalFormat("##,##0.##").format(item.stock));




        if (prix_revendeur.equals("Libre")) {
            // Show Lnr_pvX if conditions are met
            holder.prix_unit.setText(" " + new DecimalFormat("##,##0.00").format(item.pv1_ht));

        } else {
            if((prix_revendeur.equals(params.pv1_titre))){
                holder.prix_unit.setText(" " + new DecimalFormat("##,##0.00").format(item.pv1_ht));
            }else if(prix_revendeur.equals(params.pv2_titre)){
                holder.prix_unit.setText(" " + new DecimalFormat("##,##0.00").format(item.pv2_ht));
            }else if(prix_revendeur.equals(params.pv3_titre)){
                holder.prix_unit.setText(" " + new DecimalFormat("##,##0.00").format(item.pv3_ht));
            }else if(prix_revendeur.equals(params.pv4_titre)){
                holder.prix_unit.setText(" " + new DecimalFormat("##,##0.00").format(item.pv4_ht));
            }else if(prix_revendeur.equals(params.pv5_titre)){
                holder.prix_unit.setText(" " + new DecimalFormat("##,##0.00").format(item.pv5_ht));
            }else if(prix_revendeur.equals(params.pv6_titre)){
                holder.prix_unit.setText(" " + new DecimalFormat("##,##0.00").format(item.pv6_ht));
            }
        }




        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view, holder.getAdapterPosition());
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemLongClick.onLongClick(view, holder.getAdapterPosition());
                return false;
            }
        });

        String firstChar = "NO";
        if (item.produit != null) {
            if (item.produit.length() == 1) {
                firstChar = String.valueOf(item.produit.charAt(0));
            } else if (item.produit.length() > 1) {
                firstChar = item.produit.charAt(0) + String.valueOf(item.produit.charAt(1));
            } else {
                firstChar = "NO";
            }
        }


        if (color == 0) {
            if (generator != null)
                color = generator.getColor(produitList.get(position).produit);
        }

        if (prefs.getBoolean("SHOW_PROD_PIC", false)) {
            Bitmap bmp  = controller.getProductPhotoBitmap(item.produit_id.toString());
            if(bmp != null){
                holder.image.setImageBitmap(bmp);
            }else{
                holder.image.setImageResource(R.drawable.ic_camera_24);
            }
        }


    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }

    public interface ItemClick {
        void onClick(View v, int position);
    }


    public interface ItemLongClick {
        void onLongClick(View v, int position);
    }

}