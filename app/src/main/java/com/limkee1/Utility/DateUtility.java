package com.limkee1.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtility {

    public static int getLastDayOfMonth(String thisMonth){
        int lastDay = 0;
        int month = 0;

        if (thisMonth.substring(0,1).equals("0")){
            month = Integer.parseInt(thisMonth.substring(1));
        } else {
            month = Integer.parseInt(thisMonth);
        }

        if (month == 1){
            lastDay = 31;
        } else if (month == 2){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String today = sdf.format(new Date());
            String todayYear = today.substring(0,4);

            boolean isLeapYear = isLeapYear(Integer.parseInt(todayYear));
            if (isLeapYear){
                lastDay = 29;
            } else {
                lastDay = 28;
            }
        } else if (month == 3){
            lastDay = 31;
        } else if (month == 4){
            lastDay = 30;
        } else if (month == 5){
            lastDay = 31;
        } else if (month == 6){
            lastDay = 30;
        } else if (month == 7){
            lastDay = 30;
        } else if (month == 8){
            lastDay = 31;
        } else if (month == 9){
            lastDay = 30;
        } else if (month == 10){
            lastDay = 31;
        } else if (month == 11){
            lastDay = 30;
        } else{
            lastDay = 31;
        }
        return lastDay;
    }

    public  static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else {
            return true;
        }
    }
}
