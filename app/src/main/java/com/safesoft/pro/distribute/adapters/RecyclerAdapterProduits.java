package com.safesoft.pro.distribute.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.pro.distribute.postData.PostData_Produit;
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.util.ColorGeneratorModified;
import com.safesoft.pro.distribute.util.MyCardView;

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

    MyViewHolder(View view) {
      super(view);

      cardView = (CardView) view.findViewById(R.id.item_root);
      Produit = (TextView) view.findViewById(R.id.client);
      image = (ImageView) view.findViewById(R.id.imageId);
      stock = (TextView) view.findViewById(R.id.stockP);

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
    // View itemView = LayoutInflater.from(parent.getContext())
    //       .inflate(R.layout.item_list, parent, false);
    itemClick = (ItemClick) parent.getContext();

    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final MyViewHolder holder,int position) {
    PostData_Produit item = produitList.get(position);

    holder.Produit.setTextSize(17);
    holder.Produit.setTypeface(null, Typeface.BOLD);
    holder.Produit.setText(item.produit);
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
        firstChar = String.valueOf(item.produit.charAt(0))+ String.valueOf(item.produit.charAt(1));
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
       TextDrawable drawable = TextDrawable.builder().buildRound(firstChar.toUpperCase(), color);
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