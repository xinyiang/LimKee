package com.limkee1.order;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.limkee1.BaseActivity;
import com.limkee1.R;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;

import java.util.ArrayList;

public class QuickReorderConfirmOrderActivity extends BaseActivity implements QuickReorderConfirmOrderFragment.OnFragmentInteractionListener ,QuickReorderFragment.OnFragmentInteractionListener{

    public static Bundle myBundle = new Bundle();
    private ArrayList<Product> orderList;
    private Customer customer;
    private String isEnglish;
    private String cutofftime;
    private AlertDialog ad;
    boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_reorder_confirm_order);
        Toolbar toolbar = findViewById(com.limkee1.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setTitle("Confirm Order");

        myBundle = getIntent().getExtras();
        customer = myBundle.getParcelable("customer");
        orderList  = myBundle.getParcelableArrayList("orderList");
        isEnglish = myBundle.getString("language");
        cutofftime = myBundle.getString("cutoffTime");

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("orderList", orderList);
        bundle.putParcelable("customer",customer);
        bundle.putString("language", isEnglish);
        bundle.putString("cutoffTime", cutofftime);

        QuickReorderConfirmOrderFragment confirmOrderFragment = new QuickReorderConfirmOrderFragment();
        confirmOrderFragment.setArguments(bundle);
        loadFragment(confirmOrderFragment);
    }

    private void loadFragment(Fragment fragment) {

        //Change screen to option selected (using fragments)
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(com.limkee1.R.id.flContent, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Back button clicked
            //   this.finish();

            //show alert of order loss
            if (isEnglish.equals("Yes")) {
                ad = new AlertDialog.Builder(this)
                        .setMessage("All your orders will be lost. Do you want to proceed?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                QuickReorderConfirmOrderActivity.this.finish();
                                result = false;
                            }
                        })
                        .show();
            } else {
                ad = new AlertDialog.Builder(this)
                        .setMessage("你的所有订单都将取消。您要继续吗？")
                        .setPositiveButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        })
                        .setNegativeButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                QuickReorderConfirmOrderActivity.this.finish();
                                result = false;
                            }
                        })
                        .show();
            }
        }

        return result;
        //  return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setActionBarTitle(String title){
        TextView titleTextView = findViewById(com.limkee1.R.id.toolbar_title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {

            //show alert of order loss
            if (isEnglish.equals("Yes")) {
                new AlertDialog.Builder(this)
                        .setMessage("All your orders will be lost. Do you want to proceed?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                QuickReorderConfirmOrderActivity.super.onBackPressed();
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("你的所有订单都将取消。您要继续吗？")
                        .setPositiveButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        })
                        .setNegativeButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                QuickReorderConfirmOrderActivity.super.onBackPressed();
                            }
                        })
                        .show();
            }

        } else {
            getFragmentManager().popBackStack();
        }
    }
}