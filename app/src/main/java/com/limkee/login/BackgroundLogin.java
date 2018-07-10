package com.limkee.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.google.gson.Gson;
import com.limkee.R;
import com.limkee.entity.Customer;
import com.limkee.navigation.NavigationActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Miaozi on 21/6/18.
 */

public class BackgroundLogin extends AsyncTask<String,Void,String> {
    private Context context;
    private String password;
    private String companyCode;
    private AlertDialog.Builder builder;
    private String isEnglish;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    BackgroundLogin(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String login_url = "http://13.229.114.72:80/JavaBridge/login.php";//13.229.114.72:80/JavaBridge/login.php
        if(type.equals("login")){
            try {
                companyCode = params[1];
                password = params[2];
                isEnglish = params[3];
                URL url = new URL(login_url);
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                OutputStream ops = huc.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String post_data = URLEncoder.encode("companyCode","UTF-8")+"="+URLEncoder.encode(companyCode,"UTF-8")
                        +"&"+URLEncoder.encode("HashPassword","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bw.write(post_data);
                bw.flush();
                bw.close();
                ops.close();
                InputStream is = huc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
                String result = "";
                String line;
                while((line = br.readLine())!=null){
                    result += line;
                }
                br.close();
                is.close();
                huc.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        TextView pwdValidate = ((Activity)context).findViewById(R.id.pwdvalidation);
        if (!result.equals("login unsuccess")){
            String[] array = result.split(",");
            final String cutoffTime = array[0];
            final String deliveryShift = array[1];
            String debtorCode = array[2];
            String companyCode = array[3];
            String password = array[4];
            String companyName = array[5];
            String debtorName = array[6];
            String deliveryContact = array[7];
            String deliveryContact2 = array[8];
            String invAddr1 = array[9];
            String invAddr2 = array[10];
            String invAddr3 = array[11];
            String invAddr4 = array[12];
            String deliverAddr1 = array[13];
            String deliverAddr2 = array[14];
            String deliverAddr3 = array[15];
            String deliverAddr4 = array[16];
            String displayTerm = array[17];
            String status = array[18];
            int routeNo = Integer.parseInt(array[19]);

            final Customer customer = new Customer(companyCode, password, debtorCode, companyName, debtorName, deliveryContact, deliveryContact2, invAddr1, invAddr2, invAddr3, invAddr4, deliverAddr1, deliverAddr2, deliverAddr3, deliverAddr4, displayTerm, status, routeNo);


           /*
            builder= new AlertDialog.Builder(context);
            //format cut off time to remove seconds
            if(isEnglish.equals("Yes")){
                builder.setMessage("Please place order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM for today's delivery");
            } else {
                builder.setMessage("今日订单请在" + getChineseTime(cutoffTime.substring(0,cutoffTime.length()-3)) + "前下单");
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                */
                    loginPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                    loginPrefsEditor = loginPreferences.edit();
                    Intent it = new Intent(context, NavigationActivity.class);

                    Gson gson = new Gson();
                    String json = gson.toJson(customer);
                    loginPrefsEditor.putString("cutofftime", cutoffTime);
                    loginPrefsEditor.putString("customer", json);
                    loginPrefsEditor.putString("deliveryShift", deliveryShift);
                    loginPrefsEditor.putBoolean("isLogin", true);
                    loginPrefsEditor.putBoolean("isAlertDialogue", true);
                    loginPrefsEditor.putString("language", isEnglish);
                    loginPrefsEditor.commit();

                    context.startActivity(it);
                    /*
                    dialog.dismiss();
                }
            });
            final AlertDialog ad = builder.create();
            ad.show();
            */
        } else{
            if(isEnglish.equals("Yes")) {
                pwdValidate.setText("Invalid Company Code and/or Password");
            } else {
                pwdValidate.setText("公司代码和/或密码错误");
            }

        }
    }

    public static String getChineseTime(String time){
        String minutes = time.substring(3,time.length());

        String chineseHour = "";
        String chineseTime;

        time = time.substring(0,2);
        //check hour
        if (time.equals("04")){
            chineseHour = "四";
        }  else if (time.equals("05")){
            chineseHour = "五";
        } else if (time.equals("06")){
            chineseHour = "六";
        } else if (time.equals("07")){
            chineseHour = "七";
        } else if (time.equals("08")){
            chineseHour = "八";
        } else if (time.equals("09")){
            chineseHour = "九";
        } else if (time.equals("10")) {
            chineseHour = "十";
        } else {
            chineseHour = "";
        }

        //check if got mins
        if (minutes.equals("00")){
            chineseTime = chineseHour + "点";
        } else if (minutes.equals("30")){
            chineseTime = chineseHour + "点半";
        } else{
            chineseTime = chineseHour + "点" + getNumber(minutes) + "分";
        }
        return chineseTime;
    }

    public static String getNumber(String number){
        String chineseNumber = "";

        if (number.equals("05")){
            chineseNumber = "零五";
        } else if (number.equals("10")){
            chineseNumber = "十";
        } else if (number.equals("15")){
            chineseNumber = "十五";
        } else if (number.equals("20")){
            chineseNumber = "二十";
        } else if (number.equals("25")){
            chineseNumber = "二十五";
        } else if (number.equals("35")){
            chineseNumber = "三十五";
        } else if (number.equals("40")){
            chineseNumber = "四十";
        } else if (number.equals("45")){
            chineseNumber = "四十五";
        } else if (number.equals("50")){
            chineseNumber = "五十";
        } else if (number.equals("55")){
            chineseNumber = "五十五";
        }  else {
            chineseNumber = "零";
        }
        return chineseNumber;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}