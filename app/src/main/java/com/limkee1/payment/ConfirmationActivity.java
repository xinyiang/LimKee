package com.limkee1.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.limkee1.BaseActivity;
import com.limkee1.R;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.limkee1.navigation.NavigationActivity;
import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmationActivity extends BaseActivity {
    public static Bundle myBundle = new Bundle();
    String result;
    Context context;
    Customer customer;
    private View view;
    double tp = 0.00;
    private String orderId;
    private String language;
    private ArrayList<Product> orderList;
    private String paperBagNeeded;
    private String deliveryDate;
    public static Retrofit retrofit;
    private double walletDeduction;
    CompositeDisposable compositeDisposable  = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        myBundle = getIntent().getExtras();
        context = getApplicationContext();
        result = myBundle.getString("result");
        tp = getIntent().getDoubleExtra("totalPayable",0.00);
        customer = myBundle.getParcelable("customer");
        orderId = myBundle.getString("orderId");
        language = myBundle.getString("language");
        paperBagNeeded = myBundle.getString("paperBagNeeded");
        deliveryDate = myBundle.getString("deliveryDate");
        orderList = myBundle.getParcelableArrayList("orderList");
        walletDeduction = getIntent().getDoubleExtra("walletDeduction",0.00);

        TextView pmtResult = ((Activity)this).findViewById(R.id.pmtResult);
        TextView description = ((Activity)this).findViewById(R.id.description);
        TextView notifDescription = ((Activity)this).findViewById(R.id.smsNotification);
        notifDescription.setTypeface(notifDescription.getTypeface(), Typeface.ITALIC);
        ImageView iv = findViewById(R.id.status);

        if (result != null && result.equals("success")){
            iv.setImageResource(R.drawable.success);
            //description.setText(String.format("$%.2f", tp) + getResources().getString(R.string.payment_successful_description));

            if (language.equals("Yes")){
                pmtResult.setText("Order ID: #" + orderId + "\n" + getResources().getString(R.string.payment_successful));
                notifDescription.setText("A SMS will be sent to +65 " + customer.getDeliveryContact() + ". \n Please contact Lim Kee for mobile updates");
                //wallet balance also deducted
                description.setText(String.format("$%.2f", tp) + getResources().getString(R.string.payment_successful_description) + "\n" + String.format("$%.2f", walletDeduction) + " is deducted from your wallet balance");

            } else {
                pmtResult.setText("订单号: #" + orderId + "\n" + getResources().getString(R.string.payment_successful));
                notifDescription.setText("确认短信会发至 +65 " + customer.getDeliveryContact() + " \n 如果电话号码变更，请告知林记包点");
                description.setText(String.format("$%.2f", tp) + getResources().getString(R.string.payment_successful_description) + "\n" + String.format("$%.2f", walletDeduction) + "已从您的钱包扣除");

            }
            //process deduction in wallet
            doUpdateCustomerWallet(customer.getDebtorCode(), walletDeduction);

        } else {
            //pmtResult.setText(getResources().getString(R.string.payment_unsuccessful));
            //description.setText(String.format("$%.2f", tp) + getResources().getString(R.string.payment_unsuccessful_description));
            iv.setImageResource(R.drawable.fail);
        }
    }

    public void backTo(View view){
        if (result != null && result.equals("success")){
            Intent it = new Intent(getApplicationContext(), NavigationActivity.class);
            context.startActivity(it);
            this.finish();
        } else{
            Intent it = new Intent(getApplicationContext(), PaymentActivity.class);
            it.putExtra("totalPayable", tp);
            it.putExtra("customer", customer);
            it.putExtra("language", language);
            it.putExtra("paperBagRequired", paperBagNeeded);
            it.putExtra("deliveryDate", deliveryDate);
            it.putParcelableArrayListExtra("orderList", orderList);
            it.putExtra("walletDeduction", walletDeduction);
            context.startActivity(it);
            this.finish();
        }
    }

    private void doUpdateCustomerWallet(String customerCode, double walletDeduction) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(PostData.class);

        compositeDisposable.add(postData.reduceCustomerWalletAmount(customerCode, walletDeduction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleUpdateWalletResponse, this::handleError));

    }

    private void handleUpdateWalletResponse(boolean result) {
        if (result) {
            System.out.println("Wallet amount is updated");
        } else {
            System.out.println("Wallet amount did not get updated");
            //show error msg
        }
    }

    private void handleError(Throwable error) {
        System.out.println("Error " + error.getMessage());
    }


}
