package com.limkee.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

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

import com.limkee.entity.Customer;
import com.stripe.android.model.Card;

    public class BackgroundPayment extends AsyncTask<String,Void,String> {
        private Context context;
        private Activity activity;
        private String totalPayable;
        private Card card;
        private Customer customer;

    BackgroundPayment(Context ctx, Activity act) {
        context = ctx;
        activity = act;
    }

    protected void saveCard (Card card){
        this.card = card;
    }

    protected void saveCustomer (Customer customer){
        this.customer = customer;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        totalPayable = params[1];
        String payment_url = "http://13.229.114.72:80/JavaBridge/";
        String post_data;
        if(type.equals("pay_with_new_card")){
            String token = params[2];
            try {
                payment_url += "payment_newcard.php";
                URL url = new URL(payment_url);
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                OutputStream ops = huc.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                if (card == null){
                    post_data = URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(token,"UTF-8")
                            +"&"+URLEncoder.encode("totalPayable","UTF-8")+"="+URLEncoder.encode(totalPayable,"UTF-8");
                } else {
                    post_data = URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(token,"UTF-8")
                            +"&"+URLEncoder.encode("totalPayable","UTF-8")+"="+URLEncoder.encode(totalPayable,"UTF-8")
                            +"&"+URLEncoder.encode("cardNumber","UTF-8")+"="+URLEncoder.encode(card.getNumber(),"UTF-8")
                            +"&"+URLEncoder.encode("cardExpMonth","UTF-8")+"="+URLEncoder.encode(card.getExpMonth().toString(),"UTF-8")
                            +"&"+URLEncoder.encode("cardExpYear","UTF-8")+"="+URLEncoder.encode(card.getExpYear().toString(),"UTF-8")
                            +"&"+URLEncoder.encode("cardCVC","UTF-8")+"="+URLEncoder.encode(card.getCVC(),"UTF-8")
                            +"&"+URLEncoder.encode("debtorCode","UTF-8")+"="+URLEncoder.encode(customer.getDebtorCode(),"UTF-8");
                }
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
        }
        else if (type.equals("pay_with_saved_card")){
            String lastFourDigit = params[2];
            try {
                payment_url += "payment_savedcard.php";
                URL url = new URL(payment_url);
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                OutputStream ops = huc.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                post_data = URLEncoder.encode("lastFourDigit","UTF-8")+"="+URLEncoder.encode(lastFourDigit,"UTF-8")
                        +"&"+URLEncoder.encode("totalPayable","UTF-8")+"="+URLEncoder.encode(totalPayable,"UTF-8")
                        +"&"+URLEncoder.encode("debtorCode","UTF-8")+"="+URLEncoder.encode(customer.getDebtorCode(),"UTF-8");
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
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent it = new Intent(context.getApplicationContext(), ConfirmationActivity.class);
        it.putExtra("result",result);
        Double tp = Double.parseDouble(totalPayable);
        it.putExtra("totalPayable", tp/100);
        it.putExtra("customer", customer);
        context.startActivity(it);
        activity.finish();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}