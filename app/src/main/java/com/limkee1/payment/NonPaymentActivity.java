package com.limkee1.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.limkee1.BaseActivity;
import com.limkee1.R;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.limkee1.notification.SMSNotification;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NonPaymentActivity extends BaseActivity implements NonPaymentFragment.OnFragmentInteractionListener{
    private String totalPayable;
    public static Bundle myBundle = new Bundle();
    private String deliveryDate;
    private Context context;
    private Customer customer;
    private double subtotal;
    private int paperBagNeeded;
    private String isEnglish;
    public static Activity activity;
    public static Retrofit retrofit;
    double walletDeduction;
    double totalAmount;
    double tp;
    private ArrayList<Product> orderList;
    String totalAmt;
    private String newOrderID;
    CompositeDisposable compositeDisposable  = new CompositeDisposable();
    private NonPaymentFragment nonPaymentFragment = new NonPaymentFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_payment);
        Toolbar toolbar = findViewById(com.limkee1.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBundle = getIntent().getExtras();
        activity = this;
        context = getApplicationContext();
        customer = myBundle.getParcelable("customer");
        deliveryDate = myBundle.getString("deliveryDate");
        tp = myBundle.getDouble("totalPayable");
        walletDeduction = myBundle.getDouble("walletDeduction");
        totalAmount = myBundle.getDouble("totalAmount");
        subtotal = tp;
        isEnglish = myBundle.getString("language");
        paperBagNeeded = myBundle.getInt("paperBagRequired");
        orderList = myBundle.getParcelableArrayList("orderList");

        TextView tv_totalAmt = (TextView) findViewById(R.id.tv_totalAmt);
        TextView tv_walletDeduction = (TextView) findViewById(R.id.tv_walletDeduction);
        TextView tv_noPayment = (TextView) findViewById(R.id.tv_noPayment);

        if (isEnglish.equals("Yes")){
            tv_noPayment.setText("No additional payment is needed" + "\n" + "Please confirm to place order");
            tv_totalAmt.setText("Total amount is " + String.format("$%.2f", totalAmount));
            tv_walletDeduction.setText("Wallet Deduction of " + String.format("$%.2f", walletDeduction));
        } else {
            tv_noPayment.setText("不需要额外支付" + "\n" + "请确认下订单");
            tv_totalAmt.setText("总额是 " + String.format("$%.2f", totalAmount));
            tv_walletDeduction.setText("钱包扣除 " + String.format("$%.2f", walletDeduction));
        }

        TextView tv_totalPayable = (TextView)findViewById(R.id.totalPayable);
        tv_totalPayable.setText(String.format("$%.2f", tp));

        totalPayable = String.valueOf((int) Math.round(tp * 100));
        totalAmt = String.valueOf((int) Math.round(totalAmount * 100));

        Bundle bundle = new Bundle();
        bundle.putParcelable("customer",customer);
        bundle.putString("language", isEnglish);
        nonPaymentFragment.setArguments(bundle);
        loadFragment(nonPaymentFragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(com.limkee1.R.id.flContent, fragment);
        fragmentTransaction.commit();
    }

    public void next(View view){
        createSalesOrder();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Back button clicked
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title){
        TextView titleTextView = findViewById(com.limkee1.R.id.toolbar_title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
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

        compositeDisposable.add(postData.addSalesOrder(customer.getDebtorCode(), paperBagNeeded)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderResponse, this::handleError));
    }

    private void handleSalesOrderResponse(String orderNo) {
        if (orderNo != null || orderNo.length() != 0) {
            //create Sales Order Details
            newOrderID = orderNo;
            createSalesOrderDetails(newOrderID);
        } else {
            System.out.println("NonPaymentActivity failed in line 151. SALES ORDER NOT CREATED " + orderNo);
        }
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

        double subtotalAmt = totalAmount*(100.0/107.0);
        subtotalAmt = Double.parseDouble(df.format(subtotalAmt));
        double paidAmt = Double.parseDouble(df.format(tp));

        compositeDisposable.add(postData.addSalesOrderDetails(deliveryDate, subtotalAmt, paidAmt, orderNo)
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

        compositeDisposable.add(postData.addSalesOrderQuantity(itemQuantity, newOrderID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderQuantityResponse, this::handleError));
    }

    private void handleSalesOrderQuantityResponse(int numProducts) {

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
            String year = deliveryDate.substring(0, 4);
            //get the 2 digit of the month
            String month = deliveryDate.substring(6, 7);
            //if delivery month contains "-", single digit month. add 0
            //else double digit month
            if (month.equals("-")) {
                month = "0" + deliveryDate.substring(5, 6);
            } else {
                month = deliveryDate.substring(5, 7);
            }
            String day = deliveryDate.substring(8);
            if (day.length() == 1) {
                day = "0" + day;
            }
            String deliveryDate = day + "/" + month + "/" + year;

            SMSNotification notif = new SMSNotification(context, activity);
            notif.execute(customer.getDeliveryContact(), deliveryDate, newOrderID, isEnglish);

            Intent it = new Intent(context.getApplicationContext(), NonPaymentConfirmationActivity.class);
            it.putExtra("result", "success");
            it.putExtra("customer", customer);
            it.putExtra("orderId", newOrderID);
            it.putExtra("language", isEnglish);
            it.putExtra("walletDeduction", totalAmount);
            it.putParcelableArrayListExtra("orderList", orderList);
            context.startActivity(it);
            activity.finish();
        } else {
            System.out.println("Failed to place order in NonPaymentActivity in line 278");
            //delete sales order and sales order detail record
        }
    }

    private void handleError(Throwable error) {
        System.out.println("Error " + error.getMessage());
    }

}
