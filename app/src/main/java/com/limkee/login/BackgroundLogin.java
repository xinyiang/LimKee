package com.limkee.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.widget.TextView;
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

/**
 * Created by Miaozi on 22/5/18.
 */

public class BackgroundLogin extends AsyncTask<String,Void,String> {
    private Context context;
    private String password;
    private String companyCode;
    private AlertDialog.Builder builder;

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
            System.out.println(result);
            String[] array = result.split(",");
            String cutoffTime = array[0];
            String companyCode = array[2];
            String password = array[3];
            String debtorCode = array[1];
            String companyName = array[4];
            String debtorName = array[5];
            String deliveryContact = array[6];
            String deliverFax1 = array[7];
            String invAddr1 = array[8];
            String invAddr2 = array[9];
            String invAddr3 = array[10];
            String invAddr4 = array[11];
            String deliverAddr1 = array[12];
            String deliverAddr2 = array[13];
            String deliverAddr3 = array[14];
            String deliverAddr4 = array[15];
            String displayTerm = array[16];
            String status = array[17];
            int routeNo = Integer.parseInt(array[18]);
            final Customer customer = new Customer(companyCode, password, debtorCode, companyName, debtorName, deliveryContact, deliverFax1, invAddr1, invAddr2, invAddr3, invAddr4, deliverAddr1, deliverAddr2, deliverAddr3, deliverAddr4, displayTerm, status, routeNo);
            builder= new AlertDialog.Builder(context);
            //format cut off time to remove seconds
            builder.setMessage("For today's order delivery, please place your order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent it = new Intent(context.getApplicationContext(), NavigationActivity.class);
                    it.putExtra("isLogin", true);
                    ArrayList<Customer> cust = new ArrayList<>();
                    cust.add(customer);
                    it.putParcelableArrayListExtra("customer", cust);
                    context.startActivity(it);
                    dialog.dismiss();
                }
            });
            final AlertDialog ad = builder.create();
            ad.show();
        } else{
            pwdValidate.setText("Invalid Company Code and/or Password");
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