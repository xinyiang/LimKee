package com.limkee.entity;
import java.io.Serializable;

/**
 * Created by Miaozi on 21/5/18.
 */
@SuppressWarnings("serial")
public class Customer implements Serializable{
    private String companyCode;
    private String password;
    private String debtorCode;
    private String debtorName;
    private String deliveryContact;

    public Customer(String companyCode, String password,String debtorCode,String debtorName,String deliveryContact) {
        this.companyCode = companyCode;
        this.password = password;
        this.debtorCode = debtorCode;
        this.debtorName = debtorName;
        this.deliveryContact = deliveryContact;
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
}