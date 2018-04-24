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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.design.widget.NavigationView;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.R;
import com.limkee.dao.CatalogueDAO;
import com.limkee.login.LoginActivity;
import com.limkee.login.LogoutActivity;
import com.limkee.order.QuickReorderFragment;
import io.reactivex.disposables.CompositeDisposable;


public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, CatalogueFragment.OnFragmentInteractionListener,
        QuickReorderFragment.OnFragmentInteractionListener{

    public static Bundle myBundle = new Bundle();
    CompositeDisposable compositeDisposable;
    boolean logined = true;
    String username;

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
        CatalogueDAO.create(1,"Char Siew Pau 叉烧包",
                "http://www.limkee.com/images/charsiewpau.jpg",
                2.50);
        CatalogueDAO.create(2,"Big Pau 大肉包",
                "http://www.limkee.com/images/bigpau.jpg",
                1);

        //check if user is login
        Intent intent = getIntent();
        boolean isLogin = intent.getExtras().getBoolean("isLogin");

        if(isLogin) {
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
        }
        else{

            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavigationActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
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
          //  ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
          //  constraintLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_quickreorder) {
            fragmentClass = QuickReorderFragment.class;
           // ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
           // constraintLayout.setVisibility(View.GONE);
        } else if (id == R.id.nav_logout) {
             Intent intent = new Intent(this,LogoutActivity.class);
             startActivity(intent);
         }

        loadFragment(fragmentClass);

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

    private void handleResponse(Object object) {}

    private void handleError(Throwable error) {}

}
