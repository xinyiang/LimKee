package com.limkee.payment;

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
import com.limkee.dashboard.DashboardFragment;
import com.limkee.entity.Product;

public class ScanActivity extends BaseActivity implements ScanFragment.OnFragmentInteractionListener {
    public static Bundle myBundle = new Bundle();
    private String isEnglish;
    private ScanFragment scanFragment = new ScanFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Toolbar toolbar = findViewById(com.limkee.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBundle = getIntent().getExtras();
        isEnglish = myBundle.getString("language");

        Bundle bundle = new Bundle();
        bundle.putString("language", isEnglish);
        scanFragment.setArguments(bundle);
        loadFragment(scanFragment);
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
            // Back button clicked
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
