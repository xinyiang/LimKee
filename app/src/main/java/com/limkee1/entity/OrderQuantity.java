package com.limkee1.entity;

public class OrderQuantity {
    String description;
    String description2;
    int qty;
    double unitPrice;
    String uom;

    public OrderQuantity() {
    }
    public OrderQuantity(String description, String description2, int qty, double unitPrice) {
        this.description = description;
        this.description2 = description2;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    public OrderQuantity(String description, String description2, int qty, double unitPrice, String uom) {
        this.description = description;
        this.description2 = description2;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.uom = uom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}
