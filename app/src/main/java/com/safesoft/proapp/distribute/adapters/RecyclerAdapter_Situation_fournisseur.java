package com.safesoft.proapp.distribute.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_f;
import com.safesoft.proapp.distribute.utils.MyCardView_Situation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapter_Situation_fournisseur extends RecyclerView.Adapter<RecyclerAdapter_Situation_fournisseur.MyViewHolder> {

    int rowindex = -1;
    private final List<PostData_Carnet_f> carnet_f_tList;
    private ItemClick itemClick;
    private final Context mContext;
    private NumberFormat nf;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView montant_value;
        CardView cardView;
        LinearLayout lnr_item_root;
        TextView date_value;
        TextView heure_value;
        ImageButton btn_edit_situation, btn_remove_situation;

        MyViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.item_root);
            lnr_item_root = view.findViewById(R.id.lnr_item_root);

            montant_value = view.findViewById(R.id.montant_versement_value1);
            date_value = view.findViewById(R.id.date_versement_value1);
            heure_value = view.findViewById(R.id.heure_versement_value1);
            btn_edit_situation = view.findViewById(R.id.btn_edit_situation);
            btn_remove_situation = view.findViewById(R.id.btn_remove_situation);
            nf = NumberFormat.getInstance(Locale.US);
            ((DecimalFormat) nf).applyPattern("##,##0.00");


        }
    }


    public RecyclerAdapter_Situation_fournisseur(Context context, List<PostData_Carnet_f> itemList) {
        this.carnet_f_tList = itemList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView_Situation(parent.getContext());

        itemClick = (ItemClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        PostData_Carnet_f item = carnet_f_tList.get(position);
        holder.montant_value.setTextSize(17);
        holder.date_value.setTextSize(17);
        holder.heure_value.setTextSize(17);

        holder.montant_value.setText(String.valueOf(nf.format(item.carnet_versement)));
        holder.date_value.setText(item.carnet_date);
        holder.heure_value.setText(item.carnet_heure);

        // holder.btn_remove_situation.setBackgroundResource(R.drawable.delete_situation_selector);
        // holder.btn_edit_situation.setBackgroundResource(R.drawable.edit_situation__selector);


        holder.lnr_item_root.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                itemClick.onClick(view, holder.getAdapterPosition(), item);
                rowindex = position;
                notifyDataSetChanged();
            }
        });

        if (rowindex == position) {
            holder.lnr_item_root.setBackgroundColor(Color.BLUE);
        } else {
            holder.lnr_item_root.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.btn_edit_situation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view, holder.getAdapterPosition(), item);
            }
        });


        holder.btn_remove_situation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view, holder.getAdapterPosition(), item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return carnet_f_tList.size();
    }

    public interface ItemClick {
        void onClick(View v, int position, PostData_Carnet_f carnet_f);
    }
}