package com.safesoft.proapp.distribute.adapters.note_adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;

import java.util.ArrayList;

public final class NotePickerAdapter extends RecyclerView.Adapter<NotePickerAdapter.VH> {
    private final ArrayList<DATABASE.NoteRow> items;
    private final OnNotePickedListener listener;
    private final OnNoteDeleteListener deleteListener;

    public NotePickerAdapter(ArrayList<DATABASE.NoteRow> items,
                             OnNotePickedListener listener) {
        this(items, listener, null);
    }

    public NotePickerAdapter(ArrayList<DATABASE.NoteRow> items,
                             OnNotePickedListener listener,
                             OnNoteDeleteListener deleteListener) {
        this.items = items;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    static final class VH extends RecyclerView.ViewHolder {
        final TextView tv;
        final ImageButton btnDelete;

        VH(LinearLayout itemView) {
            super(itemView);
            this.tv = (TextView) itemView.getChildAt(0);
            this.btnDelete = (ImageButton) itemView.getChildAt(1);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        float density = parent.getResources().getDisplayMetrics().density;
        int padding12 = (int) (12 * density);
        int margin8 = (int) (8 * density);
        int iconSize = (int) (36 * density);

        LinearLayout row = new LinearLayout(parent.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(margin8, margin8, margin8, 0);
        row.setLayoutParams(lp);
        row.setBackgroundResource(R.drawable.item_background_holo_light);

        TextView tv = new TextView(parent.getContext());
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tvLp.gravity = android.view.Gravity.CENTER_VERTICAL;
        tv.setLayoutParams(tvLp);
        tv.setPadding(padding12, padding12, padding12 / 2, padding12);
        tv.setTextColor(parent.getResources().getColor(R.color.mainTextColor));
        tv.setTextSize(15);
        tv.setMaxLines(3);
        row.addView(tv);

        ImageButton btnDelete = new ImageButton(parent.getContext());
        LinearLayout.LayoutParams delLp = new LinearLayout.LayoutParams(iconSize, iconSize);
        delLp.gravity = android.view.Gravity.CENTER_VERTICAL;
        delLp.setMarginEnd(padding12 / 2);
        btnDelete.setLayoutParams(delLp);
        btnDelete.setImageResource(R.drawable.ic_baseline_delete_24);
        btnDelete.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnDelete.setColorFilter(parent.getResources().getColor(R.color.rouge));
        btnDelete.setContentDescription("Supprimer");
        row.addView(btnDelete);

        return new VH(row);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        DATABASE.NoteRow note = items.get(position);
        holder.tv.setText(note == null ? "" : note.text);

        holder.tv.setOnClickListener(v -> {
            if (listener != null && note != null) {
                listener.onNotePicked(note.text);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null && note != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    deleteListener.onNoteDelete(note.id, pos);
                }
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

    public interface OnNoteDeleteListener {
        void onNoteDelete(long noteId, int position);
    }
}