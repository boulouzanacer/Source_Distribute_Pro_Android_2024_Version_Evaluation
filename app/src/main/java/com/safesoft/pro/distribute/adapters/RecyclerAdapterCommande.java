package com.safesoft.pro.distribute.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.safesoft.pro.distribute.postData.PostData_Achat1;
import com.safesoft.pro.distribute.postData.PostData_Bon1;
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.util.ColorGeneratorModified;
import com.safesoft.pro.distribute.util.MyCardView2;

import java.text.DecimalFormat;
import java.util.List;

import cn.nekocode.badge.BadgeDrawable;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterCommande extends RecyclerView.Adapter<RecyclerAdapterCommande.MyViewHolder> {

  private List<PostData_Bon1> bon1List;
  private int color = 0;
  private ItemClick itemClick;
  private ItemLongClick itemLongClick;
  private ColorGeneratorModified generator;
  private Context mContext;


  class MyViewHolder extends RecyclerView.ViewHolder {

    TextView NumBon;
    TextView NomClient;
    TextView Montant;
    TextView nbrProduit;
    TextView Date_bon;
    CardView cardView;
    SlantedTextView blocage;

    MyViewHolder(View view) {
      super(view);

      cardView = (CardView) view.findViewById(R.id.item_root);
      NumBon = (TextView) view.findViewById(R.id.num_bon);
      NomClient = (TextView) view.findViewById(R.id.nom_client);
      Montant = (TextView) view.findViewById(R.id.montant);
      nbrProduit = (TextView) view.findViewById(R.id.nbr_p);
      Date_bon = (TextView) view.findViewById(R.id.date_bon);
      blocage = (SlantedTextView) view.findViewById(R.id.blocage);
    }
  }


  public RecyclerAdapterCommande(Context context, List<PostData_Bon1> itemList) {
    this.bon1List = itemList;
    if (color == 0)
      generator = ColorGeneratorModified.MATERIAL;
    mContext = context;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = new MyCardView2(parent.getContext(), R.layout.item_bon1_list);
    // View itemView = LayoutInflater.from(parent.getContext())
    //       .inflate(R.layout.item_list, parent, false);
    itemClick = (ItemClick) parent.getContext();

    itemLongClick = (ItemLongClick) parent.getContext();

    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final MyViewHolder holder,int position) {
    PostData_Bon1 item = bon1List.get(position);

    holder.NumBon.setTextSize(17);
    holder.NumBon.setTypeface(null, Typeface.BOLD);
    holder.NumBon.setText(""+item.num_bon);

    holder.NomClient.setText(""+item.client);

    if(item.montant_bon == null){
      item.montant_bon = "0.00";
    }
    holder.Montant.setText(""+ new DecimalFormat("##,##0.00").format(Double.valueOf(item.montant_bon)) + " DA");

    if(item.nbr_p == null){
      item.nbr_p = "0";
    }
    final BadgeDrawable drawable1 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_NUMBER)
            .badgeColor(0xff303F9F)
            .textSize(35)
            .number(Integer.valueOf(item.nbr_p))
            .build();


    SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable1.toSpannable()));

    holder.nbrProduit.setText(spannableString1);

    holder.Date_bon.setText(item.date_bon);

    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        itemClick.onClick(view,holder.getAdapterPosition());
      }
    });

    holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        itemLongClick.onLongClick(v , holder.getAdapterPosition());
        return true;
      }
    });

    if(item.blocage.equals("F")){
      holder.blocage.setText("Validé")
              .setTextColor(Color.WHITE)
              .setSlantedBackgroundColor(Color.GREEN)
              .setTextSize(21)
              .setSlantedLength(50)
              .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);
    }else if(item.blocage.equals("V")){
      holder.blocage.setText("Livré")
              .setTextColor(Color.WHITE)
              .setSlantedBackgroundColor(Color.BLUE)
              .setTextSize(21)
              .setSlantedLength(50)
              .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);
    }else{
      holder.blocage.setText("En attente")
              .setTextColor(Color.WHITE)
              .setSlantedBackgroundColor(Color.RED)
              .setTextSize(21)
              .setSlantedLength(50)
              .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);
    }


    if (color == 0){
      if (generator!=null)
        color = generator.getColor(bon1List.get(position).num_bon);
    }
  }

  @Override
  public int getItemCount() {
    return bon1List.size();
  }

  public interface ItemClick{
    void onClick(View v, int position);
  }

  public interface ItemLongClick{
    void onLongClick(View v, int position);
  }

  public void refresh(List<PostData_Bon1> new_itemList){
    bon1List.clear();
    bon1List.addAll(new_itemList);
    notifyDataSetChanged();
  }
}