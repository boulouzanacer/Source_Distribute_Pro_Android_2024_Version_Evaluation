package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.safesoft.proapp.distribute.eventsClasses.SelectedBonTransfertEvent;
import com.safesoft.proapp.distribute.fragments.FragmentSelectedBonTransfert;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListSelectedBon extends BaseAdapter {

  ArrayList<String> list_bons_t;
  private static LayoutInflater inflater = null;
  Context context;
  private final FragmentSelectedBonTransfert fragment;
  private final EventBus bus = EventBus.getDefault();
  private SelectedBonTransfertEvent event = null;

  public interface ProduitSelectedEventListener {
    void ProduitSelectedEvent(String s, PostData_Client client);
  }

  ProduitSelectedEventListener produitSelectedListener;

  public ListViewAdapterListSelectedBon(Context mainActivity, ArrayList<String> itemList, FragmentSelectedBonTransfert InstnceFrag) {
    // TODO Auto-generated constructor stub
    list_bons_t = itemList;
    context = mainActivity;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    this.fragment = InstnceFrag;

  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return list_bons_t.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return list_bons_t.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }

  private class ViewHolder {
    TextView num_bon;
  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    final ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflater.inflate(R.layout.list_selected_bon_row, null);
      holder.num_bon = (TextView) convertView.findViewById(R.id.bon);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    if(position % 2 == 0 ){
      convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_1);
    }else{
      convertView.setBackgroundResource(R.drawable.drawable_backg_row_listproduit_2);
    }

    holder.num_bon.setText(list_bons_t.get(position));


    convertView.setBackgroundResource(R.drawable.selector_listview_client_row);
    //On item click event
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        event = new SelectedBonTransfertEvent(list_bons_t.get(position));
        // Post the event
        bus.post(event);
        fragment.finich_fragment();
      }
    });

    return convertView;
  }

}