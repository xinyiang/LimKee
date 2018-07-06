package com.limkee.order;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.dao.CatalogueDAO;
import com.limkee.entity.Customer;
import com.limkee.entity.Product;
import com.limkee.payment.PaymentActivity;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private double totalPayable;
    private ConfirmOrderAdapter mAdapter;
    private ArrayList<Product> orderList;
    private String isEnglish;
    private Customer customer;
    private String deliveryShift;
    CompositeDisposable compositeDisposable;
    private String newOrderID;
    private String ETADeliveryDate;
    private int day;
    private int month;
    private int year;

    public ConfirmOrderFragment() {
        // Required empty public constructor
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
        deliveryShift = bundle.getString("deliveryShift");

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

        //if english, set label in english language
        TextView lblSubtotal = view.findViewById(R.id.lbl_subtotal_amt);
        TextView lblTax = view.findViewById(R.id.lbl_tax_amt);
        TextView lblFinalTotal = view.findViewById(R.id.lbl_total_amt);

        if (isEnglish.equals("Yes")) {
            lblSubtotal.setText("Sub Total");
            lblTax.setText("7% GST");
            lblFinalTotal.setText("Total");
        } else {
            lblSubtotal.setText("小计");
            lblTax.setText("7% 税");
            lblFinalTotal.setText("总额");
        }

        recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);
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
        totalPayable = taxAmt + subtotal;
        totalAmt.setText("$" + df.format(totalPayable));

        //if english, change label to english
        if (isEnglish.equals("Yes")) {
            TextView lbl_subtotal_amt, lbl_total_amt, lbl_tax_amt;
            TextView lbl_delivery_details, lbl_name, lbl_contact, lbl_address, lbl_date, lbl_time, lbl_item_details, lbl_amt_details;
            Button btnNext;
            lbl_subtotal_amt = (TextView) view.findViewById(R.id.lbl_subtotal_amt);
            lbl_total_amt = (TextView) view.findViewById(R.id.lbl_total_amt);
            lbl_tax_amt = (TextView) view.findViewById(R.id.lbl_tax_amt);
            lbl_delivery_details = (TextView) view.findViewById(R.id.lbl_delivery_details);
            lbl_name = (TextView) view.findViewById(R.id.lbl_name);
            lbl_contact = (TextView) view.findViewById(R.id.lbl_phone);
            lbl_address = (TextView) view.findViewById(R.id.lbl_address);
            lbl_date = (TextView) view.findViewById(R.id.lbl_date);
            lbl_time = (TextView) view.findViewById(R.id.lbl_time);
            lbl_item_details = (TextView) view.findViewById(R.id.lbl_items);
            lbl_amt_details = (TextView) view.findViewById(R.id.lbl_amountDetails);
            btnNext = (Button) view.findViewById(R.id.btnPlaceOrder);

            lbl_subtotal_amt.setText("Sub Total");
            lbl_tax_amt.setText("Add 7% GST");
            lbl_total_amt.setText("Total");
            lbl_delivery_details.setText("Delivery Details");
            lbl_name.setText("Name");
            lbl_contact.setText("Contact No");
            lbl_address.setText("Address");
            lbl_date.setText("Date");
            lbl_time.setText("Time");
            lbl_item_details.setText(" " + orderList.size() + "items");
            lbl_amt_details.setText("Amount details");
            btnNext.setText("Place Order");
        }

        //display delivery details data
        TextView deliveryDetails, name, contact, address, deliveryTime, numItems, amtDetails;
        EditText deliveryDate;
        Button placeOrder;
        deliveryDetails = (TextView) view.findViewById(R.id.lbl_delivery_details);
        name = (TextView) view.findViewById(R.id.name);
        contact = (TextView) view.findViewById(R.id.phone);
        address = (TextView) view.findViewById(R.id.address);
        deliveryDate = (EditText) view.findViewById(R.id.date);
        numItems = (TextView) view.findViewById(R.id.lbl_items);
        amtDetails = (TextView) view.findViewById(R.id.lbl_amountDetails);
        deliveryTime = (TextView) view.findViewById(R.id.deliveryTime);
        placeOrder = (Button) view.findViewById(R.id.btnPlaceOrder);

        name.setText(customer.getDebtorName());
        contact.setText(customer.getDeliveryContact());
        String address3 = "";
        String address4 = "";
        if (customer.getDeliverAddr3() == null){
            address3 = "";
        }

        if (customer.getDeliverAddr4() == null){
            address4 = "";
        }

        address.setText(customer.getDeliverAddr1() + " " + customer.getDeliverAddr2() + " " + address3 + " " + address4);

      //  address.setText(customer.getDeliverAddr1() + " " + customer.getDeliverAddr2() + " " + customer.getDeliverAddr3() + " " + customer.getDeliverAddr4());
        if (isEnglish.equals("Yes")) {
            deliveryDetails.setText(" Delivery details");
            if (orderList.size() == 1) {
                numItems.setText(" " + orderList.size() + " item");
            } else {
                numItems.setText(" " + orderList.size() + " items");
            }
            amtDetails.setText(" Amount details");
            deliveryDate.setText("DD/MM/YY");
            placeOrder.setText("Place Order");
        } else {
            deliveryDetails.setText(" 送货详情");
            numItems.setText(" " + orderList.size() + " 样");
            amtDetails.setText("  价钱详情");
            deliveryDate.setText("日/月/年");
            placeOrder.setText("确认订单");
        }

        if (deliveryShift.equals("AM")) {
            deliveryTime.setText("4.30am to 6.30am");
        } else {
            deliveryTime.setText("7.50am to 12.30pm");
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
                        month = selectedMonth;
                        //month index start from 0, so + 1 to get correct actual month number
                        selectedMonth += 1;
                        day = selectedDay;
                        deliveryDate.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
                        ETADeliveryDate = selectedYear + "-" + selectedMonth + "-" + selectedDay;
                        mCurrentDate.set(selectedDay, selectedMonth, selectedYear);
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
                        final Toast tag = Toast.makeText(view.getContext(), "Please select delivery date", Toast.LENGTH_SHORT);
                        new CountDownTimer(20000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                tag.show();
                            }

                            public void onFinish() {
                                tag.show();
                            }

                        }.start();

                    } else {
                        final Toast tag = Toast.makeText(view.getContext(), "请选送货日期", Toast.LENGTH_SHORT);
                        new CountDownTimer(20000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                tag.show();
                            }

                            public void onFinish() {
                                tag.show();
                            }

                        }.start();
                    }
                } else {
                    //check if date is >= today's date

                    Calendar c = Calendar.getInstance();

                    // set the calendar to start of today
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    // and get that as a Date
                    Date today = c.getTime();

                    // reuse the calendar to set user specified date
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.DAY_OF_MONTH, day);
                    Date dateSpecified = c.getTime();

                    if (dateSpecified.before(today)) {

                        System.err.println("Date specified [" + dateSpecified + "] is before today [" + today + "]");

                        if (isEnglish.equals("Yes")) {

                            final Toast tag = Toast.makeText(view.getContext(), "Invalid delivery date! Please select another delivery date.", Toast.LENGTH_SHORT);
                            new CountDownTimer(20000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    tag.show();
                                }

                                public void onFinish() {
                                    tag.show();
                                }

                            }.start();

                            deliveryDate.setText("DD/MM/YYYY");

                        } else {

                            final Toast tag = Toast.makeText(view.getContext(), "送货日期错误, 请选送货日期", Toast.LENGTH_SHORT);
                            new CountDownTimer(20000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    tag.show();
                                }

                                public void onFinish() {
                                    tag.show();
                                }

                            }.start();

                            deliveryDate.setText("日/月/年");
                        }
                    } else {
                        System.err.println("Date specified [" + dateSpecified + "] is NOT before today [" + today + "]");

                        //check if today's delivery is before cut off time


                        //insert into database 3 tables
                        createSalesOrder();

                        //go to payment activity
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("customer", customer);
                        bundle.putParcelableArrayList("orderList", orderList);
                        bundle.putDouble("subtotal", subtotal);
                        bundle.putDouble("taxAmt", taxAmt);
                        bundle.putString("deliveryDate", ETADeliveryDate);
                        bundle.putDouble("totalPayable", totalPayable);
                        //add sale details order here

                        Intent intent = new Intent(view.getContext(), PaymentActivity.class);
                        intent.putParcelableArrayListExtra("orderList", orderList);
                        intent.putExtra("customer", customer);
                        intent.putExtra("subtotal", subtotal);
                        intent.putExtra("taxAmt", taxAmt);
                        intent.putExtra("deliveryDate", ETADeliveryDate);
                        intent.putExtra("totalPayable", totalPayable);
                        //add sale details order here
                        getActivity().startActivity(intent);

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

    private void createSalesOrder() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(PostData.class);

        compositeDisposable.add(postData.addSalesOrder(customer.getDebtorCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderResponse, this::handleError));
    }

    private void handleSalesOrderResponse(String orderID) {

        if (orderID != null) {
            //create Sales Order Details
            newOrderID = orderID;
            System.out.println("SALES ORDER IS " + orderID);
            createSalesOrderDetails(orderID);
        }
    }

    private void createSalesOrderDetails(String orderID) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(PostData.class);

        compositeDisposable.add(postData.addSalesOrderDetails(ETADeliveryDate, subtotal, orderID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderDetailsResponse, this::handleError));

    }

    private void handleSalesOrderDetailsResponse(boolean added) {
        System.out.println("SALES ORDER ADDED " + added);

        if (added) {
            //create Sales Order Quantity
            createSalesOrderQuantity();

        }

    }

    private void createSalesOrderQuantity() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(PostData.class);

        ArrayList<String> itemQuantity = new ArrayList<String>();

        for (Product p : orderList){
            itemQuantity.add(p.getItemCode() + "&" + p.getDefaultQty());
        }

        compositeDisposable.add(postData.addSalesOrderQuantity(itemQuantity, newOrderID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSalesOrderQuantityResponse, this::handleError));

    }

    private void handleSalesOrderQuantityResponse(int numProducts) {

        System.out.println("SALES ORDER NUMBER OF PRODUCTS " + numProducts);
        if (numProducts == orderList.size()){

            final Toast tag = Toast.makeText(view.getContext(), "Order #" + newOrderID + " is placed successfully",  Toast.LENGTH_SHORT);
            new CountDownTimer(20000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tag.show();
                }

                public void onFinish() {
                    tag.show();
                }

            }.start();
        }

    }


    private void handleError(Throwable error) {

    }



}