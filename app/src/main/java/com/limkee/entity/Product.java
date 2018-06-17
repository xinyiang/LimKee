package com.limkee.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Xin Yi on 20/5/2018.
 */

public class Product implements Parcelable{

    @SerializedName("itemCode")
        private String itemCode;
    @SerializedName("description")
        private String description;
    @SerializedName("description2")
        private String description2;
    @SerializedName("unitPrice")
        private double unitPrice;
    @SerializedName("defaultQty")
        private  int defaultQty;
    @SerializedName("qtyMultiples")
        private  int qtyMultiples;
    @SerializedName("imageUrl")
        private String imageUrl;

        public Product(String itemCode, String description, String description2, double unitPrice, String imageUrl, int defaultQty, int qtyMultiples){
            this.itemCode = itemCode;
            this.description = description;
            this.description2 = description2;
            this.unitPrice = unitPrice;
            this.defaultQty = defaultQty;
            this.qtyMultiples = qtyMultiples;
            this.imageUrl = imageUrl;
        }
        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getitemCode() {
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

    public int getDefaultQty() {
        return defaultQty;
    }

    public void setDefaultQty(int defaultQty) {
        this.defaultQty = defaultQty;
    }

    public int getQtyMultiples() {
            return qtyMultiples;
        }

        public void setQtyMultiples(int qtyMultiples) {
            this.qtyMultiples = qtyMultiples;
        }

        public String getImageUrl() { return imageUrl; }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

    public Product(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {

            return new Product[size];
        }

    };


    public void readFromParcel(Parcel in) {
        itemCode = in.readString();
        description = in.readString();
        description2 = in.readString();
        unitPrice = in.readDouble();
        defaultQty = in.readInt();
        qtyMultiples = in.readInt();
        imageUrl = in.readString();

    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemCode);
        dest.writeString(description);
        dest.writeString(description2);
        dest.writeDouble(unitPrice);
        dest.writeInt(defaultQty);
        dest.writeInt(qtyMultiples);
        dest.writeString(imageUrl);
    }
    private ArrayList<Product> productList;

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setNoticeArrayList(ArrayList<Product> productList) {
        this.productList = productList;
    }
}
