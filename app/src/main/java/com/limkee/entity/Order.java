package com.limkee.entity;

import java.util.Date;

/**
 * Created by Xin Yi on 4/6/2018.
 */

public class Order {

    private String OrderID;
    private String deliveryDate;
    private int noOfItems;
    private OrderDetails od;
    private OrderQuantity oq;

    public Order(){

    }

    public Order(String orderID, String deliveryDate,int noOfItems, OrderDetails od, OrderQuantity oq){
        this.OrderID = orderID;
        this.deliveryDate = deliveryDate;
        this.noOfItems = noOfItems;
        this.od = od;
        this.oq = oq;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
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
}
