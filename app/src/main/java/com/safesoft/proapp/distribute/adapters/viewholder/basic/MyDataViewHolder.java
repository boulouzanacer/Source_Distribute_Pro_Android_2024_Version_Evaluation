package com.safesoft.proapp.distribute.adapters.viewholder.basic;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.safesoft.proapp.distribute.adapters.model.MyDataObject;
import com.safesoft.proapp.distribute.R;

import eu.inloop.simplerecycleradapter.SettableViewHolder;


public class MyDataViewHolder extends SettableViewHolder<MyDataObject> {

  private TextView mTitle;
  private TextView mQuantite;
  private TextView mMontant;

  public MyDataViewHolder(View itemView) {
    super(itemView);
    init();
  }

  public MyDataViewHolder(@NonNull Context context, @LayoutRes int layoutRes, @NonNull ViewGroup parent) {
    super(context, layoutRes, parent);
    init();
  }

  private void init() {
    mTitle = (TextView) itemView.findViewById(R.id.title);
    mQuantite = (TextView) itemView.findViewById(R.id.quantite);
    mMontant = (TextView) itemView.findViewById(R.id.montant);
  }

  @Override
  public void setData(@NonNull MyDataObject data) {
    mTitle.setText(data.getTitle());
    mQuantite.setText(String.valueOf(data.getQuantite()));
    mMontant.setText(String.valueOf(data.getMontant()));
  }
}
