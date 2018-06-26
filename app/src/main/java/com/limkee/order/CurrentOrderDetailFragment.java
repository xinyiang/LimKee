package com.limkee.order;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.dao.OrderDAO;
import com.limkee.entity.Customer;
import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;
import com.limkee.entity.Product;
import java.text.DecimalFormat;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentOrderDetailFragment extends Fragment {
    private CurrentOrderDetailFragment.OnFragmentInteractionListener mListener;
    private CurrentOrderDetailAdapter mAdapter;
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    String orderID;
    private ArrayList<Product> orderList;
    private Customer customer;
    private String isEnglish;
    public static Retrofit retrofit;
    private String date;
    private int numItems;
    private String deliveryShift;

    public CurrentOrderDetailFragment() {
        // Required empty public constructor
    }

    public static CurrentOrderDetailFragment newInstance() {
        CurrentOrderDetailFragment fragment = new CurrentOrderDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        customer = bundle.getParcelable("customer");
        isEnglish = bundle.getString("language");
        orderID = bundle.getString("orderID");
        date = bundle.getString("deliveryDate");
        numItems = bundle.getInt("numItems");
        deliveryShift = bundle.getString("deliveryShift");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")){
            ((CurrentOrderDetailActivity) getActivity()).setActionBarTitle("Order details");
        } else {
            ((CurrentOrderDetailActivity) getActivity()).setActionBarTitle("订单详情");
        }


        Bundle bundle = getArguments();
        customer = bundle.getParcelable("customer");
        orderID = bundle.getString("orderID");
        isEnglish = bundle.getString("language");
        date = bundle.getString("deliveryDate");
        numItems = bundle.getInt("numItems");
        deliveryShift = bundle.getString("deliveryShift");
        doGetOrderDetails(orderID);
        doGetOrderQuantity(orderID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_order_detail, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);

        mAdapter = new CurrentOrderDetailAdapter(this, isEnglish);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);


        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.start();

        doGetOrderDetails(orderID);
        doGetOrderQuantity(orderID);

        //if english, change label to english
/*
        if (isEnglish.equals("Yes")){
            TextView lbl_subtotal_amt, lbl_total_amt, lbl_tax_amt;
            TextView lbl_delivery_details,lbl_name, lbl_contact, lbl_address, lbl_datetime;
            Button btnNext;

            lbl_subtotal_amt = (TextView) view.findViewById(R.id.lbl_subtotal_amt);
            lbl_total_amt = (TextView) view.findViewById(R.id.lbl_total_amt);
            lbl_tax_amt = (TextView) view.findViewById(R.id.lbl_tax_amt);
            lbl_delivery_details = (TextView) view.findViewById(R.id.lbl_delivery_details);
            lbl_name = (TextView) view.findViewById(R.id.lbl_name);
            lbl_contact = (TextView) view.findViewById(R.id.lbl_phone);
            lbl_address = (TextView) view.findViewById(R.id.lbl_address);
            lbl_datetime = (TextView) view.findViewById(R.id.lbl_datetime);
            btnNext = (Button) view.findViewById(R.id.btnNext);

            lbl_subtotal_amt.setText("Subtotal");
            lbl_tax_amt.setText("GST (7%)");
            lbl_total_amt.setText("Total Payable");
            lbl_delivery_details.setText("Delivery Details");
            lbl_name.setText("Name");
            lbl_contact.setText("Contact No");
            lbl_address.setText("Delivery Address");
            lbl_datetime.setText("Delivery Date/Time");
            btnNext.setText("Place Order");
        }
*/

        //display order details
        /*
        TextView subtotalAmt, tax, totalAmt;
        DecimalFormat df = new DecimalFormat("#0.00");
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        double subtotal = 56.50;
        subtotalAmt.setText("$" + df.format(subtotal));

        tax = view.findViewById(R.id.taxAmt);
        double taxAmt = subtotal * 0.07;
        tax.setText("$" + df.format(taxAmt));

        totalAmt = view.findViewById(R.id.totalAmt);
        double totalPayable = taxAmt + subtotal;
        totalAmt.setText("$" + df.format(totalPayable));
        */

        //display delivery details data
        TextView orderNo, status, address, deliveryDate, deliveryTime, itemCount;
        orderNo = (TextView) view.findViewById(R.id.orderID);
        status = (TextView) view.findViewById(R.id.status);
        address = (TextView) view.findViewById(R.id.address);
        deliveryDate = (TextView) view.findViewById(R.id.date);
        itemCount = (TextView) view.findViewById(R.id.itemCount);
        deliveryTime = (TextView) view.findViewById(R.id.time);


        orderNo.setText("#" + orderID);
        status.setText("Pending Delivery");
        address.setText(customer.getDeliverAddr1() + " " + customer.getDeliverAddr2() + " " + customer.getDeliverAddr3() + " " + customer.getDeliverAddr4());
        deliveryDate.setText(date);
        if (deliveryShift.equals("AM")){
            deliveryTime.setText("4.30am to 6.30am");
        } else {
            deliveryTime.setText("7.50am to 12.30pm");
        }

        if (isEnglish.equals("Yes")){

            if (numItems == 1){
                itemCount.setText(numItems + " item");
            } else {
                itemCount.setText(numItems + " items");
            }
        } else {
            itemCount.setText(numItems + "  样");
        }

        return view;
    }

    private void doGetOrderDetails(String orderID) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<OrderDetails> call = service.getCurrentOrderDetails(orderID);
        call.enqueue(new Callback<OrderDetails>() {

            @Override
            public void onResponse(Call<OrderDetails> call, Response<OrderDetails> response) {
                OrderDetails od = response.body();
                OrderDAO.od = od;
                mAdapter.update(od);

                //display order details
                TextView subtotalAmt, tax, totalAmt;
                DecimalFormat df = new DecimalFormat("#0.00");
                subtotalAmt = view.findViewById(R.id.subtotalAmt);
                double subtotal = od.getSubtotal();
                subtotalAmt.setText("$" + df.format(subtotal));

                tax = view.findViewById(R.id.taxAmt);
                double taxAmt = subtotal * 0.07;
                tax.setText("$" + df.format(taxAmt));

                totalAmt = view.findViewById(R.id.totalAmt);
                double totalPayable = taxAmt + subtotal;
                totalAmt.setText("$" + df.format(totalPayable));
            }

            @Override
            public void onFailure(Call<OrderDetails> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void doGetOrderQuantity(String orderID) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<ArrayList<OrderQuantity>> call = service.getCurrentOrderQuantity(orderID);
        call.enqueue(new Callback<ArrayList<OrderQuantity>>() {

            @Override
            public void onResponse(Call<ArrayList<OrderQuantity>> call, Response<ArrayList<OrderQuantity>> response) {
                ArrayList<OrderQuantity> oq = response.body();
                OrderDAO.oq = oq;
                mAdapter.update2(oq);
            }

            @Override
            public void onFailure(Call<ArrayList<OrderQuantity>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
