package com.safesoft.proapp.distribute.adapters.note_adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;

import java.util.ArrayList;

public final class NotePickerAdapter extends RecyclerView.Adapter<NotePickerAdapter.VH> {
    private final ArrayList<DATABASE.NoteRow> items;
    private final OnNotePickedListener listener;

    public NotePickerAdapter(ArrayList<DATABASE.NoteRow> items, OnNotePickedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    static final class VH extends RecyclerView.ViewHolder {
        final TextView tv;

        VH(TextView itemView) {
            super(itemView);
            this.tv = itemView;
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        float density = parent.getResources().getDisplayMetrics().density;
        int padding12 = (int) (12 * density);
        int margin8 = (int) (8 * density);

        TextView tv = new TextView(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(margin8, margin8, margin8, 0);
        tv.setLayoutParams(lp);
        tv.setPadding(padding12, padding12, padding12, padding12);
        tv.setBackgroundResource(R.drawable.item_background_holo_light);
        tv.setTextColor(parent.getResources().getColor(R.color.mainTextColor));
        tv.setTextSize(15);
        tv.setMaxLines(3);
        return new VH(tv);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        DATABASE.NoteRow note = items.get(position);
        holder.tv.setText(note == null ? "" : note.text);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && note != null) {
                listener.onNotePicked(note.text);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public interface OnNotePickedListener {
        void onNotePicked(String noteText);
    }
}