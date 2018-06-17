package com.limkee.dao;

import com.limkee.entity.OrderDetails;

import java.util.ArrayList;

/**
 * Created by Xin Yi on 12/6/2018.
 */

public class OrderDetailDAO {

    public static ArrayList<OrderDetails> ordersDetailList = new ArrayList<OrderDetails>();

    public static OrderDetails create(String OrderID, String orderDate, double subtotal,  String status,  String cancelledReason) {

        OrderDetails od = new OrderDetails(OrderID, orderDate, subtotal,  status,  cancelledReason);

        ordersDetailList.add(od);
        return od;
    }

    public ArrayList<OrderDetails> getOrderDetails(){
        return ordersDetailList;
    }
}
