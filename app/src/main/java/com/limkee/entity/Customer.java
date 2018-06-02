package com.limkee.entity;

/**
 * Created by Miaozi on 21/5/18.
 */

public class Customer {
    private int customerCode;
    private String password;

    public Customer(int customerCode, String password) {
        this.customerCode = customerCode;
        this.password = password;
    }

    public int getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(int customerCode) {
        this.customerCode = customerCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}