package com.limkee.order;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.limkee.R;
import com.limkee.catalogue.*;
import com.limkee.entity.Customer;
import com.limkee.entity.Product;
import java.util.ArrayList;


public class ConfirmOrderActivity extends AppCompatActivity implements ConfirmOrderFragment.OnFragmentInteractionListener,
        CatalogueFragment.OnFragmentInteractionListener, CurrentOrderFragment.OnFragmentInteractionListener, QuickReorderFragment.OnFragmentInteractionListener{

    private View rootView;
    private ConfirmOrderFragment confirmOrderFragment = new ConfirmOrderFragment();
    public static Bundle myBundle = new Bundle();
    private ArrayList<Product> orderList;
    private Customer customer;
    private String isEnglish;
    private String deliveryShift;
    private  String cutofftime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        Toolbar toolbar = findViewById(com.limkee.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Confirm Order");

        myBundle = getIntent().getExtras();
        customer = myBundle.getParcelable("customer");
        orderList  = myBundle.getParcelableArrayList("orderList");
        isEnglish = myBundle.getString("language");
        deliveryShift = myBundle.getString("deliveryShift");
        cutofftime = myBundle.getString("cutoffTime");

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("orderList", orderList);
        bundle.putParcelable("customer",customer);
        bundle.putString("language", isEnglish);
        bundle.putString("deliveryShift", deliveryShift);
        bundle.putString("cutoffTime", cutofftime);

        confirmOrderFragment.setArguments(bundle);
        loadFragment(confirmOrderFragment);

    }

    public View onCreate(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_confirm_order, container, false);

        return rootView;
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