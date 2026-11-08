package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListClient extends BaseAdapter {

    ArrayList<PostData_Client> list_clients = new ArrayList<PostData_Client>();
    ArrayList<PostData_Client> temp_list = new ArrayList<>();
    private static LayoutInflater inflater = null;
    Context mContext;
    private final EventBus bus = EventBus.getDefault();
    SelectedClientEvent event = null;
    AlertDialog dialog;
    String SOURCE;
    private final String PREFS = "ALL_PREFS";
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
        void ProduitSelectedEvent(String s, PostData_Client client);
    }

    ProduitSelectedEventListener produitSelectedListener;

    public ListViewAdapterListClient(Context mainActivity, ArrayList<PostData_Client> itemList, AlertDialog dialog, String SOURCE) {
        // TODO Auto-generated constructor P
        list_clients = itemList;
        temp_list.addAll(list_clients);
        mContext = mainActivity;
        this.dialog = dialog;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.SOURCE = SOURCE;
        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_clients.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list_clients.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder {
        TextView client;
        TextView code_client;
        TextView solde_client;
        ImageView img_client_location;
        TextView avatar_initiales;
        CardView avatar_client_container;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_client_row, null);
            holder.client = convertView.findViewById(R.id.nom_client);
            holder.code_client = convertView.findViewById(R.id.code_client);
            holder.solde_client = convertView.findViewById(R.id.sold_client);
            holder.img_client_location = convertView.findViewById(R.id.img_client_location);
            holder.avatar_initiales = convertView.findViewById(R.id.avatar_initiales);
            holder.avatar_client_container = convertView.findViewById(R.id.avatar_client_container);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PostData_Client current = list_clients.get(position);

        holder.client.setText(current.client);
        holder.code_client.setText(current.code_client);

        DecimalFormat df = new DecimalFormat("##,##0.00");
        double solde = current.solde_client;
        if (prefs.getBoolean("AFFICHAGE_SOLDE_CLIENT", true)) {
            holder.solde_client.setText(df.format(solde));
        } else {
            holder.solde_client.setText("********");
            solde = 0.0;
        }

        if (solde > 0.009) {
            holder.solde_client.setTextColor(Color.parseColor("#047857"));
            holder.solde_client.setBackgroundResource(R.drawable.bg_pill_solde_positif);
        } else if (solde < -0.009) {
            holder.solde_client.setTextColor(Color.parseColor("#B91C1C"));
            holder.solde_client.setBackgroundResource(R.drawable.bg_pill_solde_negatif);
        } else {
            holder.solde_client.setTextColor(Color.parseColor("#92400E"));
            holder.solde_client.setBackgroundResource(R.drawable.bg_pill_solde_zero);
        }

        if (current.latitude != 0.0) {
            holder.img_client_location.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_location_on_24));
        } else {
            holder.img_client_location.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_wrong_location_24));
        }

        if (holder.avatar_initiales != null) {
            String initiales = computeInitiales(current.client, current.code_client);
            holder.avatar_initiales.setText(initiales);
            int idx = colorIndex(current.code_client);
            holder.avatar_initiales.setTextColor(AVATAR_COLORS[idx]);
            if (holder.avatar_client_container != null) {
                try {
                    holder.avatar_client_container.setCardBackgroundColor(AVATAR_BG[idx]);
                } catch (Throwable ignored) {}
            }
        }

        //On item click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SOURCE.equals("FROM_ROUTING")) {
                    if (list_clients.get(position).latitude != 0.0) {
                        event = new SelectedClientEvent(list_clients.get(position));
                        // Post the event
                        bus.post(event);
                        //dialog.dismiss();

                        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Route...")
                                .setContentText("Client bien ajouté dans la liste de route")
                                .show();
                    } else {
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Route...")
                                .setContentText("Client n'a pas une position géographique, Veuillez introduisez une dans Client -> Détails client")
                                .show();
                    }
                } else {
                    event = new SelectedClientEvent(list_clients.get(position));
                    // Post the event
                    bus.post(event);
                    dialog.dismiss();
                }
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
