package com.safesoft.pro.distribute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.eventsClasses.MessageEvent;
import com.safesoft.pro.distribute.eventsClasses.ScanResultEvent;
import com.safesoft.pro.distribute.postData.PostData_Produit;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapter_ListProduits_main extends BaseAdapter {

    ArrayList<PostData_Produit> list_produits = new ArrayList<PostData_Produit>();
    ArrayList<PostData_Produit> temp_list = new ArrayList<>();
    private static LayoutInflater inflater = null;
    Context context;
    private Boolean _switch_mode = false;

    private EventBus bus = EventBus.getDefault();
    private ScanResultEvent event = null;
    private MessageEvent event1 = null;

    public ListViewAdapter_ListProduits_main(Context mainActivity, ArrayList<PostData_Produit> itemList) {
        // TODO Auto-generated constructor stub
        list_produits = itemList;
        temp_list.addAll(list_produits);
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_produits.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list_produits.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder {
        TextView reference;
        TextView produit;
        TextView stock_t;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_produits_row2, null);
            holder.reference = (TextView) convertView.findViewById(R.id.reference);
            holder.produit = (TextView) convertView.findViewById(R.id.libelle);
            holder.stock_t = (TextView) convertView.findViewById(R.id.stock_t);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position % 2 == 0 ){
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_1);
        }else{
            convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_2);
        }

        holder.reference.setText(list_produits.get(position).ref_produit);
        holder.produit.setText(list_produits.get(position).produit);
        if(list_produits.get(position).stock != null)
        {
            holder.stock_t.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(list_produits.get(position).stock)));

        }else
        {

            holder.stock_t.setText(""+ 0.00);

        }

        convertView.setBackgroundResource(R.drawable.selector_listview_product_row);
        //On item click event
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event= new ScanResultEvent(list_produits.get(position));
                // Post the event
                bus.post(event);

                event1 = new MessageEvent("OK");
                bus.post(event1);
            }
        });

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list_produits.clear();
        if (charText.length() == 0) {
            list_produits.addAll(temp_list);
        }
        else
        {
            for (int i =0;i<temp_list.size();i++)
            {
                if (temp_list.get(i).produit.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    list_produits.add(temp_list.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }
}