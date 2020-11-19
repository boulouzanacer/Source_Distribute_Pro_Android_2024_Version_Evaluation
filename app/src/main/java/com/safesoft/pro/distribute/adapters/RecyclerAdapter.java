package com.safesoft.pro.distribute.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.postData.PostData_Client;
import com.safesoft.pro.distribute.util.ColorGeneratorModified;

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

    MyViewHolder(View view)
    {
      super(view);

      cardView = (CardView) view.findViewById(R.id.item_root);
      ClientN = (TextView) view.findViewById(R.id.client);
      image = (ImageView) view.findViewById(R.id.imageId);
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
    // View v = new MyCardView(parent.getContext());
    View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_listfournis, parent, false);
    //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listfournis, parent, false);
    itemClick =(ItemClick) parent.getContext();


    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final MyViewHolder holder,int position) {

    PostData_Client item = fournisList.get(position);

    holder.ClientN.setTextSize(17);
    holder.ClientN.setTypeface(null, Typeface.BOLD);
    holder.ClientN.setText(item.client);


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