package com.limkee.dao;

import com.limkee.entity.Order;
import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Xin Yi on 4/6/2018.
 */

public class OrderDAO {

    public static ArrayList<Order> currentOrdersList = new ArrayList<Order>();
    public static ArrayList<Order> historyOrdersList = new ArrayList<Order>();
    public static OrderDetails od;
    public static OrderQuantity oq;

    public static Order create(String OrderID, String deliveryDate, int noOfItems) {

        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        ArrayList<OrderDetails> odList = orderDetailDAO.getOrderDetails();
        for (OrderDetails order : odList){
            if (order.getOrderID().equals(OrderID)){
                od = order;
            }
        }
        OrderQuantityDAO orderQtyDAO = new OrderQuantityDAO();
        ArrayList<OrderQuantity> oQList = orderQtyDAO.getOrderQuantity();
        for (OrderQuantity o : oQList){
            if (o.getOrderID().equals(OrderID)){
                oq = o;
            }
        }

        Order salesOrder = new Order(OrderID, deliveryDate, noOfItems, od, oq);
        historyOrdersList.add(salesOrder);
        currentOrdersList.add(salesOrder);
        return salesOrder;
    }
}
