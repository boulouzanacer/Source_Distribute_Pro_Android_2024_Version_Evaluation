package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedFournisseurEvent;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListViewAdapterListFournisseur extends BaseAdapter {

    ArrayList<PostData_Fournisseur> list_fournisseurs = new ArrayList<>();
    ArrayList<PostData_Fournisseur> temp_list = new ArrayList<>();
    private static LayoutInflater inflater = null;
    Context mContext;
    private final EventBus bus = EventBus.getDefault();
    SelectedFournisseurEvent event = null;
    AlertDialog dialog;

    private static final String PREFS = "ALL_PREFS";
    private final SharedPreferences prefs;

    private static final @ColorInt int[] AVATAR_COLORS = {
        0xFF1D4ED8, 0xFF059669, 0xFFDC2626, 0xFFD97706,
        0xFF7C3AED, 0xFF0891B2, 0xFF0EA5E9, 0xFF65A30D
    };
    private static final @ColorInt int[] AVATAR_STROKES = {
        0xFF93C5FD, 0xFF6EE7B7, 0xFFFCA5A5, 0xFFFCD34D,
        0xFFC4B5FD, 0xFF67E8F9, 0xFF7DD3FC, 0xFFBEF264
    };
    private static final @ColorInt int[] AVATAR_BG = {
        0xFFDBEAFE, 0xFFD1FAE5, 0xFFFEE2E2, 0xFFFFF3C4,
        0xFFEDE9FE, 0xFFCFFAFE, 0xFFE0F2FE, 0xFFECFCCB
    };

    public interface ProduitSelectedEventListener {
        void ProduitSelectedEvent(String s, PostData_Fournisseur fournisseur);
    }

    ProduitSelectedEventListener produitSelectedListener;

    public ListViewAdapterListFournisseur(Context mainActivity, ArrayList<PostData_Fournisseur> itemList, AlertDialog dialog) {

        list_fournisseurs = itemList;
        temp_list.addAll(list_fournisseurs);
        mContext = mainActivity;
        this.dialog = dialog;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

    }

    @Override
    public int getCount() {
        return list_fournisseurs.size();
    }

    @Override
    public Object getItem(int position) {
        return list_fournisseurs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView txtv_fournisseur;
        TextView code_frs_row;
        TextView txtv_tel;
        TextView txtv_achats_frs;
        TextView txtv_versement_frs;
        TextView txtv_solde_frs;
        AppCompatImageView img_pos_fournisseur;
        TextView avatar_initiales;
        CardView avatar_fournisseur_container;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_fournisseur, null);
            holder.txtv_fournisseur = convertView.findViewById(R.id.fournisseur);
            holder.code_frs_row = convertView.findViewById(R.id.code_fournisseur_row);
            holder.txtv_tel = convertView.findViewById(R.id.tel_fournisseur);
            holder.txtv_achats_frs = convertView.findViewById(R.id.achat_fournisseur);
            holder.txtv_versement_frs = convertView.findViewById(R.id.verser_fournisseur);
            holder.txtv_solde_frs = convertView.findViewById(R.id.sold_fournisseur);
            holder.img_pos_fournisseur = convertView.findViewById(R.id.img_pos_fournisseur);
            holder.avatar_initiales = convertView.findViewById(R.id.avatar_frs_initiales);
            holder.avatar_fournisseur_container = convertView.findViewById(R.id.avatar_fournisseur_container);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PostData_Fournisseur current = list_fournisseurs.get(position);

        holder.txtv_fournisseur.setText(current.fournis);
        if (holder.code_frs_row != null) {
            holder.code_frs_row.setText(current.code_frs != null ? current.code_frs : "");
        }

        DecimalFormat df = new DecimalFormat("##,##0.00");
        String achatTxt = df.format(current.achat_montant);
        String verserTxt = df.format(current.verser_montant);
        if (holder.txtv_achats_frs != null) {
            holder.txtv_achats_frs.setText("ACH: " + achatTxt);
        }
        if (holder.txtv_versement_frs != null) {
            holder.txtv_versement_frs.setText("VER: " + verserTxt);
        }
        if (holder.txtv_tel != null) {
            String tel = (current.tel == null || current.tel.isEmpty()) ? "📞 —" : "📞 " + current.tel;
            holder.txtv_tel.setText(tel);
        }

        double solde = current.solde_montant;
        boolean affichageSolde = prefs.getBoolean("AFFICHAGE_SOLDE_FOURNISSEUR", true);
        if (affichageSolde) {
            holder.txtv_solde_frs.setText(df.format(solde));
        } else {
            holder.txtv_solde_frs.setText("********");
            solde = 0.0;
        }

        if (solde > 0.009) {
            holder.txtv_solde_frs.setTextColor(Color.parseColor("#047857"));
            holder.txtv_solde_frs.setBackgroundResource(R.drawable.bg_pill_solde_positif);
        } else if (solde < -0.009) {
            holder.txtv_solde_frs.setTextColor(Color.parseColor("#B91C1C"));
            holder.txtv_solde_frs.setBackgroundResource(R.drawable.bg_pill_solde_negatif);
        } else {
            holder.txtv_solde_frs.setTextColor(Color.parseColor("#92400E"));
            holder.txtv_solde_frs.setBackgroundResource(R.drawable.bg_pill_solde_zero);
        }

        if (holder.img_pos_fournisseur != null) {
            if (current.latitude != 0.0) {
                holder.img_pos_fournisseur.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_location_on_24));
                try {
                    holder.img_pos_fournisseur.getDrawable().setTint(Color.parseColor("#059669"));
                } catch (Throwable ignored) {}
            } else {
                holder.img_pos_fournisseur.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_wrong_location_24));
                try {
                    holder.img_pos_fournisseur.getDrawable().setTint(Color.parseColor("#DC2626"));
                } catch (Throwable ignored) {}
            }
        }

        if (holder.avatar_initiales != null) {
            String initiales = computeInitiales(current.fournis, current.code_frs);
            holder.avatar_initiales.setText(initiales);
            int idx = colorIndex(current.code_frs);
            holder.avatar_initiales.setTextColor(AVATAR_COLORS[idx]);
            if (holder.avatar_fournisseur_container != null) {
                try {
                    holder.avatar_fournisseur_container.setCardBackgroundColor(AVATAR_BG[idx]);
                } catch (Throwable ignored) {}
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new SelectedFournisseurEvent(list_fournisseurs.get(position));
                bus.post(event);
                dialog.dismiss();
            }
        });

        return convertView;
    }

    private static int colorIndex(String code) {
        if (code == null || code.isEmpty()) return 0;
        int h = 0;
        for (int i = 0; i < code.length(); i++) h = 31 * h + code.charAt(i);
        return Math.abs(h) % AVATAR_COLORS.length;
    }

    private static String computeInitiales(String nom, String code) {
        String s = (nom == null) ? "" : nom.trim();
        if (s.isEmpty() && code != null) s = code.trim();
        if (s.isEmpty()) return "--";
        StringBuilder sb = new StringBuilder();
        String[] parts = s.split("[\\s\\-_'\",.()\\[\\]{}]+");
        int taken = 0;
        for (String p : parts) {
            if (p == null || p.isEmpty()) continue;
            char c = p.charAt(0);
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toUpperCase(c));
                taken++;
                if (taken >= 2) break;
            }
        }
        String out = sb.toString();
        if (out.isEmpty()) {
            if (code != null && !code.isEmpty()) out = code.substring(0, Math.min(2, code.length())).toUpperCase(Locale.US);
            else out = "--";
        } else if (out.length() == 1 && code != null && !code.isEmpty()) {
            out = out + Character.toUpperCase(code.charAt(0));
        }
        return out;
    }
}
