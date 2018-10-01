package com.limkee.navigation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.design.widget.NavigationView;

import com.google.gson.Gson;
import com.limkee.BaseActivity;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.R;
import com.limkee.catalogue.ProductDetailsFragment;
import com.limkee.dashboard.DashboardFragment;
import com.limkee.dashboard.TopPurchasedFragment;
import com.limkee.dashboard.TotalSalesFragment;
import com.limkee.order.CancelledOrderFragment;
import com.limkee.order.MainOrderHistoryFragment;
import com.limkee.order.QuickReorderFragment;
import com.limkee.entity.Customer;
import com.limkee.login.LoginActivity;
import com.limkee.login.LogoutActivity;
import com.limkee.order.CurrentOrderFragment;
import com.limkee.order.OrderHistoryFragment;
import com.limkee.payment.AsyncResponse;
import com.limkee.payment.PaymentFragment;
import com.limkee.payment.ScanFragment;
import com.limkee.userProfile.UserProfileFragment;

public class NavigationActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, CatalogueFragment.OnFragmentInteractionListener,
        UserProfileFragment.OnFragmentInteractionListener, QuickReorderFragment.OnFragmentInteractionListener,
        OrderHistoryFragment.OnFragmentInteractionListener, CurrentOrderFragment.OnFragmentInteractionListener,
        PaymentFragment.OnFragmentInteractionListener, CancelledOrderFragment.OnFragmentInteractionListener,
        ProductDetailsFragment.OnFragmentInteractionListener, MainOrderHistoryFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener, TotalSalesFragment.OnFragmentInteractionListener,
        TopPurchasedFragment.OnFragmentInteractionListener, ScanFragment.OnFragmentInteractionListener{

    private Customer customer;
    private Bundle bundle;
    private String isEnglish;
    private String deliveryShift;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private String username;
    private String password;
    private String cutofftime;
    private AlertDialog.Builder builder;
    private CatalogueFragment cf = new CatalogueFragment();

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //check if user is login
        loginPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        boolean isLogin = loginPreferences.getBoolean("isLogin",false);
        isEnglish = loginPreferences.getString("language","");
        deliveryShift = loginPreferences.getString("deliveryShift","");

        username = loginPreferences.getString("username","");
        password = loginPreferences.getString("password","");
        builder= new AlertDialog.Builder(this);
        BackgroundCutoffTime ct = (BackgroundCutoffTime) new BackgroundCutoffTime(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output != null && !output.equals("login unsuccess")){
                    String[] array = output.split(",");
                    cutofftime = array[0];
                    System.out.println("newly get cutoffNavi " + cutofftime);
                    if(isLogin) {
                        Gson gson = new Gson();
                        String json = loginPreferences.getString("customer", "");
                        customer = gson.fromJson(json, Customer.class);
                        bundle = new Bundle();
                        bundle.putParcelable("customer", customer);
                        bundle.putString("language", isEnglish);
                        bundle.putString("deliveryShift", deliveryShift);
                        bundle.putString("cutofftime", cutofftime);
                        loadFragment(CatalogueFragment.class);
                    } else {
                        Intent it = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(it);
                    }
                }else{
                    builder.setMessage("Fail to get cutoff time");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog ad = builder.create();
                    ad.show();
                }
            }
        }
        ).execute(username,password);
    }

    @Override
    public void onBackPressed() {
        //fragment back button
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else{
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Class fragmentClass = null;
        if (id == R.id.nav_catalogue) {
            fragmentClass = CatalogueFragment.class;
            loadFragment(fragmentClass);
        } else if (id == R.id.nav_quickreorder) {
            fragmentClass = QuickReorderFragment.class;
            loadFragment(fragmentClass);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this,LogoutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_userprofile) {
            fragmentClass = UserProfileFragment.class;
            loadFragment(fragmentClass);
        } else if (id == R.id.nav_orderhistory) {
             fragmentClass = MainOrderHistoryFragment.class;
             loadFragment(fragmentClass);
        } else if (id == R.id.nav_currentorder) {
            fragmentClass = CurrentOrderFragment.class;
            loadFragment(fragmentClass);
        } else if (id == R.id.nav_dashboard) {
            fragmentClass = DashboardFragment.class;
            loadFragment(fragmentClass);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragment.setArguments(bundle);
        //Change screen to option selected (using fragments)
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment);
        fragmentTransaction.commit();
    }

    public void setActionBarTitle(String title){
        TextView titleTextView = findViewById(R.id.toolbar_title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

}
