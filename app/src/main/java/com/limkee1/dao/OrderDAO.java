package com.limkee1.dao;

import com.limkee1.entity.Order;
import com.limkee1.entity.OrderDetails;
import com.limkee1.entity.OrderQuantity;

import java.util.ArrayList;

public class OrderDAO {

    public static ArrayList<Order> currentOrdersList = new ArrayList<Order>();
    public static ArrayList<Order> historyOrdersList = new ArrayList<Order>();
    public static ArrayList<Order> cancelledOrdersList = new ArrayList<Order>();
    public static OrderDetails od;
    public static ArrayList<OrderQuantity> oq;
    public static OrderDetails historyOD;
    public static ArrayList<OrderQuantity> historyOQ;
    public static OrderDetails cancelledOD;
    public static ArrayList<OrderQuantity> cancelledOQ;

}
