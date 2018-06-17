package com.limkee.payment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.limkee.R;

public class ConfirmationActivity extends AppCompatActivity {
    public static Bundle myBundle = new Bundle();
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        myBundle = getIntent().getExtras();
        result = myBundle.getString("result");
        TextView pmtResult = ((Activity)this).findViewById(R.id.pmtResult);
        pmtResult.setText(result);
    }

}
