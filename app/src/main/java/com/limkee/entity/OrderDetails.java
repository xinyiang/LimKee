package com.limkee.entity;

/**
 * Created by Xin Yi on 24/6/2018.
 */

public class OrderDetails {
    String orderID;
    String orderDate;
    double subtotal;
    String status;
    String cancelledReason;

    public OrderDetails(){
    }

    public OrderDetails(String orderID, String orderDate, String status, String cancelledReason, double subtotal){
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.cancelledReason = cancelledReason;
        this.subtotal = subtotal;
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

    public String getCancelledReason() {
        return cancelledReason;
    }

    public void setCancelledReason(String cancelledReason) {
        this.cancelledReason = cancelledReason;
    }
}
