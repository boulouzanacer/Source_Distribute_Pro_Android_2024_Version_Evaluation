package com.safesoft.proapp.distribute.postData.PostData_Invoice;

import java.util.ArrayList;
import java.util.List;

public class InvoiceData {

    private String invoiceNumber;
    private String invoiceDate;
    private String dueDate;
    private String sellerName;
    private String buyerName;
    private String buyerPhone;
    private String currency;
    private double subtotal;
    private double taxTotal;
    private double remiseTotal;
    private double total;
    private List<InvoiceItem> lineItems = new ArrayList<>();

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(double taxTotal) {
        this.taxTotal = taxTotal;
    }
    public void setRemiseTotal(double remiseTotal) {
        this.remiseTotal = remiseTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<InvoiceItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<InvoiceItem> lineItems) {
        this.lineItems = lineItems;
    }
}