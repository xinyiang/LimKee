package com.limkee.navigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.limkee.BaseActivity;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.R;
import com.limkee.dao.CatalogueDAO;
import com.limkee.dao.OrderDAO;
import com.limkee.dao.OrderDetailDAO;
import com.limkee.dao.OrderHistoryDAO;
import com.limkee.dao.OrderQuantityDAO;
import com.limkee.entity.Customer;
import com.limkee.login.LoginActivity;
import com.limkee.login.LogoutActivity;
import com.limkee.order.CurrentOrderFragment;
import com.limkee.order.OrderHistoryFragment;
import com.limkee.order.QuickReorderFragment;
import com.limkee.userProfile.UserProfileFragment;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


public class NavigationActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, CatalogueFragment.OnFragmentInteractionListener,
        UserProfileFragment.OnFragmentInteractionListener, QuickReorderFragment.OnFragmentInteractionListener,
        OrderHistoryFragment.OnFragmentInteractionListener, CurrentOrderFragment.OnFragmentInteractionListener{

    CompositeDisposable compositeDisposable;
    Customer customer;
    Bundle bundle;
    String isEnglish;

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

        //Create temporary Food Products
        CatalogueDAO.create("001","Curry Chicken Pau","咖喱鸡肉包",2.50, "http://www.limkee.com/images/charsiewpau.jpg", 5, 5 );
        CatalogueDAO.create("002","Big Pau", "大肉包", 1, "http://www.limkee.com/images/bigpau.jpg", 5, 5);
        CatalogueDAO.create("003","Chicken Pau", "鸡肉包", 1, "http://www.limkee.com/images/chickenpau.jpg", 5, 5);
        CatalogueDAO.create("004","Char Siew Pau","叉烧包",2.50, "http://www.limkee.com/images/charsiewpau.jpg", 5, 5);
        CatalogueDAO.create("005", "Tau Sar Pau", "豆沙包", 0.5, "http://www.limkee.com/images/tarsarpau.jpg", 5, 5);
        CatalogueDAO.create("006","Lian Yong Pau", "莲蓉包", 0.60, "http://www.limkee.com/images/charsiewpau.jpg", 5, 5);
        CatalogueDAO.create("007","Coffee Pau", "咖啡包", 1.20, "http://www.limkee.com/images/coffeepau.jpg", 5, 5);
        CatalogueDAO.create("008","Vegetable Pau", "香菇菜包", 1, "http://www.limkee.com/images/vegetablepau.jpg", 5, 5);
        CatalogueDAO.create("009","Pumpkin Pau", "金瓜包", 1, "http://www.limkee.com/images/pumpkinpau.jpg", 5, 5);
        CatalogueDAO.create("010","Siew Mai", "烧卖", 1, "http://www.limkee.com/images/sm.jpg", 0, 10);
        CatalogueDAO.create("012","Loh Mai Kai", "糯米鸡饭", 1, "http://www.limkee.com/images/lmk.jpg", 0, 1);
        CatalogueDAO.create("013","Fan Choy", "叉烧饭菜", 1, "http://www.limkee.com/images/fc.jpg", 0, 1);

        //create temporary sales order details
        OrderDetailDAO.create("1", "2018-06-22 10:15:30", 125, "Pending Delivery", "");
        OrderDetailDAO.create("2", "2018-06-25 11:00:00", 34.80, "Pending Delivery", "");
        OrderDetailDAO.create("3", "2018-06-24 10:00:00", 54.80, "Delivered", "");

        //create temporary sales order quantity
        OrderQuantityDAO.create("1", "1", 100,0,  0.65);
        OrderQuantityDAO.create("1", "7", 150,0,  0.40);
        OrderQuantityDAO.create("2", "2", 30,0,  0.72);
        OrderQuantityDAO.create("2", "5", 40,0,  0.33);
        OrderQuantityDAO.create("3", "5", 1000,0,  0.33);

        //create temporary sales order
        OrderDAO.create("1","2018-06-22", 2);
        OrderDAO.create("2","2018-06-26", 2);
        OrderHistoryDAO.create("3","2018-06-27", 3);

        //check if user is login
        Intent intent = getIntent();
        boolean isLogin = intent.getExtras().getBoolean("isLogin");
        isEnglish = intent.getExtras().getString("language");

        if(isLogin) {
            ArrayList<Customer> cust = intent.getExtras().getParcelableArrayList("customer");
            customer = cust.get(0);
            bundle = new Bundle();
            bundle.putParcelable("customer", customer);
            bundle.putString("language", isEnglish);

            loadFragment(CatalogueFragment.class);
        } else {
            Intent it = new Intent(this, LoginActivity.class);
            startActivity(it);
        }

        //  Instantiate CompositeDisposable for retrofit
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onBackPressed() {
        //fragment back button
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();

        } else{
            if (isEnglish.equals("Yes")){
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getBaseContext(),LogoutActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("您确定要退出？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getBaseContext(),LogoutActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
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
            fragmentClass = OrderHistoryFragment.class;
            loadFragment(fragmentClass);
        } else if (id == R.id.nav_currentorder) {
            fragmentClass = CurrentOrderFragment.class;
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
