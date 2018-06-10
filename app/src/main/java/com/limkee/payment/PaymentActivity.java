package com.limkee.payment;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.entity.Product;
import com.limkee.order.ConfirmOrderFragment;
import com.limkee.order.QuickReorderFragment;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class PaymentActivity extends AppCompatActivity implements  ConfirmOrderFragment.OnFragmentInteractionListener,
        CatalogueFragment.OnFragmentInteractionListener, QuickReorderFragment.OnFragmentInteractionListener, PaymentFragment.OnFragmentInteractionListener{

    private View rootView;
    private QuickReorderFragment quickOrderFragment = new QuickReorderFragment();
    private PaymentFragment paymentFragment = new PaymentFragment();
    private double subtotal;
    private double taxAmt;
    private double totalPayable;
    public static Bundle myBundle = new Bundle();
    private ArrayList<Product> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        Card card = mCardInputWidget.getCard();
        card.setName("Customer Name");
        if (card == null) {
            //mErrorDialogHandler.showError("Invalid Card Data");
        }
        else {
            Stripe stripe = new Stripe(getApplicationContext(), "pk_test_FPldW3NRDq68iu1drr2o7Anb");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            //Toast.makeText(getContext(),
                                    //error.getLocalizedString(getContext()),
                                    //Toast.LENGTH_LONG
                            //).show();
                        }
                    }
            );
        }
    }

    public View onCreate(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

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