package com.limkee.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.net.Uri;
import android.widget.Switch;
import android.widget.TextView;
import com.limkee.BaseActivity;
import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.locale.MyContextWrapper;
import com.limkee.navigation.NavigationActivity;
import com.limkee.payment.PaymentActivity;

import java.security.MessageDigest;

public class LoginActivity extends BaseActivity implements
        CatalogueFragment.OnFragmentInteractionListener {
    EditText companycode, password;
    Switch switchCtrl;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    static String isEnglish = "No";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_login);
        companycode = findViewById(R.id.companyCode);
        password = findViewById(R.id.etPassword);
        final SharedPreferences settings = getSharedPreferences("switchkey", 0);

        saveLoginCheckBox = findViewById(R.id.saveLoginCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            //companycode.setText(loginPreferences.getString("username", ""));
            //password.setText(loginPreferences.getString("password", ""));
            //saveLoginCheckBox.setChecked(true);
            Intent it = new Intent(this, NavigationActivity.class);
            context.startActivity(it);
        }

        switchCtrl = (Switch) findViewById(R.id.language);
        switchCtrl.setChecked(settings.getBoolean("switchkey", false));
        switchCtrl.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyContextWrapper.saveSelectLanguage(context, isChecked);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.apply();
                if(isChecked){
                    isEnglish = "Yes";
                } else {
                    isEnglish = "No";
                }
                reStart(context);
                overridePendingTransition(0, 0);
            }
        });
    }

    public void login(View view){
        String code = companycode.getText().toString();
        String pwd = password.getText().toString();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(companycode.getWindowToken(), 0);

        if (saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            System.out.println(loginPreferences.getBoolean("saveLogin", false)+"wow");
            loginPrefsEditor.putString("username", code);
            loginPrefsEditor.putString("password", pwd);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }

        //validate fields are not blank
        if (code.equals("") || pwd.equals("")){
            TextView pwdValidate = findViewById(R.id.pwdvalidation);
            if (isEnglish.equals("Yes")){
                pwdValidate.setText("Please fill in Company Code and/or Password");
            } else {
                pwdValidate.setText(" 请输入公司代码和/或密码");
            }

        } else {
            //validate credentials to login
            pwd = getSha256(pwd);
            String type = "login";
            BackgroundLogin bl = new BackgroundLogin(this);
            bl.execute(type, code, pwd, isEnglish);
        }
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
        intent.putExtra("language", isEnglish);
        context.startActivity(intent);

    }
}