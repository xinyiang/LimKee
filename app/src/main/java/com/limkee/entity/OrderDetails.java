package com.limkee.entity;

public class OrderDetails {
    String orderID;
    String orderDate;
    double subtotal;
    String status;
    String deliveryDate;
    int paperBagRequired;

    public OrderDetails(){
    }

    public OrderDetails(String orderID, String orderDate, String status, String deliveryDate, double subtotal, int paperBagRequired){
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.subtotal = subtotal;
        this.paperBagRequired = paperBagRequired;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getPaperBagRequired() {
        return paperBagRequired;
    }

    public void setPaperBagRequired(int paperBagRequired) {
        this.paperBagRequired = paperBagRequired;
    }
}
