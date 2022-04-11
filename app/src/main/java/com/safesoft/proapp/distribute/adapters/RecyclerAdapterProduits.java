package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.res.Resources;
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
import com.safesoft.proapp.distribute.util.ColorGeneratorModified;
import com.safesoft.proapp.distribute.util.MyCardView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterProduits extends RecyclerView.Adapter<RecyclerAdapterProduits.MyViewHolder> {

  private List<PostData_Produit> produitList;
  private int color = 0;
  private ItemClick itemClick;
  private ColorGeneratorModified generator;
  private Context mContext;


  class MyViewHolder extends RecyclerView.ViewHolder {

    TextView Produit;
    CardView cardView;
    ImageView image;
    TextView stock;
    TextView colissage;
    TextView stock_colis;
    TextView stock_vrac;
    LinearLayout ly_colissage, ly_stock_colis;


    MyViewHolder(View view) {
      super(view);

      cardView = (CardView) view.findViewById(R.id.item_root);
      Produit = (TextView) view.findViewById(R.id.produit);
      image = (ImageView) view.findViewById(R.id.imageId);
      stock = (TextView) view.findViewById(R.id.stock);
      stock_colis = (TextView) view.findViewById(R.id.colis);
      stock_vrac = (TextView) view.findViewById(R.id.vrac);
      colissage = (TextView) view.findViewById(R.id.colissage);
      ly_colissage = (LinearLayout) view.findViewById(R.id.layout_colissage);
      ly_stock_colis = (LinearLayout) view.findViewById(R.id.layout_stock_colis);
      ly_colissage.setVisibility(View.GONE);
      ly_stock_colis.setVisibility(View.GONE);

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

    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final MyViewHolder holder,int position) {
    PostData_Produit item = produitList.get(position);

    holder.Produit.setTextSize(17);
    holder.Produit.setTypeface(null, Typeface.BOLD);
    holder.Produit.setText(item.produit);
    if(item.colissage == 0.0){
      holder.ly_colissage.setVisibility(View.GONE);
      holder.ly_stock_colis.setVisibility(View.GONE);
    }else {
      holder.ly_colissage.setVisibility(View.VISIBLE);
      holder.ly_stock_colis.setVisibility(View.VISIBLE);
      holder.stock_colis.setText(String.valueOf(item.stock_colis));
      holder.stock_vrac.setText(String.valueOf(item.stock_vrac));
      holder.colissage.setText(" "+ new DecimalFormat("##,##0.##").format(Double.valueOf(item.colissage)));
    }

    if(item.stock == null)
    {
      holder.stock.setText(" "+ 0.00);
    }
    else
    {
      holder.stock.setText(" "+ new DecimalFormat("##,##0.00").format(Double.valueOf(item.stock.toString())));

    }

    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        itemClick.onClick(view,holder.getAdapterPosition());
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


    if(item.photo != null)
    {
      holder.image.setImageBitmap(BitmapFactory.decodeByteArray(item.photo, 0, item.photo.length));

    }else
    {
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

  public void refresh(List<PostData_Produit> new_itemList){
    produitList.clear();
    produitList.addAll(new_itemList);
    notifyDataSetChanged();
  }
}