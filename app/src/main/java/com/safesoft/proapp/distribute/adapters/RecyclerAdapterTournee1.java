package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.haozhang.lib.SlantedTextView;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Tournee1;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;
import com.safesoft.proapp.distribute.utils.MyCardView2;

import java.util.List;

import cn.nekocode.badge.BadgeDrawable;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterTournee1 extends RecyclerView.Adapter<RecyclerAdapterTournee1.MyViewHolder> {

    private final List<PostData_Tournee1> tournee1List;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private final Context mContext;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView NumTournee;
        TextView NomTournee;
        TextView nbrClient;
        TextView Date_tournee;
        CardView cardView;
        SlantedTextView blocage;

        MyViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.item_root);
            NumTournee = view.findViewById(R.id.txtTourneeId_value);
            NomTournee = view.findViewById(R.id.txtTourneeName);
            nbrClient = view.findViewById(R.id.txtNbrClient_Value);
            Date_tournee = view.findViewById(R.id.txtDateTournee);
            blocage = view.findViewById(R.id.blocage);

        }
    }


    public RecyclerAdapterTournee1(Context context, List<PostData_Tournee1> itemList) {
        this.tournee1List = itemList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView2(parent.getContext(), R.layout.item_tournee1_list);

        itemClick = (ItemClick) parent.getContext();

        itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PostData_Tournee1 item = tournee1List.get(position);

        holder.NumTournee.setTextSize(17);
        holder.NumTournee.setTypeface(null, Typeface.BOLD);
        holder.NumTournee.setText(item.num_tournee);

        holder.NomTournee.setText(item.name_tournee);


        final BadgeDrawable drawable1 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .badgeColor(0xff303F9F)
                .textSize(35)
                .number(item.nbr_client)
                .build();


        SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable1.toSpannable()));

        holder.nbrClient.setText(spannableString1);

        holder.Date_tournee.setText(item.date_tournee);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(view, holder.getAdapterPosition());
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemLongClick.onLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });

       /* if (item.is_exported == 1) {
            holder.Date_create.setText(item.date_export_inv);
        } else {
            holder.Date_create.setText("Pas encore exporté");
        }*/

    }

    @Override
    public int getItemCount() {
        return tournee1List.size();
    }

    public interface ItemClick {
        void onClick(View v, int position);
    }


    public interface ItemLongClick {
        void onLongClick(View v, int position);
    }

}