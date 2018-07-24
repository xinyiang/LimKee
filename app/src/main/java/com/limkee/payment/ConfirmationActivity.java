package com.limkee.payment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.limkee.BaseActivity;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.entity.Customer;
import com.limkee.entity.Product;
import com.limkee.navigation.NavigationActivity;
import com.limkee.order.ConfirmOrderActivity;

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
    CompositeDisposable compositeDisposable;
    private String ETADeliveryDate;
    private String newOrderID;
    private double subtotal;
    private ArrayList<Product> orderList;
    private String isEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        myBundle = getIntent().getExtras();
        context = getApplicationContext();
        result = myBundle.getString("result");
        tp = getIntent().getDoubleExtra("totalPayable",0.00);
        customer = myBundle.getParcelable("customer");
        TextView pmtResult = ((Activity)this).findViewById(R.id.pmtResult);
        TextView description = ((Activity)this).findViewById(R.id.description);
        ImageView iv = findViewById(R.id.status);
        if (result != null && result.equals("success")){
            pmtResult.setText(getResources().getString(R.string.payment_successful));
            description.setText(String.format("$%.2f", tp) + getResources().getString(R.string.payment_successful_description));
            iv.setImageResource(R.drawable.success);

        }else{
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
        }else{
            Intent it = new Intent(getApplicationContext(), PaymentActivity.class);
            it.putExtra("totalPayable", tp);
            it.putExtra("customer", customer);
            context.startActivity(it);
            this.finish();
        }
    }
}
