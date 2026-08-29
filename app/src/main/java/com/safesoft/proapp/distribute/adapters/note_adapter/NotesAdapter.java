package com.safesoft.proapp.distribute.adapters.note_adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;

import java.util.ArrayList;

public final class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.VH> {
    private final ArrayList<DATABASE.NoteRow> items;
    private NotesAdapter.OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDelete(DATABASE.NoteRow note, int position);
    }

    public NotesAdapter(ArrayList<DATABASE.NoteRow> items) {
        this.items = items;
    }

    public void setOnDeleteClickListener(NotesAdapter.OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    static final class VH extends RecyclerView.ViewHolder {
        final TextView tv;
        final ImageButton btnDelete;

        VH(View itemView, TextView tv, ImageButton btnDelete) {
            super(itemView);
            this.tv = tv;
            this.btnDelete = btnDelete;
        }
    }

    @Override
    public NotesAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        float density = parent.getResources().getDisplayMetrics().density;
        int padding12 = (int) (12 * density);
        int margin8 = (int) (8 * density);

        LinearLayout row = new LinearLayout(parent.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        RecyclerView.LayoutParams rowLp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLp.setMargins(margin8, margin8, margin8, 0);
        row.setLayoutParams(rowLp);
        row.setPadding(padding12, padding12, padding12, padding12);
        row.setBackgroundResource(R.drawable.item_background_holo_light);

        TextView tv = new TextView(parent.getContext());
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(tvLp);
        tv.setTextColor(parent.getResources().getColor(R.color.mainTextColor));
        tv.setTextSize(15);

        ImageButton btnDelete = new ImageButton(parent.getContext());
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.setMargins(padding12, 0, 0, 0);
        btnDelete.setLayoutParams(btnLp);
        btnDelete.setImageResource(R.drawable.ic_baseline_delete_forever_24);
        btnDelete.setBackgroundColor(Color.TRANSPARENT);
        btnDelete.setColorFilter(parent.getResources().getColor(R.color.rouge));

        row.addView(tv);
        row.addView(btnDelete);

        return new NotesAdapter.VH(row, tv, btnDelete);
    }

    @Override
    public void onBindViewHolder(NotesAdapter.VH holder, int position) {
        DATABASE.NoteRow note = items.get(position);
        holder.tv.setText(note.text);
        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDelete(note, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
