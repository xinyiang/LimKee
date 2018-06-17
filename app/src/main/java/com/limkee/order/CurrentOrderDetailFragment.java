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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.limkee.R;
import com.limkee.entity.Customer;
import com.limkee.entity.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


public class CurrentOrderDetailFragment extends Fragment {
    private CurrentOrderDetailFragment.OnFragmentInteractionListener mListener;
    private CurrentOrderDetailAdapter mAdapter;
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    CompositeDisposable compositeDisposable;
    String orderID;
    private ArrayList<Product> orderList;

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
        //customer = (Customer) savedInstanceState.getSerializable("customer");
        orderID = bundle.getString("orderID");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((CurrentOrderDetailActivity) getActivity()).setActionBarTitle("Order details");

        Bundle bundle = getArguments();
        //customer = (Customer) savedInstanceState.getSerializable("customer");
        orderID = bundle.getString("orderID");
        //get order list from this order
        //doGetOrderList(orderID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_order_detail, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

/*
        recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);
        mAdapter = new CurrentOrderDetailAdapter(this, orderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        // mAdapter.notifyDataSetChanged();
*/
        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.start();


        //if english, change label to english
        /*
        if (){
            TextView lbl_subtotal_amt, lbl_total_amt, lbl_tax_amt;
            TextView lbl_delivery_details.lbl_name, lbl_contact, lbl_address, lbl_datetime;
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

        //display delivery details data
        TextView orderNo, status, address, deliveryDate, deliveryTime,itemCount;
        orderNo = (TextView) view.findViewById(R.id.orderID);
        status = (TextView) view.findViewById(R.id.status);
        address = (TextView) view.findViewById(R.id.address);
        deliveryDate = (TextView) view.findViewById(R.id.date);
        deliveryTime = (TextView) view.findViewById(R.id.time);
        itemCount = (TextView) view.findViewById(R.id.itemCount);

        orderNo.setText("#" + orderID);
        status.setText("Pending Delivery");
        address.setText("Blk 123 Ang Mo Kio Industrial Park #02-551 Singapore 740123");
        deliveryDate.setText("16/6/2018");
        deliveryTime.setText("4am to 9.30am");
        itemCount.setText("3 items");
        return view;
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
