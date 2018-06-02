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


public class LoginActivity extends BaseActivity implements
        CatalogueFragment.OnFragmentInteractionListener {
    public static Bundle myBundle = new Bundle();
    EditText companycode, password;
    Switch switchCtrl;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_login);
        final SharedPreferences switchStatus = getSharedPreferences("switchkey", 0);
        companycode = findViewById(R.id.companyCode);
        password = findViewById(R.id.etPassword);

        switchCtrl = (Switch) findViewById(R.id.language);
        switchCtrl.setChecked(switchStatus.getBoolean("switchkey", false));
        switchCtrl.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lang = "cn";
                }
                else{
                    lang = "en";
                }
                SharedPreferences.Editor editor = switchStatus.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.commit();
                reStart(context);
                overridePendingTransition(0, 0);
            }
        });

    }


    public void login(View view){
        //validate credentials to login
        String code = companycode.getText().toString();
        String pwd = password.getText().toString();

        String type = "login";
        BackgroundLogin bl = new BackgroundLogin(this);
        bl.execute(type,code,pwd);
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