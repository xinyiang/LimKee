package com.limkee.dao;

import com.limkee.entity.OrderQuantity;

import java.util.ArrayList;

/**
 * Created by Xin Yi on 12/6/2018.
 */

public class OrderQuantityDAO {

    public static ArrayList<OrderQuantity> ordersQtyList = new ArrayList<OrderQuantity>();

    /*
    public static OrderQuantity create(String orderID, String itemCode, int qty, int returnedQty, double unitPrice) {

        OrderQuantity od = new OrderQuantity(orderID, itemCode, qty, returnedQty, unitPrice);

        ordersQtyList.add(od);
        return od;
    }

    public ArrayList<OrderQuantity> getOrderQuantity() {
        return ordersQtyList;
    }

    {
    }
    */
}