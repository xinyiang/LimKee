package com.limkee.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.net.Uri;
import android.widget.Switch;

import com.limkee.BaseActivity;
import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.locale.MyContextWrapper;

import java.security.MessageDigest;

public class LoginActivity extends BaseActivity implements
        CatalogueFragment.OnFragmentInteractionListener {
    EditText companycode, password;
    Switch switchCtrl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_login);
        final SharedPreferences settings = getSharedPreferences("switchkey", 0);

        companycode = findViewById(R.id.companyCode);
        password = findViewById(R.id.etPassword);

        switchCtrl = (Switch) findViewById(R.id.language);
        switchCtrl.setChecked(settings.getBoolean("switchkey", false));
        switchCtrl.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyContextWrapper.saveSelectLanguage(context, isChecked);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.apply();
                reStart(context);
                overridePendingTransition(0, 0);
            }
        });
    }

    public void login(View view){
        //validate credentials to login
        String code = companycode.getText().toString();
        String pwd = password.getText().toString();
        pwd = getSha256(pwd);
        String type = "login";
        BackgroundLogin bl = new BackgroundLogin(this);
        bl.execute(type,code,pwd);
    }

    public static String getSha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(value.getBytes());
            return bytesToHex(md.digest());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}