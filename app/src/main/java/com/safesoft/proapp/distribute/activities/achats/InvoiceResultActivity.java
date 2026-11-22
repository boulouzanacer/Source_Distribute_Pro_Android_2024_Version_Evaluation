package com.safesoft.proapp.distribute.activities.achats;

import com.safesoft.proapp.distribute.app.BaseLocaleActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsetsController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.invoice_reader.InvoiceItemAdapter;
import com.safesoft.proapp.distribute.postData.PostData_Invoice.InvoiceData;
import com.safesoft.proapp.distribute.postData.PostData_Invoice.InvoiceParser;

import java.text.DecimalFormat;

public class InvoiceResultActivity extends BaseLocaleActivity {

    private TextView tvInvoiceNumber, tvInvoiceDate, tvDueDate, tvSellerName,
            tvBuyerName, tvBuyerPhone, tvCurrency,
            tvSubtotal, tvTaxTotal, tvTotal;

    private RecyclerView recyclerViewItems;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
            getSupportActionBar().setTitle(getString(R.string.l3_information_facture));
            getSupportActionBar().setSubtitle(getString(R.string.l3_fournisseur));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        initViews();

        String jsonResult = getIntent().getStringExtra("invoice_json");
        if (jsonResult == null || jsonResult.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.l3_json_introuvable), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        try {
            InvoiceData invoice = InvoiceParser.parse(jsonResult);
            bindHeader(invoice);
            bindRecycler(invoice);
            bindFooter(invoice);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.l3_erreur_parsing_json) + e.getMessage(), Toast.LENGTH_LONG).show();
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
        tvInvoiceNumber.setText(getString(R.string.l4_numero_prefix) + value(invoice.getInvoiceNumber()));
        tvInvoiceDate.setText(getString(R.string.l4_date_facture_prefix) + value(invoice.getInvoiceDate()));
        tvDueDate.setText(getString(R.string.l4_date_echeance_prefix) + value(invoice.getDueDate()));
        tvSellerName.setText(getString(R.string.l4_vendeur_prefix) + value(invoice.getSellerName()));
        tvBuyerName.setText(getString(R.string.l4_client_prefix) + value(invoice.getBuyerName()));
        tvBuyerPhone.setText(getString(R.string.l4_telephone_prefix) + value(invoice.getBuyerPhone()));
        tvCurrency.setText(getString(R.string.l4_devise_prefix) + value(invoice.getCurrency()));
    }

    private void bindRecycler(InvoiceData invoice) {
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(new InvoiceItemAdapter(invoice.getLineItems()));
    }

    private void bindFooter(InvoiceData invoice) {
        String currency = value(invoice.getCurrency());

        tvSubtotal.setText(getString(R.string.l4_sous_total_prefix) + df.format(invoice.getSubtotal()) + " " + currency);
        tvTaxTotal.setText(getString(R.string.l4_taxe_prefix) + df.format(invoice.getTaxTotal()) + " " + currency);
        tvTotal.setText(getString(R.string.l4_total_prefix) + df.format(invoice.getTotal()) + " " + currency);
    }

    private String value(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }
}