package eu.inloop.simplerecycleradapter;

import android.view.View;

import androidx.annotation.NonNull;

public interface ItemLongClickListener<T> {
    boolean onItemLongClick(@NonNull T item, @NonNull SettableViewHolder<T> viewHolder, @NonNull View view);
}