package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

  private List<PostData_Client> fournisList;
  private int color = 0;
  private ItemClick itemClick;
  private ColorGeneratorModified generator;
  private Context mContext;


  class MyViewHolder extends RecyclerView.ViewHolder {

    TextView ClientN;
    CardView cardView;
    ImageView image;
    ImageView img_pos_client;
    TextView Tel_clientN;
    TextView Sld_clientN;

    MyViewHolder(View view)
    {
      super(view);
      cardView = (CardView) view.findViewById(R.id.item_root);
      ClientN = (TextView) view.findViewById(R.id.client);
      Tel_clientN = (TextView) view.findViewById(R.id.tel_client);
      Sld_clientN = (TextView) view.findViewById(R.id.sld_client);
      image = (ImageView) view.findViewById(R.id.imageId);
      img_pos_client = (ImageView) view.findViewById(R.id.img_pos_client);
    }
  }


  public RecyclerAdapter(Context context, List<PostData_Client> itemList)
  {
    this.fournisList = itemList;
    if (color == 0)
      generator = ColorGeneratorModified.MATERIAL;
    mContext = context;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listfournis, parent, false);
    itemClick =(ItemClick) parent.getContext();


    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final MyViewHolder holder,int position) {

    PostData_Client item = fournisList.get(position);

    holder.ClientN.setTextSize(17);
    holder.ClientN.setTypeface(null, Typeface.BOLD);
    holder.ClientN.setText(item.client);

    holder.Tel_clientN.setText("TEL : "+ item.tel);

    holder.Sld_clientN.setTypeface(null, Typeface.BOLD);
    holder.Sld_clientN.setText("Solde : "+item.solde_montant);

    if(item.solde_montant == null)
    {
      holder.Sld_clientN.setText("Solde :"+ 0.00);
    }
    else
    {
      holder.Sld_clientN.setText("Solde :"+ new DecimalFormat("##,##0.00").format(Double.valueOf(item.solde_montant.toString())));

    }

    if(item.latitude == 0.0){
       holder.img_pos_client.setImageResource(R.drawable.ic_baseline_wrong_location_24);
    }else {
      holder.img_pos_client.setImageResource(R.drawable.ic_baseline_location_on_24);
    }
    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        itemClick.onClick(view,holder.getAdapterPosition());



      }
    });

    String firstChar = "NO";
    if(item.client != null){
      if(item.client.length() == 1){
        firstChar = String.valueOf(item.client.charAt(0));
      }else if(item.client.length() > 0){
        firstChar = String.valueOf(item.client.charAt(0))+ String.valueOf(item.client.charAt(1));
      }else{
        firstChar = "NO";
      }

    }

    if (color == 0)
    {
      if (generator!=null)
        color = generator.getColor(fournisList.get(position).client);
    }

    TextDrawable drawable = TextDrawable.builder().buildRound(firstChar.toUpperCase(), color);
    holder.image.setImageDrawable(drawable);
  }

  @Override
  public int getItemCount() {
    return fournisList.size();
  }

  public interface ItemClick{
    void onClick(View v, int position);
  }

  public void refresh(List<PostData_Client> new_itemList){
    fournisList.clear();
    fournisList.addAll(new_itemList);
    notifyDataSetChanged();
  }
}