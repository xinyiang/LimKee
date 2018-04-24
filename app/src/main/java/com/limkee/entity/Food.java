package com.limkee.entity;

import android.support.annotation.NonNull;

/**
 * Created by Xin Yi on 24/4/2018.
 */

public class Food {
    private String name;
    private int id;
    private String imageUrl;
    private double price;

    public Food(int id, String name, String imageUrl, double price){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
