package com.limkee1.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Xin Yi on 24/6/2018.
 */

public class Product implements Parcelable{

    private String itemCode;
    private String description;
    private String description2;
    private double unitPrice;
    private String uom;
    private  int defaultQty;
    private  int qtyMultiples;
    private String imageUrl;

    public Product(String itemCode, String description, String description2, double unitPrice, String uom, String imageUrl, int defaultQty, int qtyMultiples){
        this.itemCode = itemCode;
        this.description = description;
        this.description2 = description2;
        this.unitPrice = unitPrice;
        this.uom = uom;
        this.imageUrl = imageUrl;
        this.defaultQty = defaultQty;
        this.qtyMultiples = qtyMultiples;
    }

    public Product(String itemCode, int qty) {
        this.itemCode = itemCode;
        this.defaultQty = qty;
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

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
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
        uom = in.readString();
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
        dest.writeString(uom);
        dest.writeInt(defaultQty);
        dest.writeInt(qtyMultiples);
        dest.writeString(imageUrl);
    }

}
