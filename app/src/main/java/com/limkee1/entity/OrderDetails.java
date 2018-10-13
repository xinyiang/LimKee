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
    double paidAmt;
    double refundSubtotal;

    public OrderDetails(){
    }

    public OrderDetails(String orderID, String orderDate, String status, String deliveryDate, double subtotal, int paperBagRequired, double paidAmt){
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.subtotal = subtotal;
        this.paperBagRequired = paperBagRequired;
        this.paidAmt = paidAmt;
    }

    public OrderDetails(String orderID, double refundSubtotal){
        this.orderID = orderID;
        this.subtotal = refundSubtotal;
    }

    public OrderDetails(String orderID, double subtotal, String orderDate, String status, String deliveryDate, double paidAmt, double refundSubtotal){
        this.orderID = orderID;
        this.subtotal = subtotal;
        this.orderDate = orderDate;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.paidAmt = paidAmt;
        this.refundSubtotal = refundSubtotal;

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

    public double getPaidAmt() {
        return paidAmt;
    }

    public void setPaidAmt(double paidAmt) {
        this.paidAmt = paidAmt;
    }


    public OrderDetails(Parcel in) {
        super();
        readFromParcel(in);
    }

    public double getRefundSubtotal() {
        return refundSubtotal;
    }

    public void setRefundSubtotal(double refundSubtotal) {
        this.refundSubtotal = refundSubtotal;
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
        paidAmt = in.readDouble();
        refundSubtotal = in.readDouble();
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
        dest.writeDouble(paidAmt);
        dest.writeDouble(refundSubtotal);
    }
}
