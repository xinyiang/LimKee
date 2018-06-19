package com.limkee.entity;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Miaozi on 21/5/18.
 */
@SuppressWarnings("serial")
public class Customer implements Parcelable{
    private String companyCode;
    private String password;
    private String debtorCode;
    private String debtorName;
    private  String companyName;
    private String deliveryContact;
    private String deliverFax1;
    private String invAddr1;
    private String invAddr2;
    private String invAddr3;
    private String invAddr4;
    private String deliverAddr1;
    private String deliverAddr2;
    private String deliverAddr3;
    private String deliverAddr4;
    private String displayTerm;
    private String status;
    private int routeNo;

    public Customer(String companyCode, String password,String debtorCode,String debtorName,String deliveryContact) {
        this.companyCode = companyCode;
        this.password = password;
        this.debtorCode = debtorCode;
        this.debtorName = debtorName;
        this.deliveryContact = deliveryContact;
    }
    public Customer(String companyCode, String password,String debtorCode,String companyName, String debtorName,String deliveryContact,String deliverFax1,String invAddr1, String invAddr2,String invAddr3,String invAddr4,String deliverAddr1,String deliverAddr2, String deliverAddr3,String deliverAddr4, String displayTerm,String status,int routeNo) {
        this.companyCode = companyCode;
        this.password = password;
        this.debtorCode = debtorCode;
        this.companyName = companyName;
        this.debtorName = debtorName;
        this.deliveryContact = deliveryContact;
        this.deliverFax1 = deliverFax1;
        this.invAddr1 = invAddr1;
        this.invAddr2 = invAddr2;
        this.invAddr3 = invAddr3;
        this.invAddr4 = invAddr4;
        this.deliverAddr1 = deliverAddr1;
        this.deliverAddr2 = deliverAddr2;
        this.deliverAddr3 = deliverAddr3;
        this.deliverAddr4 = deliverAddr4;
        this.displayTerm = displayTerm;
        this.status = status;
        this.routeNo = routeNo;
    }
    public String getCustomerCode() {
        return companyCode;
    }

    public void setCustomerCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDebtorCode() {
        return debtorCode;
    }

    public void setDebtorCode(String debtorCode) {
        this.debtorCode = debtorCode;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getDeliveryContact() {
        return deliveryContact;
    }

    public void setDeliveryContact(String deliveryContact) {
        this.deliveryContact = deliveryContact;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDeliverFax1() {
        return deliverFax1;
    }

    public void setDeliverFax1(String deliverFax1) {
        this.deliverFax1 = deliverFax1;
    }

    public String getInvAddr1() {
        return invAddr1;
    }

    public void setInvAddr1(String invAddr1) {
        this.invAddr1 = invAddr1;
    }

    public String getInvAddr2() {
        return invAddr2;
    }

    public void setInvAddr2(String invAddr2) {
        this.invAddr2 = invAddr2;
    }

    public String getInvAddr3() {
        return invAddr3;
    }

    public void setInvAddr3(String invAddr3) {
        this.invAddr3 = invAddr3;
    }

    public String getInvAddr4() {
        return invAddr4;
    }

    public void setInvAddr4(String invAddr4) {
        this.invAddr4 = invAddr4;
    }

    public String getDeliverAddr1() {
        return deliverAddr1;
    }

    public void setDeliverAddr1(String deliverAddr1) {
        this.deliverAddr1 = deliverAddr1;
    }

    public String getDeliverAddr2() {
        return deliverAddr2;
    }

    public void setDeliverAddr2(String deliverAddr2) {
        this.deliverAddr2 = deliverAddr2;
    }

    public String getDeliverAddr3() {
        return deliverAddr3;
    }

    public void setDeliverAddr3(String deliverAddr3) {
        this.deliverAddr3 = deliverAddr3;
    }

    public String getDeliverAddr4() {
        return deliverAddr4;
    }

    public void setDeliverAddr4(String deliverAddr4) {
        this.deliverAddr4 = deliverAddr4;
    }

    public String getDisplayTerm() {
        return displayTerm;
    }

    public void setDisplayTerm(String displayTerm) {
        this.displayTerm = displayTerm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(int routeNo) {
        this.routeNo = routeNo;
    }

    public Customer(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {

            return new Customer[size];
        }

    };

    public void readFromParcel(Parcel in) {
        companyCode = in.readString();
        password = in.readString();
        debtorCode = in.readString();
        debtorName = in.readString();
        companyName = in.readString();
        deliveryContact = in.readString();
        deliverFax1 = in.readString();
        invAddr1 = in.readString();
        invAddr2 = in.readString();
        invAddr3 = in.readString();
        invAddr4 = in.readString();
        deliverAddr1 = in.readString();
        deliverAddr2 = in.readString();
        deliverAddr3 = in.readString();
        deliverAddr4 = in.readString();
        displayTerm = in.readString();
        status = in.readString();
        routeNo = in.readInt();


    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(companyCode);
        dest.writeString(password);
        dest.writeString(debtorCode);
        dest.writeString(debtorName);
        dest.writeString(companyName);
        dest.writeString(deliveryContact);
        dest.writeString(deliverFax1);
        dest.writeString(invAddr1);
        dest.writeString(invAddr2);
        dest.writeString(invAddr3);
        dest.writeString(invAddr4);
        dest.writeString(deliverAddr1);
        dest.writeString(deliverAddr2);
        dest.writeString(deliverAddr3);
        dest.writeString(deliverAddr4);
        dest.writeString(displayTerm);
        dest.writeString(status);
        dest.writeInt(routeNo);
    }
}