package com.limkee.entity;

/**
 * Created by Xin Yi on 12/6/2018.
 */

public class OrderDetails {


    String OrderID;
    String orderDate;
    double subtotal;
    String status;
    String cancelledReason;

    public OrderDetails(){
    }

    public OrderDetails(String OrderID, String orderDate, double subtotal, String status, String cancelledReason){
        this.OrderID = OrderID;
        this.orderDate = orderDate;
        this.subtotal = subtotal;
        this.status = status;
        this.cancelledReason = cancelledReason;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
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
