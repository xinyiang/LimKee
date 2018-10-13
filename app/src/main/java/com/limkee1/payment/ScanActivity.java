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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

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
    private Button payButton;
    private CheckBox saveCard;
    private Customer customer;
    private String paperBagRequired;
    private ArrayList<Product> orderList;
    private ScanActivity.RadioOnClick radioOnClick = new RadioOnClick(0);
    public static Activity activity;

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
        paperBagRequired = myBundle.getString("paperBagRequired");

        Double tp = Double.parseDouble(totalPayable);

        Toast.makeText(getBaseContext(),""+tp, Toast.LENGTH_SHORT).show();

        TextView tv = (TextView)findViewById(R.id.totalPayable);
        tv.setText(String.format("$%.2f", tp));

        totalPayable = String.valueOf((int) Math.round(tp * 100));
        Bundle bundle = new Bundle();

        payButton = (Button) findViewById(R.id.btnPlaceOrder);
        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pay("pay_with_new_card", null);
            }
        });
        bundle.putParcelable("customer", customer);
        bundle.putString("language", isEnglish);
        bundle.putParcelableArrayList("orderList", orderList);
        bundle.putString("paperBagRequired", paperBagRequired);
        bundle.putString("deliveryDate", deliveryDate);
        bundle.putDouble("totalPayable", tp);
        bundle.putString("cardNumber", cardnum);

        scanFragment.setArguments(bundle);
        loadFragment(scanFragment);
    }

    public void pay(String mType, String lastFourDigit){
        final String type = mType;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (type.equals("pay_with_saved_card")){

        }else{
            nameOnCard = (EditText) findViewById(R.id.nameOnCard);
            err = (TextInputLayout) findViewById(R.id.nameOnCard_inputLayout);
            cardNumber = (EditText) findViewById(R.id.cardNumber);
            expDate = (EditText) findViewById(R.id.expDate);
            Integer expMth = Integer.parseInt(expDate.getText().toString().substring(0,2));
            Integer expYr = Integer.parseInt(expDate.getText().toString().substring(2));
            //expDate.addTextChangedListener(new DateInputMask());

            cvc = (EditText) findViewById(R.id.cvc);

            Date date;

            saveCard = (CheckBox) findViewById(R.id.saveCard);

            Card card = new Card(
                    cardNumber.getText().toString(),
                    expMth,
                    expYr,
                    cvc.getText().toString()
            );

            if (card == null || nameOnCard.getText().toString().isEmpty()) {
                Toast.makeText(context,
                        getResources().getString(R.string.invalid_card),
                        Toast.LENGTH_LONG
                ).show();
                if (nameOnCard.getText().toString().isEmpty()){
                    err.setError("Your name on card is invalid");
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
                                bp.execute(type, totalPayable, token.getId(), isEnglish, paperBagRequired);
                            }
                            public void onError(Exception error) {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                );
            }
        }
    }

    public void selectSavedCard() {
        //get saved card
        GetJson getSavedCards = (GetJson) new GetJson(
                new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if (output == null){
                            Toast.makeText(context, getResources().getString(R.string.no_saved_card), Toast.LENGTH_SHORT).show();
                        }else{
                            try{
                                loadIntoAlertDialog(output);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).execute(customer.getDebtorCode());

    }

    private void loadIntoAlertDialog(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] cards = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject obj = jsonArray.getJSONObject(i);
            cards[i] = "XXXX XXXX XXXX " + obj.getString("LastFourDigit");
        }

        AlertDialog ad = new AlertDialog.Builder(ScanActivity.this).setTitle(getResources().getString(R.string.select_card))
                .setSingleChoiceItems(cards,radioOnClick.getIndex(),radioOnClick).create();
        ad.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.confirm), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                ListView lw = ((AlertDialog)dialog).getListView();
                Object selectedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                String selectedCard = selectedItem.toString();
                pay("pay_with_saved_card", selectedCard.substring(selectedCard.length() - 4));
            }
        });
        ad.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getString(R.string.delete_card), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                ListView lw = ((AlertDialog)dialog).getListView();
                Object selectedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                String selectedCard = selectedItem.toString();
                deleteCard(dialog, selectedCard.substring(selectedCard.length() - 4));
            }
        });
        ad.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
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

    protected void deleteCard(DialogInterface singleChoiceDialog, String lastFourDigit){
        AlertDialog ad = new AlertDialog.Builder(ScanActivity.this).setMessage(getResources().getString(R.string.confirm_delete) + lastFourDigit).create();
        ad.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.confirm), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int i){
                BackgroundDelete bd = (BackgroundDelete) new BackgroundDelete(
                        new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                selectSavedCard();
                            }
                        }
                ).execute(customer.getDebtorCode(), lastFourDigit);
            }
        });
        ad.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int i){
                dialog.cancel();
            }
        });
        ad.show();
    }

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
