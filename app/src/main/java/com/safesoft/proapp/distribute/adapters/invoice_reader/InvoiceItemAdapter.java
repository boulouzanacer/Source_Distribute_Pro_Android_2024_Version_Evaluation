package com.safesoft.proapp.distribute.adapters.invoice_reader;
import com.safesoft.proapp.distribute.app.BaseApplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Invoice.InvoiceItem;

import java.text.DecimalFormat;
import java.util.List;

public class InvoiceItemAdapter extends RecyclerView.Adapter<InvoiceItemAdapter.ItemViewHolder> {

    private final List<InvoiceItem> items;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public InvoiceItemAdapter(List<InvoiceItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_line, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        InvoiceItem item = items.get(position);

        holder.tvDescription.setText(notEmpty(item.getDescription()));
        holder.tvCode.setText(BaseApplication.instance.getString(R.string.l4_code_prefix) + notEmpty(item.getCode()));
        holder.tvBarcode.setText(BaseApplication.instance.getString(R.string.l4_code_barres_prefix) + notEmpty(item.getCodebarre()));
        holder.tvQty.setText(BaseApplication.instance.getString(R.string.l4_qte_prefix) + df.format(item.getQuantity()));
        holder.tvUnitPrice.setText(BaseApplication.instance.getString(R.string.l4_pu_prefix) + df.format(item.getUnitPrice()));
        holder.tvLineTotal.setText(BaseApplication.instance.getString(R.string.l4_total_ligne_prefix) + df.format(item.getLineTotal()));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private String notEmpty(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvCode, tvBarcode, tvQty, tvUnitPrice, tvLineTotal;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvBarcode = itemView.findViewById(R.id.tvBarcode);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvLineTotal = itemView.findViewById(R.id.tvLineTotal);
        }
    }
}