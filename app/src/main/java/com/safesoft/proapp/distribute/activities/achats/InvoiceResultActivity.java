package com.safesoft.proapp.distribute.activities.achats;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsetsController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.invoice_reader.InvoiceItemAdapter;
import com.safesoft.proapp.distribute.postData.PostData_Invoice.InvoiceData;
import com.safesoft.proapp.distribute.postData.PostData_Invoice.InvoiceParser;

import java.text.DecimalFormat;

public class InvoiceResultActivity extends AppCompatActivity {

    private TextView tvInvoiceNumber, tvInvoiceDate, tvDueDate, tvSellerName,
            tvBuyerName, tvBuyerPhone, tvCurrency,
            tvSubtotal, tvTaxTotal, tvTotal;

    private RecyclerView recyclerViewItems;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            //  WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Information facture");
            getSupportActionBar().setSubtitle("Fournisseur");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        initViews();

        String jsonResult = getIntent().getStringExtra("invoice_json");
        if (jsonResult == null || jsonResult.trim().isEmpty()) {
            Toast.makeText(this, "JSON introuvable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        try {
            InvoiceData invoice = InvoiceParser.parse(jsonResult);
            bindHeader(invoice);
            bindRecycler(invoice);
            bindFooter(invoice);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur parsing JSON : " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        tvInvoiceNumber = findViewById(R.id.tvInvoiceNumber);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvSellerName = findViewById(R.id.tvSellerName);
        tvBuyerName = findViewById(R.id.tvBuyerName);
        tvBuyerPhone = findViewById(R.id.tvBuyerPhone);
        tvCurrency = findViewById(R.id.tvCurrency);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTaxTotal = findViewById(R.id.tvTaxTotal);
        tvTotal = findViewById(R.id.tvTotal);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);

    }

    private void bindHeader(InvoiceData invoice) {
        tvInvoiceNumber.setText("Numéro : " + value(invoice.getInvoiceNumber()));
        tvInvoiceDate.setText("Date facture : " + value(invoice.getInvoiceDate()));
        tvDueDate.setText("Date échéance : " + value(invoice.getDueDate()));
        tvSellerName.setText("Vendeur : " + value(invoice.getSellerName()));
        tvBuyerName.setText("Client : " + value(invoice.getBuyerName()));
        tvBuyerPhone.setText("Téléphone : " + value(invoice.getBuyerPhone()));
        tvCurrency.setText("Devise : " + value(invoice.getCurrency()));
    }

    private void bindRecycler(InvoiceData invoice) {
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(new InvoiceItemAdapter(invoice.getLineItems()));
    }

    private void bindFooter(InvoiceData invoice) {
        String currency = value(invoice.getCurrency());

        tvSubtotal.setText("Sous-total : " + df.format(invoice.getSubtotal()) + " " + currency);
        tvTaxTotal.setText("Taxe : " + df.format(invoice.getTaxTotal()) + " " + currency);
        tvTotal.setText("Total : " + df.format(invoice.getTotal()) + " " + currency);
    }

    private String value(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}