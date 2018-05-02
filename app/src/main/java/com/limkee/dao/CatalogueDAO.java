package com.limkee.dao;

import com.limkee.entity.Food;

import java.util.ArrayList;

/**
 * Created by Xin Yi on 24/4/2018.
 */

public class CatalogueDAO {
    public static ArrayList<Food> catalogue_list = new ArrayList<Food>();

    public static Food create(int itemCode, String description, String description2, double unitPrice, int minQty, String imageURL) {
        Food food = new Food(itemCode, description, description2, unitPrice, minQty, imageURL);
        catalogue_list.add(food);
        return food;
    }
}

