package com.limkee;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.limkee.locale.MyContextWrapper;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    public static String lang = "en";
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, lang));
    }
}