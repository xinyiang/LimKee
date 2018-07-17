package com.limkee.payment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limkee.BaseActivity;
import com.limkee.R;

import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;

public class PaymentActivity extends BaseActivity implements PaymentFragment.OnFragmentInteractionListener{
    private View rootView;
    private String totalPayable;
    private PaymentFragment paymentFragment = new PaymentFragment();
    public static Bundle myBundle = new Bundle();
    private Context context;
    private ProgressBar progressBar;
    private EditText nameOnCard;
    private TextView errorNameOnCard;
    private Button payButton;
    private CheckBox saveCard;
    private CardMultilineWidget mCardMultilineWidget;
    private RadioOnClick radioOnClick = new RadioOnClick(0);
    final String[] cards = {"400000000000", "410000000000"};
    public static Activity activity; //used to finish this activity in backgroundPayment activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(com.limkee.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("@string/payment");
        myBundle = getIntent().getExtras();
        activity = this;
        context = getApplicationContext();
        double tp = myBundle.getDouble("totalPayable");

        TextView tv = (TextView)findViewById(R.id.totalPayable);
        tv.setText(String.format("$%.2f", tp));

        totalPayable = String.valueOf((int) Math.round(tp * 100));
        Bundle bundle = new Bundle();
        //bundle.putString("totalPayable",totalPayable);

        payButton = (Button) findViewById(R.id.btnPlaceOrder);
        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pay("pay_with_new_card", -1);
            }
        });

        paymentFragment.setArguments(bundle);
        loadFragment(paymentFragment);
    }

    public void pay(String mType, int selectedCard){
        //validate credentials to login
        final String type = mType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        nameOnCard = (EditText) findViewById(R.id.nameOnCard);
        errorNameOnCard = (TextView) findViewById(R.id.errNameOnCard);
        mCardMultilineWidget = (CardMultilineWidget)findViewById(R.id.card_multiline_widget);
        saveCard = (CheckBox) findViewById(R.id.saveCard);
        Card card = mCardMultilineWidget.getCard();
        //card.setName("Customer Name");

        if (card == null || nameOnCard.getText().toString().isEmpty()) {
            Toast.makeText(context,
                    "Invalid Card Data",
                    Toast.LENGTH_LONG
            ).show();
            if (nameOnCard.getText().toString().isEmpty()){
                errorNameOnCard.setVisibility(View.VISIBLE);
                nameOnCard.setBackgroundResource(R.drawable.text_underline);
            }
            else {
                errorNameOnCard.setVisibility(View.INVISIBLE);
                nameOnCard.setBackgroundResource(android.R.drawable.edit_text);
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Stripe stripe = new Stripe(context, "pk_test_FPldW3NRDq68iu1drr2o7Anb");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            if (saveCard.isChecked()){
                                //Send card details to db
                            }
                            BackgroundPayment bp = new BackgroundPayment(context, activity);
                            bp.execute(type, totalPayable, token.getId());
                            // Send token to your server
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
//        new CountDownTimer(6000, 100) {
//
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            public void onFinish() {
//                progressBar.setVisibility(View.GONE);
//            }
//        }.start();
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

    public void selectSavedCard(View view) {
        AlertDialog ad = new AlertDialog.Builder(this).setTitle("选择您要使用的卡")
                .setSingleChoiceItems(cards,radioOnClick.getIndex(),radioOnClick)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                int selectedCard = radioOnClick.getIndex();
                                pay("pay_with_saved_card", selectedCard);
                                //Toast.makeText(PaymentActivity.this, "您已经选择了： " + selectedCard + ":" + cards[selectedCard], Toast.LENGTH_LONG).show();
                                //dialog.dismiss();
                            }
                        }
                ).setNegativeButton("取消", null).create();
        //cardsRadioListView = ad.getListView();
        ad.show();
    }

    class RadioOnClick implements DialogInterface.OnClickListener{
        private int index;

        public RadioOnClick (int index) {
            this.index = index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
        public void onClick(DialogInterface dialog, int whichButton){
            setIndex(whichButton);
        }
    }
}