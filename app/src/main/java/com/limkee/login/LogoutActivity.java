package com.limkee.login;

import android.content.Intent;;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.limkee.BaseActivity;
import com.limkee.R;
import android.os.Bundle;
import com.limkee.BaseActivity;
import com.limkee.R;


public class LogoutActivity extends BaseActivity {
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        loginPrefsEditor.clear();
        loginPrefsEditor.commit();

        finish();
    }
}