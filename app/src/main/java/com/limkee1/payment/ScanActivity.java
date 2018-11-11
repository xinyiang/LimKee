package com.limkee1.payment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.limkee1.BaseActivity;
import com.limkee1.R;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class ScanActivity extends BaseActivity implements ScanFragment.OnFragmentInteractionListener {
    public static Bundle myBundle = new Bundle();
    private String isEnglish;
    private String cardnum;
    private ScanFragment scanFragment = new ScanFragment();
    private View rootView;
    private String totalPayable;
    private String deliveryDate;
    private Context context;
    private ProgressBar progressBar;
    private EditText nameOnCard;
    private EditText cardNumber;
    private EditText expDate;
    private EditText cvc;
    private TextInputLayout err;
    private TextInputLayout err2;
    private TextInputLayout err3;
    private Button payButton;
    private CheckBox saveCard;
    private Customer customer;
    private int paperBagNeeded;
    private ArrayList<Product> orderList;
    private ScanActivity.RadioOnClick radioOnClick = new RadioOnClick(0);
    public static Activity activity;
    private Integer expMth = 0 ;
    private Integer expYr = 0;
    double walletDeduction;
    double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Toolbar toolbar = findViewById(com.limkee1.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBundle = getIntent().getExtras();
        isEnglish = myBundle.getString("language");
        cardnum = myBundle.getString("cardnum");
        activity = this;
        context = getApplicationContext();
        customer = myBundle.getParcelable("customer");
        deliveryDate = myBundle.getString("deliveryDate");
        orderList = myBundle.getParcelableArrayList("orderList");
        totalPayable = myBundle.getString("totalPayable");
        paperBagNeeded = myBundle.getInt("paperBagNeeded");
        Double tp = Double.parseDouble(totalPayable);
        walletDeduction = myBundle.getDouble("walletDeduction");
        totalAmount = myBundle.getDouble("totalAmount");

        if (walletDeduction != 0) {
            TextView tv_totalAmt = (TextView) findViewById(R.id.tv_totalAmt);
            tv_totalAmt.setVisibility(View.VISIBLE);

            TextView tv_walletDeduction = (TextView) findViewById(R.id.tv_walletDeduction);
            tv_walletDeduction.setVisibility(View.VISIBLE);

            if (isEnglish.equals("Yes")){
                tv_totalAmt.setText("Total amount is " + String.format("$%.2f", totalAmount));
                tv_walletDeduction.setText("Wallet Deduction of " + String.format("$%.2f", walletDeduction));
            } else {
                tv_totalAmt.setText("总额是 " + String.format("$%.2f", totalAmount));
                tv_walletDeduction.setText("钱包扣除 " + String.format("$%.2f", walletDeduction));
            }
        }

        TextView tv_totalPayable = (TextView)findViewById(R.id.totalPayable);
        tv_totalPayable.setText(String.format("$%.2f", tp));

        totalPayable = String.valueOf((int) Math.round(tp * 100));
        Bundle bundle = new Bundle();

        payButton = (Button) findViewById(R.id.btnPlaceOrder);
        if (isEnglish.equals("Yes")){
            payButton.setText("Pay " + String.format("$%.2f", tp));
        } else {
            payButton.setText("付款 " + String.format("$%.2f", tp));
        }
        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pay("pay_with_new_card", null);
            }
        });

        bundle.putParcelable("customer", customer);
        bundle.putString("language", isEnglish);
        bundle.putParcelableArrayList("orderList", orderList);
        bundle.putInt("paperBagNeeded", paperBagNeeded);
        bundle.putString("deliveryDate", deliveryDate);
        bundle.putDouble("totalPayable", tp);
        bundle.putDouble("walletDeduction", walletDeduction);
        bundle.putDouble("totalAmount", totalAmount);
        bundle.putString("cardNumber", cardnum);

        scanFragment.setArguments(bundle);
        loadFragment(scanFragment);
    }

    public void pay(String mType, String lastFourDigit){
        final String type = mType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        nameOnCard = (EditText) findViewById(R.id.nameOnCard);
        err = (TextInputLayout) findViewById(R.id.nameOnCard_inputLayout);
        err2 = (TextInputLayout) findViewById(R.id.expirydate_inputLayout);
        err3 = (TextInputLayout) findViewById(R.id.cvc_inputLayout);

        cardNumber = (EditText) findViewById(R.id.cardNumber);
        expDate = (EditText) findViewById(R.id.expDate);
        if(!expDate.getText().toString().isEmpty()) {
            expMth = Integer.parseInt(expDate.getText().toString().substring(0, 2));
            expYr = Integer.parseInt(expDate.getText().toString().substring(3));
        }
        cvc = (EditText) findViewById(R.id.cvc);
        Date date;
        saveCard = (CheckBox) findViewById(R.id.saveCard);

        Card card = new Card(cardNumber.getText().toString(), expMth, expYr, cvc.getText().toString());

        if (card == null || nameOnCard.getText().toString().isEmpty() || !isValidFutureDate(""+expMth+"/01/20"+expYr) || cvc == null) {
            Toast.makeText(context, getResources().getString(R.string.invalid_card), Toast.LENGTH_LONG).show();
            if (nameOnCard.getText().toString().isEmpty()){
                err.setError("Your name on card is invalid");
            }
            if (expDate.getText().toString().isEmpty() || !isValidFutureDate(""+expMth+"/01/20"+expYr)){
                err2.setError("Expiration date is invalid");
            }
            if (cvc.getText().toString().isEmpty()){
                err3.setError("CVC is invalid");
            }
            nameOnCard.addTextChangedListener(filterTextWatcher);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Stripe stripe = new Stripe(context, "pk_test_FPldW3NRDq68iu1drr2o7Anb");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            BackgroundPayment bp = new BackgroundPayment(context, activity);
                            bp.saveCustomer(customer);
                            bp.saveDeliveryDate(deliveryDate);
                            bp.saveOrderList(orderList);
                            if (saveCard.isChecked()){
                                bp.saveCard(card);
                            }
                            bp.execute(type, totalPayable, token.getId(), isEnglish, Integer.toString(paperBagNeeded), Double.toString(walletDeduction), Double.toString(totalAmount));
                        }
                        public void onError(Exception error) {
                            Toast.makeText(context, "Error processing charge", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
            );
        }
    }

    public static boolean isValidFutureDate(String pDateString) {
        if (!isValidDate(pDateString)) {
            return false;
        }
        StringTokenizer st =  new StringTokenizer(pDateString.trim(), "/");
        if (st.countTokens() != 3) {
            throw new NumberFormatException("Date format should be MM/DD/YYYY.");
        }
        String month = st.nextToken();
        String day = st.nextToken();
        String year = st.nextToken();
        long oneDayInMillis = 86400000;
        GregorianCalendar ref = new GregorianCalendar();
        ref.setTime(new Date(System.currentTimeMillis() - oneDayInMillis));
        ref.set(Calendar.HOUR_OF_DAY, 0);
        ref.set(Calendar.MINUTE, 1);
        ref.set(Calendar.AM_PM, Calendar.AM);
        GregorianCalendar now = toDate(year, month, day);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 2);
        ref.set(Calendar.AM_PM, Calendar.AM);

        return now.after(ref);
    }

    public static boolean isValidDate(String pDateString) {
        StringTokenizer st=  new StringTokenizer(pDateString.trim(), "/");
        if (st.countTokens() != 3) {
            throw new NumberFormatException("Date format should be MM/DD/YYYY.");
        }
        String month = st.nextToken();
        String day = st.nextToken();
        String year = st.nextToken();
        return toDate(year, month, day) != null;
    }

    public static GregorianCalendar toDate(final String year, final String month, final String day) {
        int mm, dd, yyyy;

        try {
            if(year.length() != 4) {
                throw new NumberFormatException("Please provide four(4) digits for the year.");
            }
            yyyy = Integer.parseInt(year);
            if(yyyy == 0) {
                throw new NumberFormatException("zero is an invalid year.");
            }
        }
        catch(NumberFormatException nfe) {
            throw new NumberFormatException(year + " is an invalid year.");
        }

        try {
            mm = Integer.parseInt(month);
            if(mm < 1 || mm > 12) {
                throw new NumberFormatException(month + " is an invalid month.");
            }
        }
        catch(NumberFormatException nfe) {
            throw new NumberFormatException(month + " is an invalid month.");
        }

        try {
            dd = Integer.parseInt(day);
        }
        catch(NumberFormatException nfe) {
            throw new NumberFormatException(day + " is an invalid day.");
        }

        GregorianCalendar gc = new GregorianCalendar( yyyy, --mm, 1 );
        if(dd > gc.getActualMaximum(GregorianCalendar.DATE)) {
            throw new NumberFormatException();
        }
        if(dd < gc.getActualMinimum(GregorianCalendar.DATE)) {
            throw new NumberFormatException();
        }
        return new GregorianCalendar(yyyy,mm,dd);
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

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //nameOnCard.setBackground(originalDrawable);
            err.setError(null);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(com.limkee1.R.id.flContent, fragment);
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
        TextView titleTextView = findViewById(com.limkee1.R.id.toolbar_title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    public void onFragmentInteraction(Uri uri) {
    }
}
