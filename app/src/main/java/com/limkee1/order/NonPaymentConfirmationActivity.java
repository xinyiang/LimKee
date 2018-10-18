package com.limkee1.order;

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
import com.limkee1.navigation.NavigationActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NonPaymentConfirmationActivity extends BaseActivity  {

    public static Bundle myBundle = new Bundle();
    Context context;
    Customer customer;
    public static Retrofit retrofit;
    private double walletDeduction;
    private String isEnglish;
    private String orderID;
    CompositeDisposable compositeDisposable  = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_payment_confirmation);

        myBundle = getIntent().getExtras();
        customer = myBundle.getParcelable("customer");
        walletDeduction =myBundle.getDouble("walletDeduction");
        isEnglish = myBundle.getString("language");
        orderID = myBundle.getString("orderId");

        TextView result = ((Activity)this).findViewById(R.id.result);
        TextView description = ((Activity)this).findViewById(R.id.description);
        TextView notifDescription = ((Activity)this).findViewById(R.id.smsNotification);
        notifDescription.setTypeface(notifDescription.getTypeface(), Typeface.ITALIC);
        ImageView iv = findViewById(R.id.status);
        iv.setImageResource(R.drawable.success);

        if (isEnglish.equals("Yes")){
            description.setText(String.format("$%.2f", walletDeduction) + " is deducted from your wallet balance");
            result.setText("Order ID: #" + orderID + "\n" + "Order Placed Successfully!");
            notifDescription.setText("A SMS will be sent to +65 " + customer.getDeliveryContact() + "\n Please contact Lim Kee for mobile updates");
        } else {
            description.setText(String.format("$%.2f", walletDeduction) + "已从您的钱包扣除");
            result.setText("订单号: #" + orderID + "\n" + "订单已成功下单!");
            notifDescription.setText("确认短信会发至 +65 " + customer.getDeliveryContact() + "\n 如果电话号码变更，请告知林记包点");
        }
        doUpdateCustomerWallet(customer.getDebtorCode(), walletDeduction);
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


    public void back(View view){
        Intent it = new Intent(getApplicationContext(), NavigationActivity.class);
        context.startActivity(it);
        this.finish();
    }
}
