package com.limkee.login;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.navigation.NavigationActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import static java.security.AccessController.getContext;


public class LoginActivity extends AppCompatActivity implements
        CatalogueFragment.OnFragmentInteractionListener {
    public static Bundle myBundle = new Bundle();
    EditText companycode, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        companycode = (EditText) findViewById(R.id.companyCode);
        password = (EditText) findViewById(R.id.password);
    }

    public void login(View view) {
        //validate credentials to login
        String code = companycode.getText().toString();
        String pwd = password.getText().toString();


        String type = "login";
        BackgroundLogin bl = new BackgroundLogin(this);
        bl.execute(type,code,pwd);

        //redirect if success login
        Intent it = new Intent(this, NavigationActivity.class);
        it.putExtra("isLogin", true);
        startActivity(it);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
