package com.safesoft.proapp.distribute.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MyCardView2 extends RelativeLayout {

    private CardView cardView;

    private ValueAnimator increaseAnimation;
    private ValueAnimator decreaseAnimation;

    public static final int DURATION = 150;
    private @LayoutRes int layoutResId = 0;

    public MyCardView2(Context context, int resource) {
        super(context);
        initialize(context, resource);
        // ❌ NE PAS register ici
    }

    private void initialize(Context context, int resource) {
        View root = inflate(context, resource, this);
        cardView = root.findViewById(R.id.item_root);
    }

    // ===============================
    // EVENTBUS LIFECYCLE SAFE
    // ===============================

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {

        // stop animations (important RecyclerView recycle)
        if (increaseAnimation != null) {
            increaseAnimation.cancel();
            increaseAnimation = null;
        }

        if (decreaseAnimation != null) {
            decreaseAnimation.cancel();
            decreaseAnimation = null;
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        super.onDetachedFromWindow();
    }

    // ===============================

    @Subscribe
    public void onMessage(ScrollEvent event) {

        if (getLayoutParams() == null) return;

        int margin = event.getMargin();

        RecyclerView.LayoutParams layoutParams =
                (RecyclerView.LayoutParams) getLayoutParams();

        int marginBottom = layoutParams.bottomMargin;

        if (margin == 0) {

            if (increaseAnimation != null && increaseAnimation.isRunning())
                increaseAnimation.cancel();

            decreaseAnimation = ValueAnimator.ofInt(marginBottom, 0);
            decreaseAnimation.addUpdateListener(animation -> {
                RecyclerView.LayoutParams lp =
                        (RecyclerView.LayoutParams) getLayoutParams();

                lp.bottomMargin = (int) animation.getAnimatedValue();
                setLayoutParams(lp);
            });

            decreaseAnimation.setDuration(DURATION);
            decreaseAnimation.start();

        } else {

            if (decreaseAnimation != null && decreaseAnimation.isRunning())
                decreaseAnimation.cancel();

            int maxMargin = getResources()
                    .getDimensionPixelSize(R.dimen.cardview_max_margin_bottom);

            increaseAnimation = ValueAnimator.ofInt(marginBottom, maxMargin);

            increaseAnimation.addUpdateListener(animation -> {
                RecyclerView.LayoutParams lp =
                        (RecyclerView.LayoutParams) getLayoutParams();

                lp.bottomMargin = (int) animation.getAnimatedValue();
                setLayoutParams(lp);
            });

            increaseAnimation.setDuration(DURATION);
            increaseAnimation.start();
        }
    }
}