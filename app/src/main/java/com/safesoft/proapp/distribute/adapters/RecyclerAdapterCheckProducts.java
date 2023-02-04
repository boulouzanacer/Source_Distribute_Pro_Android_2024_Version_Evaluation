package com.safesoft.proapp.distribute.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;
import com.safesoft.proapp.distribute.utils.MyCardView2;
import com.safesoft.proapp.distribute.utils.ScalingActivityAnimator;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterCheckProducts extends RecyclerView.Adapter<RecyclerAdapterCheckProducts.MyViewHolder> {

    private List<PostData_Produit> produitList;
    ArrayList<PostData_Produit> temp_list = new ArrayList<>();
    private ArrayList<PostData_Bon2> bon2_list;
    private int color = 0;
    private ItemClick itemClick;
    private ColorGeneratorModified generator;
    private Context mContext;
    private Activity mActivity;
    private EventBus bus;
    private String mode_tarif;
    private String PREFS = "ALL_PREFS";
    private boolean stock_moins = false;
    private boolean photo_pr = false;
    private AlertDialog dialog;
    private String SOURCE_ACTIVITY;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Produit, Prix_vente, Qte_r, Colissage, Stock_colis,Stock_colis_title, Colissage_title;
        ImageView photopr;
        CardView cardView;
        ScalingActivityAnimator mScalingActivityAnimator ;

        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.item_root_select_produit);
            Produit = (TextView) view.findViewById(R.id.produit);
            Prix_vente = (TextView) view.findViewById(R.id.prix_u);
            Colissage = (TextView) view.findViewById(R.id.colissage);
            Colissage_title = (TextView) view.findViewById(R.id.colissage_title);
            Stock_colis = (TextView) view.findViewById(R.id.stock_colis);
            Stock_colis_title = (TextView) view.findViewById(R.id.stock_colis_title);

            Qte_r = (TextView) view.findViewById(R.id.qte_r);
            photopr = view.findViewById(R.id.img_product);
            //
           // Decrease = (Button) view.findViewById(R.id.btnDec);
            //qte = (EditText) view.findViewById(R.id.qte);
            mScalingActivityAnimator = new ScalingActivityAnimator(mContext, mActivity, R.id.root_view, R.layout.pop_view);
            this.setIsRecyclable(false);
        }
    }


    public RecyclerAdapterCheckProducts(Context context, Activity activity, List<PostData_Produit> itemList, String mode_tarif, AlertDialog dialog, String SOURCE_ACTIVITY) {
        this.produitList = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        mContext = context;
        mActivity = activity;
        bus = EventBus.getDefault();
        this.mode_tarif = mode_tarif;
        this.SOURCE_ACTIVITY = SOURCE_ACTIVITY;
        bon2_list = new ArrayList<>();
        SharedPreferences prefs3 = mContext.getSharedPreferences(PREFS, mContext.MODE_PRIVATE);
        if (prefs3.getBoolean("STOCK_MOINS", false)) {
            stock_moins = true;
        } else {
            stock_moins = false;
        }

        if (prefs3.getBoolean("PR_PRO", false)) {
            photo_pr = true;
        } else {
            photo_pr = false;
        }
        this.dialog = dialog;
        setHasStableIds(true);
    }


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

        if(mode_tarif.equals("3")){
            holder.Prix_vente.setText(new DecimalFormat("##,##0.00").format(item.pv3_ht));
        }else if(mode_tarif.equals("2")){
            holder.Prix_vente.setText(new DecimalFormat("##,##0.00").format(item.pv2_ht));
        } else
            holder.Prix_vente.setText(new DecimalFormat("##,##0.00").format(item.pv1_ht));

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

        if(item.photo != null)
        {
            holder.photopr.setImageBitmap(BitmapFactory.decodeByteArray(item.photo, 0, item.photo.length));
        }

//        if(photo_pr)
//        {
//            if(item.photo != null)
//            {
//                holder.photopr.setImageBitmap(BitmapFactory.decodeByteArray(item.photo, 0, item.photo.length));
//            }
//        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                itemClick.onClick(view, holder.getAdapterPosition(), item, SOURCE_ACTIVITY);

                dialog.dismiss();
            }
        });

        if (color == 0){
            if (generator!=null)
                color = generator.getColor(produitList.get(position).produit);
        }


    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface ItemClick{
        void onClick(View v, int position, PostData_Produit item, String SOURCE_ACTIVITY);
    }

    public void refresh(List<PostData_Produit> new_itemList){
        produitList.clear();
        produitList.addAll(new_itemList);
        notifyDataSetChanged();
    }

    private void StockOverFlowMessage(Double qte){
            Crouton.makeText((Activity)mContext, "Stock non disponible, Quantité "+String .valueOf(qte)+" est superieur du stock ", Style.ALERT).show();
    }
}