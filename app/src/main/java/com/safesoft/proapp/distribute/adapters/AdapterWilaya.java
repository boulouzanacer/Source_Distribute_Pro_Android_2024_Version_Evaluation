package com.safesoft.proapp.distribute.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_wilaya;

import java.util.ArrayList;

/**
 * Created by UK2016 on 02/01/2017.
 */

public class AdapterWilaya extends ArrayAdapter<PostData_wilaya> {

  private Context context;
  private ArrayList<PostData_wilaya> wilayas;
  public Resources res;
  PostData_wilaya currRowVal = null;
  LayoutInflater inflater;

  public AdapterWilaya(Context context, int textViewResourceId, ArrayList<PostData_wilaya> wilayas, Resources resLocal) {
    super(context, textViewResourceId, wilayas);
    this.context = context;
    this.wilayas = wilayas;
    this.res = resLocal;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent) {
    View row = inflater.inflate(R.layout.dropdown_wilaya_commune_item, parent, false);
    currRowVal = wilayas.get(position);
    TextView label = row.findViewById(R.id.text_wilaya_commune);
    label.setText(currRowVal.wilaya);

    return row;
  }
}