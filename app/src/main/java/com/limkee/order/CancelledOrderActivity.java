package com.limkee.order;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import com.limkee.R;
import com.limkee.entity.Customer;

public class CancelledOrderActivity extends AppCompatActivity implements CancelledOrderFragment.OnFragmentInteractionListener, OrderHistoryFragment.OnFragmentInteractionListener {

    public static Bundle myBundle = new Bundle();
    private Customer customer;
    private String isEnglish;
    private CancelledOrderFragment cancelledOrderFragment = new CancelledOrderFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_order);

        Toolbar toolbar = findViewById(com.limkee.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBundle = getIntent().getExtras();
        customer = myBundle.getParcelable("customer");
        isEnglish = myBundle.getString("language");

        Bundle bundle = new Bundle();
        bundle.putParcelable("customer",customer);
        bundle.putString("language", isEnglish);
        cancelledOrderFragment.setArguments(bundle);
        loadFragment(cancelledOrderFragment);

    }

    private void loadFragment(Fragment fragment) {

        //Change screen to option selected (using fragments)
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(com.limkee.R.id.flContent, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //
        //  HANDLE BACK BUTTON
        //
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
