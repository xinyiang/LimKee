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
    int transactionID;
    String transactionStatus;
    int deduction;
    double amount;


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

    public OrderDetails(int transactionID, String orderID, String transactionStatus, int deduction, double amount, double subtotal, double paidAmt, String orderDate, String status, String deliveryDate){
        this.transactionID = transactionID;
        this.orderID = orderID;
        this.transactionStatus = transactionStatus;
        this.deduction = deduction;
        this.amount = amount;
        this.subtotal = subtotal;
        this.orderDate = orderDate;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.paidAmt = paidAmt;
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

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public int getDeduction() {
        return deduction;
    }

    public void setDeduction(int deduction) {
        this.deduction = deduction;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
        paidAmt = in.readDouble();
        transactionID = in.readInt();
        transactionStatus = in.readString();
        amount = in.readDouble();
        deduction = in.readInt();
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
        dest.writeInt(transactionID);
        dest.writeString(transactionStatus);
        dest.writeInt(deduction);
        dest.writeDouble(amount);
    }
}
