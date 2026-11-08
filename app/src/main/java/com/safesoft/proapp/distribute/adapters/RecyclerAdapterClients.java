package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.utils.ColorGeneratorModified;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerAdapterClients extends RecyclerView.Adapter<RecyclerAdapterClients.MyViewHolder> {

    private final List<PostData_Client> clientList;
    private final Context mContext;
    private final SharedPreferences prefs;
    private final ColorGeneratorModified generator;
    private ItemClick itemClick;
    private ItemLongClick itemLongClick;
    private OnEncaisserListener encaisserListener;
    private final String PREFS = "ALL_PREFS";

    private static String fmtDA(double v) {
        return String.format(java.util.Locale.FRANCE, "%,.2f DA", v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ClientN;
        CardView cardView;
        ImageView image;
        ImageView img_pos_client;
        TextView Tel_clientN;
        TextView Sld_clientN;
        TextView Achats_clientN;
        TextView Vers_clientN;
        LinearLayout CellSoldeClient;
        Button btnEncaisser;

        MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.item_root);
            ClientN = view.findViewById(R.id.client);
            Tel_clientN = view.findViewById(R.id.tel_client);
            Sld_clientN = view.findViewById(R.id.sld_client);
            Achats_clientN = view.findViewById(R.id.achats_client);
            Vers_clientN = view.findViewById(R.id.vers_client);
            CellSoldeClient = view.findViewById(R.id.cell_solde_client);
            image = view.findViewById(R.id.imageId);
            img_pos_client = view.findViewById(R.id.img_pos_client);
            btnEncaisser = view.findViewById(R.id.btn_encaisser_client);
        }
    }

    // ✅ UPDATED constructor (IMPORTANT FIX)
    public RecyclerAdapterClients(Context context,
                                  List<PostData_Client> itemList,
                                  ItemClick click,
                                  ItemLongClick longClick,
                                  OnEncaisserListener encaisser) {

        this.clientList = itemList;
        this.mContext = context;
        this.itemClick = click;
        this.itemLongClick = longClick;
        this.encaisserListener = encaisser;

        this.prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.generator = ColorGeneratorModified.MATERIAL;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clients, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        PostData_Client item = clientList.get(position);

        holder.ClientN.setTextSize(17);
        holder.ClientN.setTypeface(null, Typeface.BOLD);
        holder.ClientN.setText(item.client);

        holder.Tel_clientN.setText("TEL : " + item.tel);

        // —— ACHATS (contrôlé par SHOW_ACHAT_CLIENT) ——
        if (prefs.getBoolean("SHOW_ACHAT_CLIENT", false)) {
            try {
                holder.Achats_clientN.setText(fmtDA(item.achat_client));
            } catch (Exception e) {
                holder.Achats_clientN.setText("0,00 DA");
            }
        } else {
            holder.Achats_clientN.setText("**** DA");
        }

        // —— VERSEMENTS : TOUJOURS AFFICHÉS ——
        try {
            holder.Vers_clientN.setText(fmtDA(item.verser_client));
        } catch (Exception e) {
            holder.Vers_clientN.setText("0,00 DA");
        }

        // —— SOLDE (contrôlé par AFFICHAGE_SOLDE_CLIENT) ——
        if (prefs.getBoolean("AFFICHAGE_SOLDE_CLIENT", true)) {
            try {
                double solde = item.solde_client;
                holder.Sld_clientN.setText(fmtDA(solde));

                int bgResId;
                int textColor;
                if (solde >= 0.001) {
                    bgResId = R.drawable.bg_cell_red;
                    textColor = ContextCompat.getColor(mContext, R.color.solde_debiteur_text);
                } else if (solde <= -0.001) {
                    bgResId = R.drawable.bg_cell_emerald;
                    textColor = ContextCompat.getColor(mContext, R.color.solde_crediteur_text);
                } else {
                    bgResId = R.drawable.bg_cell_blue;
                    textColor = ContextCompat.getColor(mContext, R.color.solde_zero_text);
                }
                holder.CellSoldeClient.setBackgroundResource(bgResId);
                holder.Sld_clientN.setTextColor(textColor);
                if (holder.CellSoldeClient.getChildCount() > 0 && holder.CellSoldeClient.getChildAt(0) instanceof TextView) {
                    ((TextView) holder.CellSoldeClient.getChildAt(0)).setTextColor(textColor);
                }
                try {
                    GradientDrawable gd = (GradientDrawable) holder.CellSoldeClient.getBackground();
                    if (solde >= 0.001) {
                        gd.setStroke((int) (1 * mContext.getResources().getDisplayMetrics().density),
                                ContextCompat.getColor(mContext, R.color.solde_debiteur_stroke));
                    } else if (solde <= -0.001) {
                        gd.setStroke((int) (1 * mContext.getResources().getDisplayMetrics().density),
                                ContextCompat.getColor(mContext, R.color.solde_crediteur_stroke));
                    } else {
                        gd.setStroke((int) (1 * mContext.getResources().getDisplayMetrics().density),
                                ContextCompat.getColor(mContext, R.color.solde_zero_stroke));
                    }
                } catch (Exception ignore) {}
            } catch (Exception e) {
                holder.Sld_clientN.setText("0,00 DA");
            }
        } else {
            holder.Sld_clientN.setText("******** DA");
        }

        holder.img_pos_client.setImageResource(
                item.latitude == 0.0
                        ? R.drawable.ic_baseline_wrong_location_24
                        : R.drawable.ic_baseline_location_on_24
        );

        // ✅ SAFE CLICK HANDLING
        holder.cardView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || itemClick == null) return;
            itemClick.onClick(v, pos);
        });

        holder.cardView.setOnLongClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || itemLongClick == null) return true;

            itemLongClick.onLongClick(v, pos);
            return true;
        });

        holder.btnEncaisser.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || encaisserListener == null) return;
            encaisserListener.onEncaisser(v, pos);
        });

        // Avatar initials
        String firstChar = "NO";
        if (item.client != null && !item.client.isEmpty()) {
            firstChar = item.client.length() == 1
                    ? String.valueOf(item.client.charAt(0))
                    : item.client.substring(0, 2);
        }

        int color = generator.getColor(item.client);
        TextDrawable drawable =
                TextDrawable.builder().buildRound(firstChar.toUpperCase(), color);

        holder.image.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public interface ItemClick {
        void onClick(View v, int position);
    }

    public interface ItemLongClick {
        void onLongClick(View v, int position);
    }

    public interface OnEncaisserListener {
        void onEncaisser(View v, int position);
    }

    public void refresh(List<PostData_Client> new_itemList) {
        clientList.clear();
        clientList.addAll(new_itemList);
        notifyDataSetChanged();
    }
}