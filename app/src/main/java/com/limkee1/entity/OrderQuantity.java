package com.limkee1.entity;

public class OrderQuantity {

    String itemCode;
    String description;
    String description2;
    int qty;
    double unitPrice;
    String uom;
    String orderID;
    int returnQty;
    int reduceQty;


    public OrderQuantity() {
    }

    public OrderQuantity(String itemCode, int qty, String orderID) {
        this.itemCode = itemCode;
        this.qty = qty;
        this.orderID = orderID;
    }

    public OrderQuantity(String description, String description2, int qty, double unitPrice, String uom) {
        this.description = description;
        this.description2 = description2;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.uom = uom;
    }

    public OrderQuantity(String orderID, String description, String description2, double unitPrice, String uom, int reduceQty, int returnQty, int qty) {
        this.orderID = orderID;
        this.description = description;
        this.description2 = description2;
        this.unitPrice = unitPrice;
        this.uom = uom;
        this.returnQty = returnQty;
        this.reduceQty = reduceQty;
        this.qty = qty;

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

    public void setUOM(String uom) {
        this.uom = uom;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(int returnQty) {
        this.returnQty = returnQty;
    }

    public int getReduceQty() {
        return reduceQty;
    }

    public void setReduceQty(int reduceQty) {
        this.reduceQty = reduceQty;
    }
}
