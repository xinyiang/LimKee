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
    public static ArrayList<Order> cancelledOrdersList = new ArrayList<Order>();
    public static OrderDetails od;
    public static ArrayList<OrderQuantity> oq;
    public static OrderDetails historyOD;
    public static ArrayList<OrderQuantity> historyOQ;
    public static OrderDetails cancelledOD;
    public static ArrayList<OrderQuantity> cancelledOQ;

}
