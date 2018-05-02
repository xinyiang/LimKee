package com.limkee.entity;

import android.support.annotation.NonNull;

/**
 * Created by Xin Yi on 24/4/2018.
 */

public class Food {
    private int itemCode;
    private String description;
    private String description2;
    private double unitPrice;
    private  int minQty;
    private String imageUrl;

    public Food(int itemCode, String description, String description2, double unitPrice, int minQty, String imageUrl){
        this.itemCode = itemCode;
        this.description = description;
        this.description2 = description2;
        this.unitPrice = unitPrice;
        this.minQty = minQty;
        this.imageUrl = imageUrl;
    }
    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public int getitemCode() {
        return itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getMinQty() {
        return minQty;
    }

    public void setMinQty(int minQty) {
        this.minQty = minQty;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



}
