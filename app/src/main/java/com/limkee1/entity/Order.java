package com.limkee1.entity;

public class Order {
    private String orderID;
    private String deliveryDate;
    private int noOfItems;
    private String deliveryShift;

    public Order() {

    }

    public Order(String orderID, String deliveryDate, int noOfItems, String deliveryShift) {
        this.orderID = orderID;
        this.deliveryDate = deliveryDate;
        this.noOfItems = noOfItems;
        this.deliveryShift = deliveryShift;
    }

    public Order(String orderID, double subtotal) {
        this.orderID = orderID;
        this.deliveryDate = deliveryDate;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public String getDeliveryShift() {
        return deliveryShift;
    }

    public void setDeliveryShift(String deliveryShift) {
        this.deliveryShift = deliveryShift;
    }

}