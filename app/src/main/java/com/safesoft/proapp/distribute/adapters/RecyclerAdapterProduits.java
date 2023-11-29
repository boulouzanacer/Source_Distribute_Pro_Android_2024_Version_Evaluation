package com.safesoft.proapp.distribute.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
  private int color = 0;
  private ItemClick itemClick;
  private ItemLongClick itemLongClick;
  private ColorGeneratorModified generator;
  private final Context mContext;
  SharedPreferences prefs;
  private final String PREFS = "ALL_PREFS";


  class MyViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView Produit;
    ImageView image;
    TextView stock;
    TextView colissage,colissage_title;
    TextView stock_colis,colis_title;
    TextView stock_vrac,vrac_title;
    TextView prix_unit;
    LinearLayout ly_colissage, ly_stock_colis;


    MyViewHolder(View view) {
      super(view);

      cardView = (CardView) view.findViewById(R.id.item_root_produit);
      Produit = (TextView) view.findViewById(R.id.produit);
      image = (ImageView) view.findViewById(R.id.img_product);
      stock = (TextView) view.findViewById(R.id.qte_r);
      stock_colis = (TextView) view.findViewById(R.id.stock_colis);
      stock_vrac = (TextView) view.findViewById(R.id.vrac);
      colissage = (TextView) view.findViewById(R.id.colissage);
      prix_unit = (TextView) view.findViewById(R.id.pu);
      colissage_title = (TextView) view.findViewById(R.id.colissage_title);
      colis_title = (TextView) view.findViewById(R.id.colis_title);
      vrac_title = (TextView) view.findViewById(R.id.vrac_title);
      //ly_colissage = (LinearLayout) view.findViewById(R.id.layout_colissage);
      //ly_stock_colis = (LinearLayout) view.findViewById(R.id.layout_stock_colis);

    }
  }


  public RecyclerAdapterProduits(Context context, List<PostData_Produit> itemList) {
    this.produitList = itemList;
    if (color == 0)
      generator = ColorGeneratorModified.MATERIAL;
    mContext = context;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = new MyCardView(parent.getContext());

    itemClick = (ItemClick) parent.getContext();
    itemLongClick = (ItemLongClick) parent.getContext();

    return new MyViewHolder(v);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onBindViewHolder(final MyViewHolder holder,int position) {
    PostData_Produit item = produitList.get(position);

    prefs = mContext.getSharedPreferences(PREFS, mContext.MODE_PRIVATE);

    holder.Produit.setTextSize(17);
    holder.Produit.setTypeface(null, Typeface.BOLD);
    holder.Produit.setText(item.produit);

    if(item.colissage == 0.0){
      holder.colissage_title.setText("");
      holder.colissage.setText("");

      holder.stock_colis.setText("");
      holder.colis_title.setText("");

      holder.stock_vrac.setText("");
      holder.vrac_title.setText("");
    }else {

      holder.colissage_title.setText(R.string.colis);
      holder.colissage.setText(" "+ new DecimalFormat("##,##0.##").format(Double.valueOf(item.colissage)));

      holder.stock_colis.setText(" "+ new DecimalFormat("##,##0.##").format(Double.valueOf(item.stock_colis)));
      holder.colis_title.setText(R.string.Stock_colis);

      holder.stock_vrac.setText(" "+ new DecimalFormat("##,##0.##").format(Double.valueOf(item.stock_vrac)));
      holder.vrac_title.setText(R.string.Stock_vrac);

        }
    if(item.stock_vrac == 0.0){
          holder.stock_vrac.setText("");

        }else{
          holder.stock_vrac.setText(" "+ new DecimalFormat("##,##0.##").format(Double.valueOf(item.stock_vrac)));
        }
    holder.stock.setText(" "+ new DecimalFormat("##,##0.##").format(Double.valueOf(item.stock)));
    holder.prix_unit.setText(" "+ new DecimalFormat("##,##0.00").format(Double.valueOf(item.pv1_ht)));


    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        itemClick.onClick(view,holder.getAdapterPosition());
      }
    });

    holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View view) {
        itemLongClick.onLongClick(view,holder.getAdapterPosition());
        return false;
      }
    });

    String firstChar = "NO";
    if(item.produit != null){
      if(item.produit.length() == 1){
       firstChar = String.valueOf(item.produit.charAt(0));
      }
      else if(item.produit.length() > 1){
        firstChar = item.produit.charAt(0)+ String.valueOf(item.produit.charAt(1));
      }else{
        firstChar = "NO";
      }
    }


   if (color == 0){
     if (generator!=null)
        color = generator.getColor(produitList.get(position).produit);
    }

   if(prefs.getBoolean("SHOW_PROD_PIC", false)){
     if(item.photo != null)
     {
       holder.image.setImageBitmap(BitmapFactory.decodeByteArray(item.photo, 0, item.photo.length));

     }else
     {
       TextDrawable drawable = TextDrawable.builder().buildRound(firstChar.toUpperCase(), ContextCompat.getColor(mContext, R.color.blue));
       holder.image.setImageDrawable(drawable);
     }
   }else{
     TextDrawable drawable = TextDrawable.builder().buildRound(firstChar.toUpperCase(), ContextCompat.getColor(mContext, R.color.blue));
     holder.image.setImageDrawable(drawable);
   }

  }

  @Override
  public int getItemCount() {
    return produitList.size();
  }

  public interface ItemClick{
    void onClick(View v, int position);
  }


  public interface ItemLongClick{
    void onLongClick(View v, int position);
  }

}