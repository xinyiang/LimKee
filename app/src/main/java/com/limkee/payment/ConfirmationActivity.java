package com.limkee.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.limkee.BaseActivity;
import com.limkee.R;
import com.limkee.entity.Customer;
import com.limkee.navigation.NavigationActivity;


public class ConfirmationActivity extends BaseActivity {
    public static Bundle myBundle = new Bundle();
    String result;
    Context context;
    Customer customer;
    private View view;
    double tp = 0.00;
    private String orderId;
    private String language;

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
        System.out.println(orderId+"!!!");
        TextView pmtResult = ((Activity)this).findViewById(R.id.pmtResult);
        TextView description = ((Activity)this).findViewById(R.id.description);
        TextView notifDescription = ((Activity)this).findViewById(R.id.smsNotification);
        notifDescription.setTypeface(notifDescription.getTypeface(), Typeface.ITALIC);
        ImageView iv = findViewById(R.id.status);

        if (result != null && result.equals("success")){
            iv.setImageResource(R.drawable.success);
            description.setText(String.format("$%.2f", tp) + getResources().getString(R.string.payment_successful_description));
            if (language.equals("Yes")){
                pmtResult.setText("Order ID: #" + orderId + "\n" + getResources().getString(R.string.payment_successful));
                notifDescription.setText("A SMS will be sent to +65 " + customer.getDeliveryContact() + ". \n Please contact Lim Kee for mobile updates.");
            } else {
                pmtResult.setText("订单号: #" + orderId + "\n" + getResources().getString(R.string.payment_successful));
                notifDescription.setText("确认短信会发至 +65 " + customer.getDeliveryContact() + "。 \n 如果电话号码变更，请告知林记包点。");
            }
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
