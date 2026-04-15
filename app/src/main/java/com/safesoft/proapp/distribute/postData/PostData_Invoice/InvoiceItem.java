package com.safesoft.proapp.distribute.postData.PostData_Invoice;

public class InvoiceItem {

    private String code;
    private String codebarre;
    private String description;
    private double quantity;
    private double unitPrice;
    private double tax_rate;
    private double remise;
    private double lineTotal;

    public InvoiceItem() {
    }

    public InvoiceItem(String code, String codebarre, String description, double quantity, double unitPrice, double tax_rate, double remise, double lineTotal) {
        this.code = code;
        this.codebarre = codebarre;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.tax_rate = unitPrice;
        this.remise = unitPrice;
        this.lineTotal = lineTotal;
    }

    public String getCode() {
        return code;
    }

    public String getCodebarre() {
        return codebarre;
    }

    public String getDescription() {
        return description;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodebarre(String codebarre) {
        this.codebarre = codebarre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setTax(double tax_rate) {
        this.tax_rate = tax_rate;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }
}