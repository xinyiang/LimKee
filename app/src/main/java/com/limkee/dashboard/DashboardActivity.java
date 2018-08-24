package com.limkee.dashboard;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.limkee.BaseActivity;
import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.catalogue.ProductDetailsFragment;
import com.limkee.entity.Product;
import com.limkee.order.QuickReorderFragment;

public class DashboardActivity extends BaseActivity implements DashboardFragment.OnFragmentInteractionListener {

    public static Bundle myBundle = new Bundle();
    private Product product;
    private String isEnglish;
    private DashboardFragment dashboardFragment = new DashboardFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(com.limkee.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBundle = getIntent().getExtras();
        product = myBundle.getParcelable("product");
        isEnglish = myBundle.getString("language");

        Bundle bundle = new Bundle();
        bundle.putParcelable("product",product);
        bundle.putString("language", isEnglish);
        dashboardFragment.setArguments(bundle);
        loadFragment(dashboardFragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(com.limkee.R.id.flContent, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setActionBarTitle(String title){
        TextView titleTextView = findViewById(com.limkee.R.id.toolbar_title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    public void onFragmentInteraction(Uri uri) {

    }
}

