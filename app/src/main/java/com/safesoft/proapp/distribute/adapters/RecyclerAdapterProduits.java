package com.safesoft.proapp.distribute.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterProduits extends RecyclerView.Adapter<RecyclerAdapterProduits.MyViewHolder> {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static final ExecutorService PHOTO_EXECUTOR = Executors.newFixedThreadPool(2);
    private static final LruCache<String, Bitmap> PHOTO_CACHE = createPhotoCache();

    private static LruCache<String, Bitmap> createPhotoCache() {
        int maxKb = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheKb = Math.max(1024, maxKb / 16);
        return new LruCache<String, Bitmap>(cacheKb) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return Math.max(1, value.getByteCount() / 1024);
            }
        };
    }

    private final List<PostData_Produit> produitList;
    private final PostData_Params params;
    private int color = 0;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private final Context mContext;
    private final boolean showProductPictures;
    private String prix_revendeur;
    DATABASE controller;
    private final DecimalFormat dfStock = new DecimalFormat("##,##0.##");
    private final DecimalFormat dfMoney = new DecimalFormat("##,##0.00");


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
        this.showProductPictures = context.getSharedPreferences("ALL_PREFS", Context.MODE_PRIVATE).getBoolean("SHOW_PROD_PIC", false);
        setHasStableIds(true);
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
            holder.colissage.setText(" " + dfStock.format(item.colissage));

            holder.stock_colis.setText(" " + dfStock.format(item.stock_colis));
            holder.colis_title.setText(R.string.Stock_colis);

            holder.stock_vrac.setText(" " + dfStock.format(item.stock_vrac));
            holder.vrac_title.setText(R.string.Stock_vrac);

        }
        if (item.stock_vrac == 0.0) {
            holder.stock_vrac.setText("");

        } else {
            holder.stock_vrac.setText(" " + dfStock.format(item.stock_vrac));
        }
        holder.stock.setText(" " + dfStock.format(item.stock));




        if (prix_revendeur.equals("Libre")) {
            // Show Lnr_pvX if conditions are met
            holder.prix_unit.setText(" " + dfMoney.format(item.pv1_ht * (1 + item.tva / 100)));

        } else {
            if((prix_revendeur.equals(params.pv1_titre))){
                holder.prix_unit.setText(" " + dfMoney.format(item.pv1_ht * (1 + item.tva / 100)));
            }else if(prix_revendeur.equals(params.pv2_titre)){
                holder.prix_unit.setText(" " + dfMoney.format(item.pv2_ht * (1 + item.tva / 100)));
            }else if(prix_revendeur.equals(params.pv3_titre)){
                holder.prix_unit.setText(" " + dfMoney.format(item.pv3_ht * (1 + item.tva / 100)));
            }else if(prix_revendeur.equals(params.pv4_titre)){
                holder.prix_unit.setText(" " + dfMoney.format(item.pv4_ht * (1 + item.tva / 100)));
            }else if(prix_revendeur.equals(params.pv5_titre)){
                holder.prix_unit.setText(" " + dfMoney.format(item.pv5_ht * (1 + item.tva / 100)));
            }else if(prix_revendeur.equals(params.pv6_titre)){
                holder.prix_unit.setText(" " + dfMoney.format(item.pv6_ht * (1 + item.tva / 100)));
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

        bindProductPhoto(holder.image, item.produit_id == null ? null : item.produit_id.toString());


    }

    private void bindProductPhoto(ImageView imageView, String produitId) {
        if (!showProductPictures) {
            imageView.setImageResource(R.drawable.ic_camera_24);
            imageView.setTag(null);
            return;
        }

        if (produitId == null || produitId.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_camera_24);
            imageView.setTag(null);
            return;
        }

        imageView.setTag(produitId);

        Bitmap cached = PHOTO_CACHE.get(produitId);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }

        imageView.setImageResource(R.drawable.ic_camera_24);

        PHOTO_EXECUTOR.execute(() -> {
            Bitmap bmp = controller.getProductPhotoBitmap(produitId);
            if (bmp != null) {
                PHOTO_CACHE.put(produitId, bmp);
            }
            MAIN_HANDLER.post(() -> {
                Object tag = imageView.getTag();
                if (!(tag instanceof String) || !produitId.equals(tag)) return;
                if (bmp != null) {
                    imageView.setImageBitmap(bmp);
                } else {
                    imageView.setImageResource(R.drawable.ic_camera_24);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }

    @Override
    public long getItemId(int position) {
        PostData_Produit item = produitList.get(position);
        if (item == null || item.produit_id == null) return RecyclerView.NO_ID;
        String idStr = item.produit_id.toString();
        if (idStr.isEmpty()) return RecyclerView.NO_ID;
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException ignored) {
            return idStr.hashCode();
        }
    }

    public interface ItemClick {
        void onClick(View v, int position);
    }


    public interface ItemLongClick {
        void onLongClick(View v, int position);
    }

}
