package com.limkee1.catalogue;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.entity.Product;

public class ProductDetailsActivity extends AppCompatActivity implements CatalogueFragment.OnFragmentInteractionListener, QuickReorderFragment.OnFragmentInteractionListener, ProductDetailsFragment.OnFragmentInteractionListener {

    public static Bundle myBundle = new Bundle();
    private Product product;
    private String isEnglish;
    private ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(com.limkee1.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBundle = getIntent().getExtras();
        product = myBundle.getParcelable("product");
        isEnglish = myBundle.getString("language");

        Bundle bundle = new Bundle();
        bundle.putParcelable("product",product);
        bundle.putString("language", isEnglish);
        productDetailsFragment.setArguments(bundle);
        loadFragment(productDetailsFragment);

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
        TextView titleTextView = findViewById(com.limkee1.R.id.toolbar_title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
