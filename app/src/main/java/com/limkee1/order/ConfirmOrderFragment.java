package com.limkee1.order;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.Utility.DateUtility;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.limkee1.payment.PaymentActivity;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmOrderFragment extends Fragment {

    private ConfirmOrderFragment.OnFragmentInteractionListener mListener;
    private View view;
    private EditText deliveryDate;
    Calendar mCurrentDate;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Button next;
    private TextView subtotalAmt;
    private double subtotal;
    private double taxAmt;
    private double totalAmount;
    private ConfirmOrderAdapter mAdapter;
    private ArrayList<Product> orderList;
    private String isEnglish;
    private Customer customer;
    CompositeDisposable compositeDisposable;
    private String ETADeliveryDate;
    private int day;
    private int month;
    private int year;
    private String cutoffTime;
    private int paperBagNeeded;
    public static Retrofit retrofit;
    private double walletDeduction;

    public ConfirmOrderFragment() {
    }

    public static ConfirmOrderFragment newInstance() {
        ConfirmOrderFragment cf = new ConfirmOrderFragment();
        Bundle args = new Bundle();
        cf.setArguments(args);
        return cf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        orderList = bundle.getParcelableArrayList("orderList");
        customer = bundle.getParcelable("customer");
        isEnglish = bundle.getString("language");
        cutoffTime = bundle.getString("cutoffTime", cutoffTime);

        if (getActivity() instanceof ConfirmOrderActivity) {
            if (isEnglish.equals("Yes")) {
                ((ConfirmOrderActivity) getActivity()).setActionBarTitle("Confirm Order");
            } else {
                ((ConfirmOrderActivity) getActivity()).setActionBarTitle("确认订单");
            }
        }
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView = (RecyclerView) view.findViewById(com.limkee1.R.id.recyclerView);
        mAdapter = new ConfirmOrderAdapter(this, orderList, isEnglish);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        // recyclerView.setNestedScrollingEnabled(false);
        // mAdapter.notifyDataSetChanged();

        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.start();

        subtotal = calculateSubtotal(orderList);
        DecimalFormat df = new DecimalFormat("#0.00");

        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        subtotalAmt.setText("$" + df.format(subtotal));

        TextView tax = view.findViewById(R.id.taxAmt);
        taxAmt = subtotal * 0.07;
        tax.setText("$" + df.format(taxAmt));

        TextView totalAmt = view.findViewById(R.id.totalAmt);
        totalAmount = taxAmt + subtotal;
        totalAmt.setText("$" + df.format(totalAmount));

        if (isEnglish.equals("Yes")) {
            TextView  lbl_item_details;
            lbl_item_details = (TextView) view.findViewById(R.id.lbl_items);
            lbl_item_details.setText(" " + orderList.size() + "items");
        }

        //display delivery details data
        TextView contact, company, productDetails, orderDetails, amountDetails;
        EditText deliveryDate;

        contact = (TextView) view.findViewById(R.id.phone);
        company = (TextView) view.findViewById(R.id.companyName);
        deliveryDate = (EditText) view.findViewById(R.id.date);
        productDetails = (TextView) view.findViewById(R.id.lbl_items);
        orderDetails = (TextView) view.findViewById(R.id.lbl_deliveryDetails);
        amountDetails = (TextView) view.findViewById(R.id.lbl_amountDetails);

        if (customer.getDeliveryContact2() == null || customer.getDeliveryContact2().length() == 0) {
            contact.setText(customer.getDeliveryContact());

        } else {
            contact.setText(customer.getDeliveryContact() + " ," + customer.getDeliveryContact2());
        }

        company.setText(customer.getCompanyName());

        if (isEnglish.equals("Yes")) {
            orderDetails.setText(" Order Details");
            amountDetails.setText(" Amount Details");
            if (orderList.size() == 1) {
                productDetails.setText(" Product Details (" + orderList.size() + " item)");
            } else {
                productDetails.setText(" Product Details (" + orderList.size() + " items)");
            }
        } else {
            productDetails.setText(" 订单样品 (" + orderList.size() + " 样)");
        }

        String deliverDate = "";
        //check if today's delivery is before cut off time and set delivery date field
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date currentTimestamp = new Date();
            String currentDate = "";

            currentDate = sdf.format(currentTimestamp);
            String date = currentDate.substring(0,10);

            String dateTime = date + " " + cutoffTime;
            Date cutoffTimestamp = sdf.parse(dateTime);

            String day = date.substring(8,date.length());
            String month = date.substring(5,7);
            String yr = date.substring(0,4);

            SimpleDateFormat sundayFormat = new SimpleDateFormat("EEEE");
            String dayOfWeek = "";

            //need to check if is new month/new year
            int lastDay = DateUtility.getLastDayOfMonth(month);

            if (currentTimestamp.before(cutoffTimestamp)) {
                //System.out.println("delivery time before cut off");

                //set delivery date to today's date by default
                //need to check if today day is a sunday then +1 again
                Date todayDate = new Date(yr + "/" +  month + "/" + day);
                dayOfWeek = sundayFormat.format(todayDate);

                int todayDay = Integer.parseInt(day);
                if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {
                    todayDay = todayDay+1;

                    if (todayDay>lastDay){
                        todayDay = lastDay;
                    }
                    //System.out.println("line 56 delivery time before cut off time is a sunday " + todayDay);
                    String currentDay = "";
                    if (todayDay < 10){
                        currentDay = "0" + todayDay;
                    } else {
                        currentDay = Integer.toString(todayDay);
                    }

                    if (month.length() == 1){
                        month = "0" + month;
                    }

                    deliverDate = currentDay + "/" + month + "/" + yr;
                } else {
                    if (month.length() == 1){
                        month = "0" + month;
                    }
                    deliverDate = day + "/" + month + "/" + yr;
                }
            } else {
                //System.out.println("delivery time after cut off");
                //set delivery date to tomorrow's date by default if its not a sunday
                int nextDay = Integer.parseInt(day) + 1;

                if (nextDay > lastDay){
                    nextDay = 1;
                }

                //need to check if next day is a sunday then +1 again
                Date nextDayDate = new Date(yr + "/" +  month + "/" + nextDay);
                dayOfWeek = sundayFormat.format(nextDayDate);

                if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {
                    nextDay = nextDay+1;

                    if (nextDay>lastDay){
                        nextDay = 1;
                    }
                }

                if (nextDay == 1 && Integer.parseInt(month) != 12) {
                    //new month
                    int newMth = Integer.parseInt(month) + 1;
                    String newMonth = "";
                    if (newMth < 10){
                        newMonth = "0" + newMth;
                    } else {
                        newMonth = Integer.toString(newMth);
                    }

                    //need to check if 1st day of new month is a sunday eg: 01/09/2019
                    String nextDayStr = "";
                    if (nextDay < 10){
                        nextDayStr = "0" + nextDay;
                    } else {
                        nextDayStr = Integer.toString(nextDay);
                    }

                    Date newYear = new Date(yr + "/" +  newMth + "/" + nextDayStr);
                    dayOfWeek = sundayFormat.format(newYear);

                    if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {
                        deliverDate = "02" + "/" + newMonth + "/" + yr;
                    } else {
                        deliverDate = "01" + "/" + newMonth + "/" + yr;
                    }

                } else if (nextDay == 1 && Integer.parseInt(month) == 12) {
                    //new year
                    int newYr = Integer.parseInt(yr) + 1;
                    int newDay = 1;

                    //need to check new year day is a Sunday eg: 01/01/2023

                    Date newYear = new Date(newYr + "/" +  "01" + "/" + "01");
                    dayOfWeek = sundayFormat.format(newYear);

                    if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {
                        newDay = nextDay+1;
                    }

                    String newYearDay = "0" + newDay;   //either 1st Jan or 2nd Jan

                    deliverDate = newYearDay + "/" + "01" + "/" + newYr;

                } else {
                    //within same month
                    String nextD = "";
                    if (nextDay < 10){
                        nextD = "0" + nextDay;
                    } else {
                        nextD = Integer.toString(nextDay);
                    }

                    if (month.length() == 1){
                        month = "0" + month;
                    }

                    deliverDate = nextD + "/" + month + "/" + yr;
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        SimpleDateFormat expectedPattern = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd/MM/yyyy");

        try {
            Date datetime = expectedPattern.parse(deliverDate);
            String timestamp = formatter.format(datetime);
            deliveryDate.setText(timestamp);

            String day = deliverDate.substring(0,2);
            String month = deliverDate.substring(3,5);
            String yr = deliverDate.substring(6);
            ETADeliveryDate = yr + "-" + month + "-" + day;
            System.out.println("ETA in Confirm Order line in line 339 is " + ETADeliveryDate);

        } catch (Exception e) {
            System.out.println("Error in Confirm Order line in line 342 " + e.getMessage());
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        deliveryDate = (EditText) view.findViewById(R.id.date);

        //choose delivery date
        deliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCurrentDate = Calendar.getInstance();
                year = mCurrentDate.get(Calendar.YEAR);
                month = mCurrentDate.get(Calendar.MONTH);
                day = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {

                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;

                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                        Date date = new Date(selectedYear, selectedMonth, selectedDay - 1);
                        String dayOfWeek = sdf.format(date);

                        if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {
                            //show error msg for no delivery
                            if (isEnglish.equals("Yes")) {
                                final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                        .setMessage("There is no delivery on Sunday. Please choose another date.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                                deliveryDate.setText("DD/MM/YYYY");
                                            }
                                        })
                                        .show();
                                TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                textView.setTextSize(20);
                            } else {
                                final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                        .setMessage("星期日没有送货，请选其他送货日期")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                                deliveryDate.setText("日/月/年");
                                            }
                                        })
                                        .show();
                                TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                textView.setTextSize(20);
                            }
                        } else {

                            Calendar c = Calendar.getInstance();
                            // set the calendar to start of today
                            c.set(Calendar.HOUR_OF_DAY, 0);
                            c.set(Calendar.MINUTE, 0);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);

                            //and get that as a Date
                            Date today = c.getTime();
                            //reuse the calendar to set user specified's delivery date
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, month);
                            c.set(Calendar.DAY_OF_MONTH, day);
                            Date dateSpecified = c.getTime();

                            if (dateSpecified.before(today)) {

                                if (isEnglish.equals("Yes")) {
                                    final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                            .setMessage("Invalid delivery date! Please select another delivery date.")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //finish();
                                                    deliveryDate.setText("DD/MM/YYYY");
                                                }
                                            })
                                            .show();
                                    TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                    textView.setTextSize(20);
                                } else {
                                    final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                            .setMessage("送货日期错误, 请选送货日期")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //finish();
                                                    deliveryDate.setText("日/月/年");
                                                }
                                            })
                                            .show();
                                    TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                    textView.setTextSize(20);
                                }
                            }
                            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd/MM/yyyy");
                            SimpleDateFormat expectedPattern = new SimpleDateFormat("dd/MM/yyyy");

                            //month index start from 0, so + 1 to get correct actual month number
                            selectedMonth += 1;

                            try {
                                Date datetime = expectedPattern.parse(selectedDay + "/" + selectedMonth + "/" + selectedYear);
                                String timestamp = formatter.format(datetime);
                                deliveryDate.setText(timestamp);
                            } catch (Exception e){
                                deliveryDate.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
                            }

                            if (selectedMonth < 10){
                                ETADeliveryDate = selectedYear + "-0" + selectedMonth + "-" + selectedDay;
                            } else {
                                ETADeliveryDate = selectedYear + "-" + selectedMonth + "-" + selectedDay;
                            }

                            mCurrentDate.set(selectedDay, selectedMonth, selectedYear);
                        }
                    }
                }, year, month, day);
                mDatePicker.show();
            }
        });

        next = view.findViewById(R.id.btnPlaceOrder);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {

                //check if delivery date is selected
                String deliveryDateText = deliveryDate.getText().toString();
                if (deliveryDateText.equals("DD/MM/YYYY") || deliveryDateText.equals("日/月/年")) {
                    if (isEnglish.equals("Yes")) {
                        final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                .setMessage("Please select delivery date")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                })
                                .show();
                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                        textView.setTextSize(20);
                    } else {
                        final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                .setMessage("请选送货日期")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                })
                                .show();
                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                        textView.setTextSize(20);
                    }

                } else {

                    //check if paper bag is required
                    CheckBox paperBagRequired = (CheckBox)view.findViewById(R.id.checkBox);
                    if (paperBagRequired.isChecked()) {
                        paperBagNeeded = 1;
                    } else {
                        paperBagNeeded = 0;
                    }

                    //check if today's delivery is before cut off time
                    try{
                        Date currentTimestamp = new Date();

                        //cut off time stamp takes ETADeliveryDate
                        String deliveryDateTime = ETADeliveryDate + " " + cutoffTime;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                        Date cutoffTimestamp = sdf.parse(deliveryDateTime);

                        //compare current time is < cut off time
                        if (currentTimestamp.before(cutoffTimestamp)) {

                            //check total payable
                            doGetWalletAmt(customer.getDebtorCode());

                        } else {
                            System.out.println("delivery time after cut off");
                            //show error message
                            if (isEnglish.equals("Yes")) {
                                final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                        .setMessage("Today's delivery is over! Please choose another delivery date.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                            }
                                        })
                                        .show();
                                TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                textView.setTextSize(20);
                            } else {
                                final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                        .setMessage("今日送货已结束，请选其他送货日期")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                            }
                                        })
                                        .show();
                                TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                textView.setTextSize(20);
                            }
                        }

                    } catch (ParseException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    private double calculateSubtotal(ArrayList<Product> orderList) {
        double subtotal = 0;
        for (Product p : orderList) {
            int qty = p.getDefaultQty();
            subtotal += p.getUnitPrice() * qty;
        }
        return subtotal;
    }

    private void doGetWalletAmt(String customerCode) {
        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Double> call = service.getCustomerWallet(customerCode);
        call.enqueue(new Callback<Double>() {

            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                double data = response.body();
                DecimalFormat df = new DecimalFormat("#0.00");

                if (data == 0) {
                    walletDeduction = 0;
                } else if (data > Double.parseDouble(df.format(totalAmount))) {
                    //no need to pay
                    //303 > 77, wallet deduction is 77
                    walletDeduction = Double.parseDouble(df.format(totalAmount));

                } else {
                    //data <= wallet
                    //empty the wallet
                    walletDeduction = data;
                }

                //if payable is 0, dun need payment
                double totalPayable = Double.parseDouble(df.format(totalAmount)) - Double.parseDouble(df.format(walletDeduction));
                System.out.println("total payable " + totalPayable);
                if (totalPayable != 0.00){
                    System.out.println("total amt is " + Double.parseDouble(df.format(totalAmount)) + " and wallet deduction is " + Double.parseDouble(df.format(walletDeduction)) + " and total payable is " + Double.parseDouble(df.format(totalPayable)));
                    //go to payment activity
                    Intent intent = new Intent(view.getContext(), PaymentActivity.class);
                    intent.putParcelableArrayListExtra("orderList", orderList);
                    intent.putExtra("customer", customer);
                    intent.putExtra("subtotal", subtotal);
                    intent.putExtra("deliveryDate", ETADeliveryDate);
                    intent.putExtra("totalAmount", Double.parseDouble(df.format(totalAmount)));
                    intent.putExtra("walletDeduction", Double.parseDouble(df.format(walletDeduction)));
                    intent.putExtra("totalPayable", Double.parseDouble(df.format(totalPayable)));
                    intent.putExtra("language", isEnglish);
                    intent.putExtra("paperBagRequired", paperBagNeeded);
                    getActivity().startActivity(intent);
                } else {
                    //do not need to pay
                    System.out.println("no need to pay");
                    Intent intent = new Intent(view.getContext(), NonPaymentActivity.class);
                    intent.putParcelableArrayListExtra("orderList", orderList);
                    intent.putExtra("customer", customer);
                    intent.putExtra("subtotal", subtotal);
                    intent.putExtra("deliveryDate", ETADeliveryDate);
                    intent.putExtra("totalAmount", Double.parseDouble(df.format(totalAmount)));
                    intent.putExtra("walletDeduction", Double.parseDouble(df.format(walletDeduction)));
                    intent.putExtra("totalPayable", Double.parseDouble(df.format(totalPayable)));
                    intent.putExtra("language", isEnglish);
                    intent.putExtra("paperBagRequired", paperBagNeeded);
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}

