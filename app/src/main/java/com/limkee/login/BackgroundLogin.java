package com.limkee.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

/**
 * Created by Miaozi on 22/5/18.
 */

public class BackgroundLogin extends AsyncTask<String,Void,String> {
    private Context context;
    private String companyCode;
    private String password;

    BackgroundLogin(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String login_url = "http://13.229.114.72:80/JavaBridge/login.php";
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

        //redirect
        if (result.equals("login success")){
            Customer customer = new Customer(companyCode,password,"","","");
            Intent it = new Intent(context.getApplicationContext(), NavigationActivity.class);
            it.putExtra("isLogin", true);
            it.putExtra("customer", (Serializable) customer);
            context.startActivity(it);
        }else{
            pwdValidate.setText("Invalid Company Code or Password");
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