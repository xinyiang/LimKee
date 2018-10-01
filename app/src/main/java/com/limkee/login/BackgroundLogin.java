package com.limkee.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

        loginPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        if (result != null && !result.equals("login unsuccess")){
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

            Intent it = new Intent(context, NavigationActivity.class);
            Gson gson = new Gson();
            String json = gson.toJson(customer);
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", companyCode);
            loginPrefsEditor.putString("password", password);

            loginPrefsEditor.putString("cutofftime", cutoffTime);
            loginPrefsEditor.putString("customer", json);
            loginPrefsEditor.putString("deliveryShift", deliveryShift);
            loginPrefsEditor.putBoolean("isLogin", true);
            loginPrefsEditor.putBoolean("isAlertDialogue", true);
            loginPrefsEditor.putString("language", isEnglish);
            loginPrefsEditor.putBoolean("FirstTimeLogin", true);
            loginPrefsEditor.apply();
            loginPrefsEditor.commit();
            context.startActivity(it);
        } else{
            if(isEnglish.equals("Yes")) {
                pwdValidate.setText("Invalid Username and/or Password");
            } else {
                pwdValidate.setText("用户名和/或密码错误");
            }
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();

        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}