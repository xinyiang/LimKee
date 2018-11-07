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

    public static boolean isLeapYear(int year) {
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

    public static String getChineseMonth(String engMonth){
        String chineseMth = "";

        if (engMonth.equals("Jan")){
            chineseMth = "一月";
        } else if (engMonth.equals("Feb")){
            chineseMth = "二月";
        } else if (engMonth.equals("Mar")){
            chineseMth = "三月";
        } else if (engMonth.equals("Apr")){
            chineseMth = "四月";
        } else if (engMonth.equals("May")){
            chineseMth = "五月";
        } else if (engMonth.equals("Jun")){
            chineseMth = "六月";
        } else if (engMonth.equals("Jul")){
            chineseMth = "七月";
        } else if (engMonth.equals("Aug")){
            chineseMth = "八月";
        } else if (engMonth.equals("Sep")){
            chineseMth = "九月";
        } else if (engMonth.equals("Oct")){
            chineseMth = "十月";
        } else if (engMonth.equals("Nov")){
            chineseMth = "十一月";
        } else if (engMonth.equals("Dec")){
            chineseMth = "十二月";
        }
        return  chineseMth;
    }

    public static String getMonth(int numMonth){
        String engMonth = "";

        if (numMonth == 1) {
            engMonth = "Jan";
        } else if (numMonth == 2) {
            engMonth = "Feb";
        } else if (numMonth == 3) {
            engMonth = "Mar";
        } else if (numMonth == 4) {
            engMonth = "Apr";
        } else if (numMonth == 5) {
            engMonth = "May";
        } else if (numMonth == 6) {
            engMonth = "Jun";
        } else if (numMonth == 7) {
            engMonth = "Jul";
        } else if (numMonth == 8) {
            engMonth = "Aug";
        } else if (numMonth == 9) {
            engMonth = "Sep";
        } else if (numMonth == 10) {
            engMonth = "Oct";
        } else if (numMonth == 11) {
            engMonth = "Nov";
        }  else if (numMonth == 12) {
            engMonth = "Dec";
        }
        return engMonth;
    }

}
