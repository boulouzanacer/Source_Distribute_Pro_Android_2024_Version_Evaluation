package com.safesoft.pro.distribute.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.postData.PostData_Carnet_c;
import com.safesoft.pro.distribute.util.MyCardView_Situation;

import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapter_Situation extends RecyclerView.Adapter<RecyclerAdapter_Situation.MyViewHolder> {

    private List<PostData_Carnet_c> carnet_c_tList;
    private ItemClick itemClick;
    private Context mContext;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView montant_value;
        CardView cardView;
        TextView date_value;

        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.item_root);
            montant_value = (TextView) view.findViewById(R.id.montant_versement_value1);
            date_value = (TextView) view.findViewById(R.id.date_versement_value1);
        }
    }


    public RecyclerAdapter_Situation(Context context, List<PostData_Carnet_c> itemList) {
        this.carnet_c_tList = itemList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView_Situation(parent.getContext());
        // View itemView = LayoutInflater.from(parent.getContext())
        //       .inflate(R.layout.item_list, parent, false);
        itemClick = (ItemClick) parent.getContext();
       // itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,int position) {
        PostData_Carnet_c item = carnet_c_tList.get(position);

        holder.montant_value.setTextSize(17);
        //  holder.Montant.setTypeface(null, Typeface.BOLD);
        holder.montant_value.setText(item.carnet_versement);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view,holder.getAdapterPosition());
            }
        });




        holder.date_value.setTextSize(17);
        // holder.Date.setTypeface(null, Typeface.BOLD);
        holder.date_value.setText(item.carnet_date + "   " + item.carnet_heure);

    }

    @Override
    public int getItemCount() {
        return carnet_c_tList.size();
    }

    public interface ItemClick{
        void onClick(View v, int position);
    }



    public void refresh(List<PostData_Carnet_c> new_itemList){
        carnet_c_tList.clear();
        carnet_c_tList.addAll(new_itemList);
        notifyDataSetChanged();
    }
}