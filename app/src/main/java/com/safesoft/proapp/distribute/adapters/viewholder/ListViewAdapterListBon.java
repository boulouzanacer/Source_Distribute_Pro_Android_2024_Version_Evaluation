package com.safesoft.proapp.distribute.adapters.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.SelectedBonEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by UK2015 on 22/08/2016.
 */
public class ListViewAdapterListBon extends BaseAdapter {

  ArrayList<String> list_bons= new ArrayList<String>();
  ArrayList<String> temp_list = new ArrayList<>();
  private static LayoutInflater inflater = null;
  Context context;
  private final EventBus bus = EventBus.getDefault();
  SelectedBonEvent event = null;
  AlertDialog dialog;

  public ListViewAdapterListBon(Context mainActivity, ArrayList<String> itemList, AlertDialog dialog) {
    // TODO Auto-generated constructor P
    list_bons = itemList;
    temp_list.addAll(list_bons);
    context = mainActivity;
    this.dialog = dialog;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return list_bons.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return list_bons.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }

  private class ViewHolder {
    TextView nom_bon;
  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    final ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflater.inflate(R.layout.list_bon_row, null);
      holder.nom_bon = (TextView) convertView.findViewById(R.id.nom_bon);

      convertView.setTag(holder);

    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.nom_bon.setText(list_bons.get(position));

    //On item click event
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        event = new SelectedBonEvent(list_bons.get(position));
        // Post the event
        bus.post(event);
        dialog.dismiss();
      }
    });

    return convertView;
  }
}