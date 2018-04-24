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


public class LoginActivity extends AppCompatActivity implements
         CatalogueFragment.OnFragmentInteractionListener {
    public static Bundle myBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void login(View view) {

       EditText companyCode = (EditText) findViewById(R.id.companyCode);
       EditText password = (EditText) findViewById(R.id.password);

       //validate credentials to login
       String code = companyCode.getText().toString();
       String pw = password.getText().toString();

        //if valid
        //show delivery cut off time

        //redirect
        Intent it = new Intent(this, NavigationActivity.class);
        it.putExtra("isLogin", true);
        startActivity(it);
    }
}
