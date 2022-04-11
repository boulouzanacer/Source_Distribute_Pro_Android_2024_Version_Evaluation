package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.util.ColorGeneratorModified;
import com.safesoft.proapp.distribute.util.MyCardView2;

import java.util.List;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class RecyclerAdapterAchat1 extends RecyclerView.Adapter<RecyclerAdapterAchat1.MyViewHolder> {

    private List<PostData_Achat1> achat1List;
    private int color = 0;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private ColorGeneratorModified generator;
    private Context mContext;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView NumAchat;
        TextView NomAchat;
        TextView NomClient;

        TextView Date_exp;
        TextView nbrProduit;
        TextView Date_achat;
        CardView cardView;
        SlantedTextView blocage;


        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.item_root);
            NumAchat = (TextView) view.findViewById(R.id.num_achat);
            NomAchat = (TextView) view.findViewById(R.id.nom_achat);
            NomClient = (TextView) view.findViewById(R.id.nom_client);

            Date_exp = (TextView) view.findViewById(R.id.date_exp);
            nbrProduit = (TextView) view.findViewById(R.id.nbr_p);
            Date_achat = (TextView) view.findViewById(R.id.date_achat);
            blocage = (SlantedTextView) view.findViewById(R.id.blocage);

        }
    }


    public RecyclerAdapterAchat1(Context context, List<PostData_Achat1> itemList) {
        this.achat1List = itemList;
        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new MyCardView2(parent.getContext(), R.layout.item_achat1_list);
        itemClick = (ItemClick) parent.getContext();

        itemLongClick = (ItemLongClick) parent.getContext();

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,int position) {
        PostData_Achat1 item = achat1List.get(position);

        holder.NumAchat.setTextSize(17);
        holder.NumAchat.setTypeface(null, Typeface.BOLD);
        holder.NumAchat.setText(""+item.num_achat);
        holder.NomClient.setText(""+item.fournisseur);

        holder.NomAchat.setText(""+item.nom_achat);

            /*
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
 */
        holder.Date_achat.setText(item.date_achat + " " + item.heure_achat);

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
        if(item.is_sent == 1){
            holder.blocage.setText("Exporté")
                    .setTextColor(Color.WHITE)
                    .setSlantedBackgroundColor(Color.GREEN)
                    .setTextSize(21)
                    .setSlantedLength(50)
                    .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);

            holder.Date_exp.setText(item.date_export_achat);
        }else {
            holder.blocage.setText("En attente")
                    .setTextColor(Color.WHITE)
                    .setSlantedBackgroundColor(Color.RED)
                    .setTextSize(21)
                    .setSlantedLength(50)
                    .setMode(SlantedTextView.MODE_RIGHT_BOTTOM);
            holder.Date_exp.setText("Pas encore exporté");
        }




        if (color == 0){
            if (generator!=null)
                color = generator.getColor(achat1List.get(position).num_achat);
        }
    }

    @Override
    public int getItemCount() {
        return achat1List.size();
    }

    public interface ItemClick{
        void onClick(View v, int position);
    }

    public interface ItemLongClick{
        void onLongClick(View v, int position);
    }

    public void refresh(List<PostData_Achat1> new_itemList){
        achat1List.clear();
        achat1List.addAll(new_itemList);
        notifyDataSetChanged();
    }
}