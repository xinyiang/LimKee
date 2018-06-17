package com.limkee.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

        if (result.equals("login success")){
            builder= new AlertDialog.Builder(context);
            compareTime(builder);
            Customer customer = new Customer(companyCode,password,"","","");
            Intent it = new Intent(context.getApplicationContext(), NavigationActivity.class);
            it.putExtra("isLogin", true);
            it.putExtra("customer", (Serializable) customer);
            context.startActivity(it);
        }else{
            pwdValidate.setText("Invalid Company Code or Password");
        }
    }

    public void compareTime(AlertDialog.Builder builder){
        try {
            DateFormat df = new SimpleDateFormat("HH:mm");
            String ct = df.format(Calendar.getInstance().getTime());
            Date currentTime = df.parse(ct);

            Date ct1 = df.parse("04:00");
            Date ct2 = df.parse("04:05");
            Date ct3 = df.parse("04:15");
            Date ct4 = df.parse("04:25");
            Date ct5 = df.parse("04:30");
            Date ct6 = df.parse("05:40");
            Date ct7 = df.parse("05:45");
            Date ct8 = df.parse("05:50");
            Date ct9 = df.parse("06:00");
            Date ct10 = df.parse("07:20");
            Date ct11 = df.parse("07:25");
            Date ct12 = df.parse("07:40");
            Date ct13 = df.parse("09:20");
            Date ct14 = df.parse("09:40");
            Date ct15 = df.parse("10:00");
            Date ct16 = df.parse("10:30");

            if (currentTime.after(ct16)||currentTime.before(ct1)) {
                builder.setMessage("Next order cutoff time will be 4:00AM");
            }else if (ct2.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 04:05AM");
            }else if (ct3.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 4:15AM");
            }else if (ct4.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 4:25AM");
            }else if (ct5.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 4:30AM");
            }else if (ct6.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 5:40AM");
            }else if (ct7.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 5:45AM");
            }else if (ct8.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 5:50AM");
            }else if (ct9.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 6:00AM");
            }else if (ct10.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 7:20AM");
            }else if (ct11.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 7:25AM");
            }else if (ct12.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 7:40AM");
            }else if (ct13.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 9:20AM");
            }else if (ct14.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 9:40AM");
            }else if (ct15.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 10:00AM");
            }else if (ct16.after(currentTime)){
                builder.setMessage("Next order cutoff time for today will be 10:30AM");
            }
            final AlertDialog ad = builder.create();
            ad.show();
            new CountDownTimer(25000, 10000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub
                }
                @Override
                public void onFinish() {
                    ad.dismiss();
                }
            }.start();
        } catch (ParseException e) {
            e.printStackTrace();// Invalid date was entered
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