package com.limkee.entity;

/**
 * Created by Xin Yi on 12/6/2018.
 */

public class OrderQuantity {

    String OrderID;
    String itemCode;
    int qty;
    int returnedQty;
    double unitPrice;

    public OrderQuantity(){
    }

    public OrderQuantity(String OrderID, String itemCode,int qty, int returnedQty, double unitPrice){
        this.OrderID = OrderID;
        this.itemCode = itemCode;
        this.qty = qty;
        this.returnedQty = returnedQty;
        this.unitPrice = unitPrice;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderQtyID(int orderQtyID) {
        this.OrderID = OrderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
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
}
