package com.limkee1.payment;


import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundValidation extends AsyncTask<String,Void,String> {
    private String debtorCode;
    private String lastFourDigit;
    private String CVC;

    public AsyncResponse delegate = null;

    public BackgroundValidation(AsyncResponse delegate){
        this.delegate = delegate;
    }

    protected String encodeBase64 (String data){
        try{
            byte[] temp = data.getBytes("UTF-8");
            return Base64.encodeToString(temp,Base64.NO_WRAP | Base64.URL_SAFE);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return "";
    }

@Override
protected String doInBackground(String... params) {
    String payment_url = "http://13.229.114.72:80/JavaBridge/validate_cvc.php";
    String post_data;
    debtorCode = params[0];
    lastFourDigit = params[1];
    CVC = params[2];
    try {
        URL url = new URL(payment_url);
        HttpURLConnection huc = (HttpURLConnection)url.openConnection();
        huc.setRequestMethod("POST");
        huc.setDoInput(true);
        huc.setDoOutput(true);
        OutputStream ops = huc.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
        post_data = URLEncoder.encode("debtorCode","UTF-8")+"="+URLEncoder.encode(debtorCode,"UTF-8")
                +"&"+URLEncoder.encode("lastFourDigit","UTF-8")+"="+URLEncoder.encode(lastFourDigit,"UTF-8")
                +"&"+URLEncoder.encode("cvc","UTF-8")+"="+URLEncoder.encode(encodeBase64(CVC),"UTF-8");
        bw.write(post_data);
        bw.flush();
        bw.close();
        ops.close();
        InputStream is = huc.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String result = "";
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        br.close();
        is.close();
        huc.disconnect();
        result = sb.toString();
        return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


@Override
protected void onPostExecute(String result) {
    delegate.processFinish(result);
}

@Override
protected void onPreExecute() {
    super.onPreExecute();
}

@Override
protected void onProgressUpdate(Void... values) {
    super.onProgressUpdate(values);
}
}