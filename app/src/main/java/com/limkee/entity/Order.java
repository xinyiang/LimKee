package com.limkee.entity;

import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;

import java.util.ArrayList;

public class Order {

    private String OrderID;
    private String deliveryDate;
    private int noOfItems;
    private OrderDetails od;
    private ArrayList<OrderQuantity> oq;

    public Order() {

    }

    public Order(String orderID, String deliveryDate, int noOfItems, OrderDetails od, ArrayList<OrderQuantity> oq) {
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

    public OrderDetails getOd() {
        return od;
    }

    public void setOd(OrderDetails od) {
        this.od = od;
    }

    public ArrayList<OrderQuantity> getOqList() {
        return oq;
    }

    public void setOqList(ArrayList<OrderQuantity> oqList) {
        this.oq = oqList;
    }
}