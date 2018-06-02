package com.limkee.login;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Miaozi on 22/5/18.
 */

public class BackgroundLogin extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog ad;

    public BackgroundLogin(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        //String login_url = "http://10.0.2.2/login.php";
        String login_url = "http://localhost/login.php";
        if(type.equals("login")){
            try {
                String username = params[1];
                String password = params[2];
                URL url = new URL(login_url);
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                OutputStream ops = huc.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")
                        +"&"+URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bw.write(post_data);
                bw.flush();
                bw.close();
                ops.close();
                InputStream is = huc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
                String result = "";
                String line = "";
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
        ad.setMessage(result);
        ad.show();
        System.out.println("LOGIN " + result);
    }

    @Override
    protected void onPreExecute() {
        ad = new AlertDialog.Builder(context).create();
        ad.setTitle("Login Status");
        System.out.println("Trying to login....");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}