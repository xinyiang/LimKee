package com.limkee.entity;

/**
 * Created by Xin Yi on 24/6/2018.
 */

public class OrderQuantity {
    String description;
    String description2;
    int qty;
    int returnedQty;
    double unitPrice;
    String uom;

    public OrderQuantity() {
    }

    public OrderQuantity(String description, String description2, int qty, int returnedQty, double unitPrice) {
        this.description = description;
        this.description2 = description2;
        this.qty = qty;
        this.returnedQty = returnedQty;
        this.unitPrice = unitPrice;
    }

    public OrderQuantity(String description, String description2, int qty, int returnedQty, double unitPrice, String uom) {
        this.description = description;
        this.description2 = description2;
        this.qty = qty;
        this.returnedQty = returnedQty;
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

    public int getReturnedQty() {
        return returnedQty;
    }

    public void setReturnedQty(int returnedQty) {
        this.returnedQty = returnedQty;
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
