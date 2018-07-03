package com.limkee.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.limkee.BaseActivity;
import com.limkee.R;
import com.limkee.navigation.NavigationActivity;
import com.limkee.order.ConfirmOrderActivity;

public class ConfirmationActivity extends BaseActivity {
    public static Bundle myBundle = new Bundle();
    String result;
    Context context;
    double tp = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        myBundle = getIntent().getExtras();
        context = getApplicationContext();
        result = myBundle.getString("result");
        tp = getIntent().getDoubleExtra("totalPayable",0.00);
        TextView pmtResult = ((Activity)this).findViewById(R.id.pmtResult);
        TextView description = ((Activity)this).findViewById(R.id.description);
        ImageView iv = findViewById(R.id.status);
        if (result.equals("success")){
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
        if (result.equals("success")){
            Intent it = new Intent(getApplicationContext(), NavigationActivity.class);
            context.startActivity(it);
            this.finish();
        }else{
            Intent it = new Intent(getApplicationContext(), PaymentActivity.class);
            it.putExtra("totalPayable", tp);
            context.startActivity(it);
            this.finish();
        }
    }

}
