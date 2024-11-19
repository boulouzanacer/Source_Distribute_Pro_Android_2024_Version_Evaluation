package eu.inloop.simplerecycleradapter;

import android.view.View;

import androidx.annotation.NonNull;

public interface ItemClickListener<T> {
    void onItemClick(@NonNull T item, @NonNull SettableViewHolder<T> viewHolder, @NonNull View view);
}