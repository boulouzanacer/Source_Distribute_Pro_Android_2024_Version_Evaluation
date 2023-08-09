package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;
import com.safesoft.proapp.distribute.utils.MyCardView2;

import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterTransfert1 extends RecyclerView.Adapter<RecyclerAdapterTransfert1.MyViewHolder> {

    private final List<PostData_Transfer1> transfert1List;
    private int color = 0;
    private ItemClick itemClick;
    private  ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private final Context mContext;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView NumBon;
        TextView Source;
        TextView Dest;
        TextView Date_bon;
        CardView cardView;
        ImageView image;

        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.item_root);
            NumBon = (TextView) view.findViewById(R.id.num_bon);
            Source = (TextView) view.findViewById(R.id.source);
            Dest = (TextView) view.findViewById(R.id.dest);
            Date_bon = (TextView) view.findViewById(R.id.date_bon);
            image = (ImageView) view.findViewById(R.id.imageId);
        }
    }


    public RecyclerAdapterTransfert1(Context context, List<PostData_Transfer1> itemList) {
        this.transfert1List = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView2(parent.getContext(), R.layout.item_transfer1_list);

        itemClick = (ItemClick) parent.getContext();
        itemLongClick = (ItemLongClick)parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,int position) {
        PostData_Transfer1 item = transfert1List.get(position);

        holder.NumBon.setTextSize(17);
        holder.NumBon.setTypeface(null, Typeface.BOLD);
        holder.NumBon.setText(item.num_bon);

        holder.Source.setTextSize(12);
        holder.Source.setTypeface(null, Typeface.BOLD);
        holder.Source.setText("Source : "+item.nom_depot_s);

        holder.Dest.setTextSize(12);
        holder.Dest.setTypeface(null, Typeface.BOLD);
        holder.Dest.setText("Destination : "+item.nom_depot_d);

        holder.Date_bon.setTextSize(12);
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

        String firstChar = "NO";
        if(item.nbr_p != null){
            if(item.nbr_p.length() > 0){
                if(item.nbr_p.length() == 1)
                    firstChar = String.valueOf(item.nbr_p.charAt(0));
                else if(item.nbr_p.length() == 2)
                    firstChar = String.valueOf(item.nbr_p.charAt(0))+ item.nbr_p.charAt(1);
                else if(item.nbr_p.length() == 3)
                    firstChar = String.valueOf(item.nbr_p.charAt(0)) + item.nbr_p.charAt(1) + item.nbr_p.charAt(2);
                else if(item.nbr_p.length() == 4)
                    firstChar = String.valueOf(item.nbr_p.charAt(0)) + item.nbr_p.charAt(1) + item.nbr_p.charAt(2) + item.nbr_p.charAt(3);
            }else{
                firstChar = "NO";
            }

        }

        if (color == 0){
            if (generator!=null)
                color = generator.getColor(transfert1List.get(position).num_bon);
        }

        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar.toUpperCase(), color);
        holder.image.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return transfert1List.size();
    }

    public interface ItemClick{
        void onClick(View v, int position);
    }
    public interface ItemLongClick{
        void onLongClick(View v, int position);
    }
    public void refresh(List<PostData_Transfer1> new_itemList){
        transfert1List.clear();
        transfert1List.addAll(new_itemList);
        notifyDataSetChanged();
    }
}