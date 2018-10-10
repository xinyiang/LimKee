package com.limkee1.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDetails implements Parcelable {
    String orderID;
    String orderDate;
    double subtotal;
    String status;
    String deliveryDate;
    int paperBagRequired;

    public OrderDetails(){
    }

    public OrderDetails(String orderID, String orderDate, String status, String deliveryDate, double subtotal, int paperBagRequired){
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.subtotal = subtotal;
        this.paperBagRequired = paperBagRequired;
    }

    public OrderDetails(String orderID, double refundSubtotal){
        this.orderID = orderID;
        this.subtotal = refundSubtotal;
    }

    public OrderDetails(String orderID, double refundSubtotal, String orderDate, String status, String deliveryDate){
        this.orderID = orderID;
        this.subtotal = refundSubtotal;
        this.orderDate = orderDate;
        this.status = status;
        this.deliveryDate = deliveryDate;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getPaperBagRequired() {
        return paperBagRequired;
    }

    public void setPaperBagRequired(int paperBagRequired) {
        this.paperBagRequired = paperBagRequired;
    }

    public OrderDetails(Parcel in) {
        super();
        readFromParcel(in);
    }


    public static final Parcelable.Creator<OrderDetails> CREATOR = new Parcelable.Creator<OrderDetails>() {
        public OrderDetails createFromParcel(Parcel in) {
            return new OrderDetails(in);
        }

        public OrderDetails[] newArray(int size) {

            return new OrderDetails[size];
        }

    };

    public void readFromParcel(Parcel in) {
        orderID = in.readString();
        orderDate = in.readString();
        status = in.readString();
        deliveryDate = in.readString();
        subtotal = in.readDouble();
        paperBagRequired = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderID);
        dest.writeString(orderDate);
        dest.writeString(status);
        dest.writeString(deliveryDate);
        dest.writeDouble(subtotal);
        dest.writeInt(paperBagRequired);
    }
}
