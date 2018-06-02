package com.limkee.dao;

import com.limkee.entity.Product;

import java.util.ArrayList;

/**
 * Created by Xin Yi on 24/4/2018.
 */

public class CatalogueDAO {
    public static ArrayList<Product> catalogue_list = new ArrayList<Product>();
    public static ArrayList<Product> order_list = new ArrayList<Product>();

    public static Product create(String itemCode, String description, String description2, double unitPrice, String imageURL, int defaultQty, int qtyMultiples) {
        if (catalogue_list == null) {
            catalogue_list = new ArrayList<>();
        }
        Product food = new Product(itemCode, description, description2, unitPrice, imageURL, defaultQty, qtyMultiples);
        catalogue_list.add(food);
        return food;
    }
}

