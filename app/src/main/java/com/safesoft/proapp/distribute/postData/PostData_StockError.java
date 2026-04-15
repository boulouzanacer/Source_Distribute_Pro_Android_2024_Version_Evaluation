package com.safesoft.proapp.distribute.postData;

public class PostData_StockError {
    public String produit;
    public double qte;
    public double stock;

    public PostData_StockError(String produit, double qte, double stock) {
        this.produit = produit;
        this.qte = qte;
        this.stock = stock;
    }
}
