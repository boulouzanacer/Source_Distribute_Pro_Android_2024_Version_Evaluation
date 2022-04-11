package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.safesoft.proapp.distribute.fragments.FragmentListClient;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListClient extends BaseAdapter {

  ArrayList<PostData_Client> list_clients = new ArrayList<PostData_Client>();
  ArrayList<PostData_Client> temp_list = new ArrayList<>();
  private static LayoutInflater inflater = null;
  Context context;
  private FragmentListClient fragment;
  private EventBus bus = EventBus.getDefault();
  SelectedClientEvent event = null;

  public interface ProduitSelectedEventListener {
    public void ProduitSelectedEvent(String s, PostData_Client client);
  }

  ProduitSelectedEventListener produitSelectedListener;

  public ListViewAdapterListClient(Context mainActivity, ArrayList<PostData_Client> itemList, FragmentListClient InstnceFrag) {
    // TODO Auto-generated constructor P
    list_clients = itemList;
    temp_list.addAll(list_clients);
    context = mainActivity;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    this.fragment = InstnceFrag;

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
    TextView stock_t;
  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    final ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflater.inflate(R.layout.list_client_row, null);
      holder.client = (TextView) convertView.findViewById(R.id.client);
      holder.code_client = (TextView) convertView.findViewById(R.id.code_client);
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

    holder.client.setText(list_clients.get(position).client);
    holder.code_client.setText(list_clients.get(position).code_client);
    // holder.stock_t.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(list_produits.get(position).quantity_old)));

    convertView.setBackgroundResource(R.drawable.selector_listview_client_row);
    //On item click event
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        event = new SelectedClientEvent(list_clients.get(position));
        // Post the event
        bus.post(event);
        fragment.finich_fragment();
      }
    });

    return convertView;
  }

  // Filter Class
  public void filter(String charText) {
    charText = charText.toLowerCase(Locale.getDefault());
    list_clients.clear();
    if (charText.length() == 0) {
      list_clients.addAll(temp_list);
    }
    else
    {
      for (int i =0;i<temp_list.size();i++)
      {
        if (temp_list.get(i).client.toLowerCase(Locale.getDefault()).contains(charText))
        {
          list_clients.add(temp_list.get(i));
        }
      }
    }
    notifyDataSetChanged();
  }
}