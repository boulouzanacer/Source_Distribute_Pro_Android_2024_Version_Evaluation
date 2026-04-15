package com.safesoft.proapp.distribute.postData.PostData_Invoice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InvoiceParser {

    public static InvoiceData parse(String json) throws Exception {
        JSONObject obj = new JSONObject(json);

        InvoiceData invoice = new InvoiceData();
        invoice.setInvoiceNumber(getStringOrEmpty(obj, "invoice_number"));
        invoice.setInvoiceDate(getStringOrEmpty(obj, "invoice_date"));
        invoice.setDueDate(getStringOrEmpty(obj, "due_date"));
        invoice.setSellerName(getStringOrEmpty(obj, "seller_name"));
        invoice.setBuyerName(getStringOrEmpty(obj, "buyer_name"));
        invoice.setBuyerPhone(getStringOrEmpty(obj, "buyer_phone"));
        invoice.setCurrency(getStringOrEmpty(obj, "currency"));
        invoice.setSubtotal(getDoubleOrZero(obj, "subtotal"));
        invoice.setTaxTotal(getDoubleOrZero(obj, "tax_total"));
        invoice.setRemiseTotal(getDoubleOrZero(obj, "remise_total"));
        invoice.setTotal(getDoubleOrZero(obj, "total"));

        List<InvoiceItem> items = new ArrayList<>();
        JSONArray arr = obj.optJSONArray("line_items");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject itemObj = arr.getJSONObject(i);

                InvoiceItem item = new InvoiceItem();
                item.setCode(getStringOrEmpty(itemObj, "code"));
                item.setCodebarre(getStringOrEmpty(itemObj, "codebarre"));
                item.setDescription(getStringOrEmpty(itemObj, "description"));
                item.setQuantity(getDoubleOrZero(itemObj, "quantity"));
                item.setUnitPrice(getDoubleOrZero(itemObj, "unit_price"));
                item.setRemise(getDoubleOrZero(itemObj, "remise"));
                item.setTax(getDoubleOrZero(itemObj, "tax_rate"));
                item.setLineTotal(getDoubleOrZero(itemObj, "line_total"));

                items.add(item);
            }
        }

        invoice.setLineItems(items);
        return invoice;
    }

    private static String getStringOrEmpty(JSONObject obj, String key) {
        if (!obj.has(key) || obj.isNull(key)) {
            return "";
        }
        return obj.optString(key, "");
    }

    private static double getDoubleOrZero(JSONObject obj, String key) {
        if (!obj.has(key) || obj.isNull(key)) {
            return 0.0;
        }
        return obj.optDouble(key, 0.0);
    }
}