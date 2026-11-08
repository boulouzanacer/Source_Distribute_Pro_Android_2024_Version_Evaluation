package com.safesoft.proapp.distribute.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;
import com.safesoft.proapp.distribute.utils.MyCardView;
import com.safesoft.proapp.distribute.utils.MyCardView2;
import com.safesoft.proapp.distribute.utils.ScalingActivityAnimator;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterCheckProducts extends RecyclerView.Adapter<RecyclerAdapterCheckProducts.MyViewHolder> {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static final ExecutorService PHOTO_EXECUTOR = Executors.newFixedThreadPool(2);
    private static final LruCache<String, Bitmap> PHOTO_CACHE = createPhotoCache();

    private static LruCache<String, Bitmap> createPhotoCache() {
        int maxKb = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheKb = Math.max(1024, maxKb / 16);
        return new LruCache<String, Bitmap>(cacheKb) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return Math.max(1, value.getByteCount() / 1024);
            }
        };
    }

    private final List<PostData_Produit> produitList;
    private int color = 0;
    private ItemClick itemClick;
    private ColorGeneratorModified generator;
    private final Context mContext;
    private final Activity mActivity;
    private final String mode_tarif;
    private final boolean showProductPictures;

    private final AlertDialog dialog;
    private final String SOURCE;
    DATABASE controller;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Produit, Prix, Qte_r, Colissage, Stock_colis, Stock_colis_title, Colissage_title;
        ImageView photopr;
        CardView cardView;
        ScalingActivityAnimator mScalingActivityAnimator;

        MyViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.item_root_select_produit);
            Produit = view.findViewById(R.id.produit);
            Prix = view.findViewById(R.id.prix_u);
            Colissage = view.findViewById(R.id.colissage);
            Colissage_title = view.findViewById(R.id.colissage_title);
            Stock_colis = view.findViewById(R.id.stock_colis);
            Stock_colis_title = view.findViewById(R.id.stock_colis_title);

            Qte_r = view.findViewById(R.id.qte_r);
            photopr = view.findViewById(R.id.img_product);

            mScalingActivityAnimator = new ScalingActivityAnimator(mContext, mActivity, R.id.root_view, R.layout.pop_view);
        }
    }


    public RecyclerAdapterCheckProducts(Context context, Activity activity, List<PostData_Produit> itemList, String mode_tarif, AlertDialog dialog, String SOURCE) {
        this.produitList = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        this.mContext = context;
        this.mActivity = activity;
        this.SOURCE = SOURCE;
        this.mode_tarif = mode_tarif;
        this.dialog = dialog;
        this.controller = new DATABASE(context);
        this.showProductPictures = context.getSharedPreferences("ALL_PREFS", Context.MODE_PRIVATE).getBoolean("SHOW_PROD_PIC", false);
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView2(parent.getContext(), R.layout.item_product_select);
        itemClick = (ItemClick) parent.getContext();

        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final PostData_Produit item = produitList.get(position);

        holder.Produit.setText(item.produit);

        if (SOURCE.equals("ACHAT")) {
            holder.Prix.setText(new DecimalFormat("##,##0.00").format(item.pamp));
        } else if (SOURCE.equals("VENTE")) {

            switch (mode_tarif) {
                case "6" ->
                        holder.Prix.setText(new DecimalFormat("##,##0.00").format((item.pv6_ht * item.tva / 100) + item.pv6_ht));
                case "5" ->
                        holder.Prix.setText(new DecimalFormat("##,##0.00").format((item.pv5_ht * item.tva / 100) + item.pv5_ht));
                case "4" ->
                        holder.Prix.setText(new DecimalFormat("##,##0.00").format((item.pv4_ht * item.tva / 100) + item.pv4_ht));
                case "3" ->
                        holder.Prix.setText(new DecimalFormat("##,##0.00").format((item.pv3_ht * item.tva / 100) + item.pv3_ht));
                case "2" ->
                        holder.Prix.setText(new DecimalFormat("##,##0.00").format((item.pv2_ht * item.tva / 100) + item.pv2_ht));
                default ->
                        holder.Prix.setText(new DecimalFormat("##,##0.00").format((item.pv1_ht * item.tva / 100) + item.pv1_ht));
            }

        }

        holder.Stock_colis.setText(new DecimalFormat("##,##0.##").format(item.stock_colis));

        if (item.colissage == 0.0) {
            holder.Colissage.setText("");
            holder.Colissage_title.setText("");
        } else {
            holder.Colissage.setText(new DecimalFormat("##,##0.##").format(item.colissage));
            holder.Colissage_title.setText("Colis.");
        }
        if (item.stock_colis == 0) {
            holder.Stock_colis.setText("");
            holder.Stock_colis_title.setText("");
        } else {
            holder.Stock_colis.setText(new DecimalFormat("##,##0.##").format(item.stock_colis));
            holder.Stock_colis_title.setText("Stock colis");
        }
        holder.Qte_r.setText(new DecimalFormat("##,##0.##").format(item.stock));


        bindProductPhoto(holder.photopr, item.produit_id == null ? null : item.produit_id.toString());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    itemClick.onClick(view, holder.getBindingAdapterPosition(), item);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                dialog.dismiss();
            }
        });

        if (color == 0) {
            if (generator != null)
                color = generator.getColor(produitList.get(position).produit);
        }

    }

    private void bindProductPhoto(@NonNull ImageView imageView, String produitId) {
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
        void onClick(View v, int position, PostData_Produit item) throws ParseException;
    }
}
