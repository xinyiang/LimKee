package com.limkee.dao;

import com.limkee.entity.Food;

import java.util.ArrayList;

/**
 * Created by Xin Yi on 24/4/2018.
 */

public class CatalogueDAO {
        public static ArrayList<Food> catalogue_list = new ArrayList<Food>();

        public static Food create(int id, String name, String imageURL, double price) {
            Food food = new Food(id, name, imageURL,price);
            catalogue_list.add(food);
            return food;
        }
}

