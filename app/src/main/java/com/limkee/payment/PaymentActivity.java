package com.limkee.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.order.ConfirmOrderFragment;
import com.limkee.order.QuickReorderFragment;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;

public class PaymentActivity extends AppCompatActivity implements PaymentFragment.OnFragmentInteractionListener{
    private View rootView;
    private String totalPayable;
    private PaymentFragment paymentFragment = new PaymentFragment();
    public static Bundle myBundle = new Bundle();
    private Context context;
    private CardInputWidget mCardInputWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(com.limkee.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("@string/payment");
        myBundle = getIntent().getExtras();
        context = getApplicationContext();
        totalPayable = String.valueOf(myBundle.getDouble("totalPayable"));
        Bundle bundle = new Bundle();
        bundle.putString("totalPayable",totalPayable);
        paymentFragment.setArguments(bundle);
        loadFragment(paymentFragment);
    }

    public void pay(View view){
        //validate credentials to login
        final String type = "pay";
        mCardInputWidget = (CardInputWidget)findViewById(R.id.card_input_widget);
        Card card = mCardInputWidget.getCard();
        //card.setName("Customer Name");

        if (card == null) {
            Toast.makeText(context,
                    "Invalid Card Data",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            Stripe stripe = new Stripe(context, "pk_test_FPldW3NRDq68iu1drr2o7Anb");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            BackgroundPayment bp = new BackgroundPayment(context);
                            bp.execute(type,token.getId(),totalPayable);
                            // Send token to your server
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
    }

    public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment, container, false);
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