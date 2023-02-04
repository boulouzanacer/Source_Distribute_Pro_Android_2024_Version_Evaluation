package com.safesoft.proapp.distribute.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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

public class RecyclerProduitCommande extends RecyclerView.Adapter<RecyclerProduitCommande.MyViewHolder> {

    private List<PostData_Produit> produitList;
    private ArrayList<PostData_Bon2> bon2_list;
    private int color = 0;
    private ItemClick itemClick;
    private ColorGeneratorModified generator;
    private Context mContext;
    private Activity mActivity;
    private EventBus bus;
    private String mode_tarif;
    private String PREFS = "ALL_PREFS";
    private boolean stock_moins = true;
    private String name_class;
    private boolean photo_pr = false;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Produit, Prix_vente, Qte_r, Colissage, stock_colis;
        ImageView photopr;
        CardView cardView;
        ScalingActivityAnimator mScalingActivityAnimator ;

        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.item_root);
            Produit = (TextView) view.findViewById(R.id.produit);
            Prix_vente = (TextView) view.findViewById(R.id.prix_u);
            Colissage = (TextView) view.findViewById(R.id.colissage);

            stock_colis = (TextView) view.findViewById(R.id.stock_colis);

            Qte_r = (TextView) view.findViewById(R.id.qte_r);
            photopr = view.findViewById(R.id.img_product);
            mScalingActivityAnimator = new ScalingActivityAnimator(mContext, mActivity, R.id.root_view, R.layout.pop_view);
            this.setIsRecyclable(false);
        }
    }


    public RecyclerProduitCommande(Context context, Activity activity, List<PostData_Produit> itemList, String mode_tarif, String name_class) {
        this.produitList = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        mContext = context;
        mActivity = activity;
        bus = EventBus.getDefault();
        this.mode_tarif = mode_tarif;
        bon2_list = new ArrayList<>();
        SharedPreferences prefs3 = mContext.getSharedPreferences(PREFS, mContext.MODE_PRIVATE);
        if (prefs3.getBoolean("PR_PRO", false)) {
            photo_pr = true;
        } else {
            photo_pr = false;
        }
        this.name_class = name_class;
        setHasStableIds(true);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView2(parent.getContext(), R.layout.item_product_select);

        itemClick = (ItemClick) parent.getContext();

        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final PostData_Produit item = produitList.get(position);

        holder.Produit.setTextSize(14);
        holder.Produit.setTypeface(null, Typeface.BOLD);
        holder.Produit.setText(item.produit);


        if(mode_tarif.equals("3")){
            holder.Prix_vente.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(item.pv3_ht)));
        }else if(mode_tarif.equals("2")){
            holder.Prix_vente.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(item.pv2_ht)));
        } else
            holder.Prix_vente.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(item.pv1_ht)));


        holder.Qte_r.setText(new DecimalFormat("##,##0.##").format(Double.valueOf(item.stock)));


        if(photo_pr)
        {
            if(item.photo != null)
            {
                holder.photopr.setImageBitmap(BitmapFactory.decodeByteArray(item.photo, 0, item.photo.length));
            }
        }




        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view,holder.getAdapterPosition());
            }
        });





        /*holder.Prix_vente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mScalingActivityAnimator.setPopViewHeightIsTwoThirdOfScreen();
                View popView1 = holder.mScalingActivityAnimator.start();
                Button mButtonBack1 = (Button) popView1.findViewById(R.id.btn_cancel);
                Button mButtonBack2 = (Button) popView1.findViewById(R.id.btn_sure);
                final EditText Val_P_v = (EditText) popView1.findViewById(R.id.edited_prix);
                Val_P_v.setText(holder.Prix_vente.getText().toString());
                mButtonBack1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mScalingActivityAnimator.resume();
                    }
                });

                mButtonBack2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!Val_P_v.getEditableText().toString().isEmpty()){

                            holder.Prix_vente.setText(Val_P_v.getText().toString());
                        }else{
                            holder.Prix_vente.setText("0.00");
                        }

                        if (holder.checkBox_produit.isChecked()) {

                            for (int b = 0; b < bon2_list.size(); b++) {
                                if (bon2_list.get(b).codebarre.equals(item.code_barre)) {
                                    bon2_list.get(b).p_u = holder.Prix_vente.getText().toString();
                                    if((!holder.qte.getText().toString().isEmpty()) && Integer.valueOf(holder.qte.getText().toString()) > 0) {
                                        bon2_list.get(b).montant_produit = String.valueOf(Double.valueOf(holder.qte.getText().toString()) * Double.valueOf(holder.Prix_vente.getText().toString()));

                                    }else{
                                        bon2_list.get(b).montant_produit = String.valueOf(0.0 * Double.valueOf(holder.Prix_vente.getText().toString()));
                                        holder.qte.setText("0");
                                    }

                                    // Post the event
                                    bus.post(new CheckedPanierEvent(bon2_list));

                                }
                            }

                        }

                        holder.mScalingActivityAnimator.resume();
                    }
                });
            }
        });*/


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
        void onClick(View v, int position);
    }

    public void refresh(List<PostData_Produit> new_itemList){
        produitList.clear();
        produitList.addAll(new_itemList);
        notifyDataSetChanged();
    }

    private void StockOverFlowMessage(Double qte){
        if(name_class.toString().equals("1")){
            Crouton.makeText((Activity)mContext, "Stock non disponible, Quantité "+String .valueOf(qte)+" est superieur du stock ", Style.ALERT).show();
        }else{
            Crouton.makeText((Activity)mContext, "Stock non disponible, Quantité "+String .valueOf(qte)+" est superieur du stock ", Style.ALERT).show();
        }
    }
}