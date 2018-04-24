package com.limkee.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.limkee.R;

import com.limkee.navigation.NavigationActivity;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();

      //  Intent intent2 = new Intent(this,NavigationActivity.class);
      //  startActivity(intent2);
      //  finish();


    }
}
