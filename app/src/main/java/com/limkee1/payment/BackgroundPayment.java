package com.limkee1.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.limkee1.notification.SMSNotification;
import com.stripe.android.model.Card;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackgroundPayment extends AsyncTask<String,Void,String> {
    private Context context;
    private Activity activity;
    private String totalPayable;
    private double tp;
    private Card card;
    private static Customer customer;
    CompositeDisposable compositeDisposable  = new CompositeDisposable();
    private String ETADeliveryDate;
    private String newOrderID;
    private ArrayList<Product> orderList;
    private String isEnglish;
    private String paperBagNeeded;
    private int paperBagRequired;

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

    protected void saveDeliveryDate (String deliveryDate){
        ETADeliveryDate = deliveryDate;
    }

    protected void saveOrderList (ArrayList<Product> orderList){
        this.orderList = orderList;
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
        String type = params[0];
        totalPayable = params[1];
        String payment_url = "http://13.229.114.72:80/JavaBridge/";
        String post_data;
        isEnglish = params[3];
        paperBagNeeded = params[4];
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
                            +"&"+URLEncoder.encode("cardNumber","UTF-8")+"="+URLEncoder.encode(encodeBase64(card.getNumber()),"UTF-8")
                            +"&"+URLEncoder.encode("cardExpMonth","UTF-8")+"="+URLEncoder.encode(card.getExpMonth().toString(),"UTF-8")
                            +"&"+URLEncoder.encode("cardExpYear","UTF-8")+"="+URLEncoder.encode(card.getExpYear().toString(),"UTF-8")
                            +"&"+URLEncoder.encode("cardCVC","UTF-8")+"="+URLEncoder.encode(encodeBase64(card.getCVC()),"UTF-8")
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
        } else if (type.equals("pay_with_saved_card")){
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
        tp = Double.parseDouble(totalPayable);
        tp = tp/100;
        if (result != null && result.equals("success")){
            //insert into database 3 tables
            createSalesOrder();
        } else{
            Intent it = new Intent(context.getApplicationContext(), ConfirmationActivity.class);
            it.putExtra("result","unsuccess");
            it.putExtra("totalPayable", tp);
            it.putExtra("customer", customer);
            it.putExtra("language", isEnglish);
            it.putExtra("paperBagNeeded", paperBagNeeded);
            it.putParcelableArrayListExtra("orderList", orderList);
            context.startActivity(it);

            activity.finish();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private void createSalesOrder() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build().create(PostData.class);

        if (paperBagNeeded.equals("yes")) {
            paperBagRequired = 1;
        } else {
            paperBagRequired = 0;
        }

        compositeDisposable.add(postData.addSalesOrder(customer.getDebtorCode(), paperBagRequired)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderResponse, this::handleError));
    }

    private void handleSalesOrderResponse(String orderNo) {
        if (orderNo != null || orderNo.length() != 0) {
            //create Sales Order Details
            newOrderID = orderNo;
            System.out.println("SALES ORDER CREATED " + newOrderID);
            createSalesOrderDetails(newOrderID);
        } else {
            System.out.println("Background payment failed in line 244. SALES ORDER NOT CREATED " + orderNo);
            //show error msg
        }
    }

    private void handleError(Throwable error) {
        System.out.println("Error " + error.getMessage());
    }

    private void createSalesOrderDetails(String orderNo) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(PostData.class);

        DecimalFormat df = new DecimalFormat("#0.00");
        double totalAmt = tp*(100.0/107.0);
        totalAmt = Double.parseDouble(df.format(totalAmt));
        System.out.println("tocheck" +orderNo);
        compositeDisposable.add(postData.addSalesOrderDetails(ETADeliveryDate, totalAmt, orderNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderDetailsResponse, this::handleError));
    }

    private void handleSalesOrderDetailsResponse(boolean added) {
        if (added) {
            //create Sales Order Quantity
            createSalesOrderQuantity();
        }
    }

    private void createSalesOrderQuantity() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(PostData.class);

        ArrayList<String> itemQuantity = new ArrayList<String>();

        for (Product p : orderList) {
            itemQuantity.add(p.getItemCode() + "&" + p.getDefaultQty());
        }

        System.out.println("quantity + " + itemQuantity);
        compositeDisposable.add(postData.addSalesOrderQuantity(itemQuantity, newOrderID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderQuantityResponse, this::handleError));

    }

    private void handleSalesOrderQuantityResponse(int numProducts) {
        System.out.println("SALES ORDER NUMBER OF PRODUCTS " + numProducts + " and order list size is " + orderList.size());

        if (numProducts == orderList.size()) {

            //concatenate zeros
            if (newOrderID.length() == 1) {
                newOrderID = "00000" + newOrderID;
            } else if (newOrderID.length() == 2) {
                newOrderID = "0000" + newOrderID;
            } else if (newOrderID.length() == 3) {
                newOrderID = "000" + newOrderID;
            } else if (newOrderID.length() == 4) {
                newOrderID = "00" + newOrderID;
            } else if (newOrderID.length() == 5) {
                newOrderID = "0" + newOrderID;
            } else if (newOrderID.length() == 6) {
                //remain as original
            } else {
                newOrderID = "";
            }

            Date todayDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyMM");
            String todayYearMonth = formatter.format(todayDate);

            newOrderID = todayYearMonth + "-" + newOrderID;

            //get the last 2 digit of the year
            String year = ETADeliveryDate.substring(0, 4);
            //get the 2 digit of the month
            String month = ETADeliveryDate.substring(6, 7);
            //if delivery month contains "-", single digit month. add 0
            //else double digit month
            if (month.equals("-")) {
                month = "0" + ETADeliveryDate.substring(5, 6);
            } else {
                month = ETADeliveryDate.substring(5, 7);
            }
            String day = ETADeliveryDate.substring(8);
            if (day.length() == 1) {
                day = "0" + day;
            }
            String deliveryDate = day + "/" + month + "/" + year;

            SMSNotification notif = new SMSNotification(context, activity);
            notif.execute(customer.getDeliveryContact(), deliveryDate, newOrderID, isEnglish);

            Intent it = new Intent(context.getApplicationContext(), ConfirmationActivity.class);
            it.putExtra("result", "success");
            it.putExtra("totalPayable", tp);
            it.putExtra("customer", customer);
            it.putExtra("orderId", newOrderID);
            it.putExtra("language", isEnglish);
            it.putExtra("deliveryDate", deliveryDate);
            it.putParcelableArrayListExtra("orderList", orderList);
            context.startActivity(it);

            activity.finish();

        } else {
            System.out.println("Background payment failed in line 376");
            //show error msg
            //delete sales order and sales order detail record
        }

    }

}