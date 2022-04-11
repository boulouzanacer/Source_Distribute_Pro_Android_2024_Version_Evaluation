package com.safesoft.proapp.distribute.adapters.viewholder.advanced;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.safesoft.proapp.distribute.adapters.model.WrappedMyDataObject;
import com.safesoft.proapp.distribute.R;

import eu.inloop.simplerecycleradapter.SettableViewHolder;


public class HeaderViewHolder extends SettableViewHolder<WrappedMyDataObject> {

  private TextView mTitle;
  private TextView mQuantite;
  private TextView mMontant;

  public HeaderViewHolder(View itemView) {
    super(itemView);
    init();
  }

  public HeaderViewHolder(@NonNull Context context, @LayoutRes int layoutRes, @NonNull ViewGroup parent) {
    super(context, layoutRes, parent);
    init();
  }

  private void init() {
    mTitle = (TextView) itemView.findViewById(R.id.title);
    mQuantite = (TextView) itemView.findViewById(R.id.quantite);
    mMontant = (TextView) itemView.findViewById(R.id.montant);
  }

  @Override
  public void setData(@NonNull WrappedMyDataObject data) {
       /* mTitle.setText(data.getHeaderTitle());
        mQuantite.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(data.getmHeaderQuantite())));
        final BadgeDrawable drawable4 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(0xff4DABE0)
                        .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(data.getmHeaderMontant())))
                        .text2(" DA ")
                        .build();

        SpannableString spannableString2 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
        mMontant.setText(spannableString2);
        */
    mTitle.setText("Produit");
    mQuantite.setText("Qte");
    mMontant.setText("Total");
  }

  @Override
  public boolean isClickable() {
    return false;
  }
}
