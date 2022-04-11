package com.safesoft.proapp.distribute.adapters.viewholder.advanced;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.safesoft.proapp.distribute.adapters.model.MyDataObject;
import com.safesoft.proapp.distribute.adapters.model.WrappedMyDataObject;
import com.safesoft.proapp.distribute.R;

import java.text.DecimalFormat;
import java.util.List;

import cn.nekocode.badge.BadgeDrawable;
import eu.inloop.simplerecycleradapter.SettableViewHolder;
import me.grantland.widget.AutofitTextView;


public class AdvancedDataViewHolder extends SettableViewHolder<WrappedMyDataObject> {

  private AutofitTextView mTitle;
  private TextView mQuantite;
  private TextView mMontant;

  public AdvancedDataViewHolder(View itemView) {
    super(itemView);
    init();
  }

  public AdvancedDataViewHolder(@NonNull Context context, @LayoutRes int layoutRes, @NonNull ViewGroup parent) {
    super(context, layoutRes, parent);
    init();
  }

  private void init() {
    mTitle = (AutofitTextView) itemView.findViewById(R.id.title);
    mQuantite = (TextView) itemView.findViewById(R.id.quantite);
    mMontant = (TextView) itemView.findViewById(R.id.montant);
  }

  @Override
  public void setData(@NonNull WrappedMyDataObject data) {
    MyDataObject dataObject = data.getDataObject();
    mTitle.setText(dataObject.getTitle());
    if(dataObject.getQuantite() != null){
      mQuantite.setText(new DecimalFormat("##,##0").format(Double.valueOf(dataObject.getQuantite())));
    }else
      mQuantite.setText("-");

    if(dataObject.getMontant() != null){
      final BadgeDrawable drawable4 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xff303F9F)
                      .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(dataObject.getMontant())))
                      .text2(" DA ")
                      .build();
      SpannableString spannableString2 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
      mMontant.setText(spannableString2);
    }else{
      mMontant.setText("-");
    }
  }

  @Nullable
  @Override
  public List<? extends View> getInnerClickableAreas() {
    //  return Arrays.asList(mBtnUp, mBtnDown, mBtnRemove, mBtnMore);
    return null;
  }
}
