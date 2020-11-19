package com.safesoft.pro.distribute.adapters.viewholder.advanced;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.safesoft.pro.distribute.adapters.model.WrappedMyDataObject;
import com.safesoft.pro.distribute.R;

import eu.inloop.simplerecycleradapter.SettableViewHolder;


public class HeaderViewHolderTotal extends SettableViewHolder<WrappedMyDataObject> {

  private TextView mTitle;
  public HeaderViewHolderTotal(View itemView) {
    super(itemView);
    init();
  }

  public HeaderViewHolderTotal(@NonNull Context context, @LayoutRes int layoutRes, @NonNull ViewGroup parent) {
    super(context, layoutRes, parent);
    init();
  }

  private void init() {
    mTitle = (TextView) itemView.findViewById(R.id.title);
  }

  @Override
  public void setData(@NonNull WrappedMyDataObject data) {
    mTitle.setText(data.getHeaderTitle());
  }

  @Override
  public boolean isClickable() {
    return false;
  }
}
