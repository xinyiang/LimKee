package com.limkee1.wallet;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.dao.WalletDAO;
import com.limkee1.entity.Customer;
import com.limkee1.entity.OrderDetails;
import com.limkee1.entity.OrderQuantity;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionHistoryDetailsFragment extends Fragment {
    private TransactionHistoryDetailsFragment.OnFragmentInteractionListener mListener;
    private TransactionHistoryDetailAdapter mAdapter;
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    String orderID;
    private Customer customer;
    private String isEnglish;
    public static Retrofit retrofit;
    private int numItems = 0;
    private OrderDetails od;
    private TextView itemCount;
    private int deduction;
    private double transactionAmt;
    private double subtotal;

    public TransactionHistoryDetailsFragment() {
    }

    public static TransactionHistoryDetailsFragment newInstance() {
        TransactionHistoryDetailsFragment fragment = new TransactionHistoryDetailsFragment();
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
        od = bundle.getParcelable("orderDetails");
        deduction = bundle.getInt("deduction");
        transactionAmt = bundle.getDouble("transactionAmount");
        subtotal  = bundle.getDouble("subtotal");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")){
            ((TransactionHistoryActivity) getActivity()).setActionBarTitle("Order Details");
        } else {
            ((TransactionHistoryActivity) getActivity()).setActionBarTitle("订单详情");
        }

        Bundle bundle = getArguments();
        customer = bundle.getParcelable("customer");
        orderID = bundle.getString("orderID");
        isEnglish = bundle.getString("language");
        od = bundle.getParcelable("orderDetails");
        doGetOrderQuantity(orderID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transaction_history_details, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView = (RecyclerView) view.findViewById(com.limkee1.R.id.recyclerView);

        mAdapter = new TransactionHistoryDetailAdapter(this, isEnglish, od);
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

        doGetOrderQuantity(orderID);

        //if english, change label to english
        TextView orderNo, status, deliveryDate, company;
        orderNo = (TextView) view.findViewById(R.id.orderID);
        status = (TextView) view.findViewById(R.id.status);
        deliveryDate = (TextView) view.findViewById(R.id.deliveredDate);
        itemCount = (TextView) view.findViewById(R.id.lbl_itemsCount);
        company = (TextView) view.findViewById(R.id.companyName);

        orderNo.setText("#" + orderID);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd/MM/yyyy");
        SimpleDateFormat expectedPattern = new SimpleDateFormat("dd/MM/yyyy");

        String currentStatus = od.getStatus();

        if (isEnglish.equals("Yes")){
            if (currentStatus.equals("Pending Delivery")) {
                status.setText("Pending Delivery");
            } else if (currentStatus.equals("Delivered")){
                status.setText("Delivered");
            } else if (currentStatus.equals("Cancelled")){
                status.setText("Cancelled");
            } else {
                status.setText("");
            }
        } else {
            if (currentStatus.equals("Pending Delivery")) {
                status.setText("待送货");
            } else if (currentStatus.equals("Delivered")){
                status.setText("已送货");
            } else if (currentStatus.equals("Cancelled")){
                status.setText("取消");
            } else {
                status.setText("");
            }
        }

        company.setText(customer.getCompanyName());

        TextView subtotalAmt, tax, lbl_totalAmt, totalAmt, paidAmt, lbl_paidAmt, lbl_walletDeductedAmt, walletDeductedAmt;
        DecimalFormat df = new DecimalFormat("#0.00");
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        lbl_paidAmt  = view.findViewById(R.id.lbl_paid_amt);
        paidAmt = view.findViewById(R.id.paidAmt);
        lbl_walletDeductedAmt  = view.findViewById(R.id.lbl_walletDeducted_amt);
        walletDeductedAmt = view.findViewById(R.id.walletDeductedAmt);
        tax = view.findViewById(R.id.taxAmt);
        totalAmt = view.findViewById(R.id.totalAmt);
        lbl_totalAmt = view.findViewById(R.id.lbl_total_amt);

       // subtotal = transactionAmt;
        if (deduction == 1){
            //show paid amt and wallet deducted amt
            lbl_paidAmt.setVisibility(View.VISIBLE);
            paidAmt.setVisibility(View.VISIBLE);
            paidAmt.setText("$" + df.format(od.getPaidAmt()));

            lbl_walletDeductedAmt.setVisibility(View.VISIBLE);
            walletDeductedAmt.setVisibility(View.VISIBLE);
            double walletDeductionAmt = Double.parseDouble(df.format((transactionAmt)));
            walletDeductedAmt.setText("-$" + walletDeductionAmt);
        } else {
            //subtotal remove 7% gst
            subtotal = transactionAmt*(100.0/107.0);
            //bold total amount and increase font size
            totalAmt.setTypeface(null, Typeface.BOLD);
            lbl_totalAmt.setTypeface(null, Typeface.BOLD);
            totalAmt.setTextSize(20);
            lbl_totalAmt.setTextSize(20);
        }

        subtotalAmt.setText("$" + df.format(subtotal));
        double taxAmt = subtotal * 0.07;
        tax.setText("$" + df.format(taxAmt));
        double totalPayable = taxAmt + subtotal;
        totalAmt.setText("$" + df.format(totalPayable));

        TextView orderDateTxt = view.findViewById(R.id.orderDate);
        String orderDate = od.getOrderDate();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h.mmaa");
            Date datetime = expectedPattern.parse(orderDate);
            String createdTimestamp = sdf.format(datetime);
            orderDateTxt.setText(createdTimestamp);
        } catch (Exception e){
            String date = orderDate.substring(8,10);
            String month = orderDate.substring(5,7);
            String year = orderDate.substring(0,4);
            String formatOrderDate = date + "/" + month + "/" + year;

            String hr = orderDate.substring(11,13);
            String min = orderDate.substring(14,16);
            orderDateTxt.setText(formatOrderDate + " " + hr + "." + min);
        }

        String ETA = od.getDeliveryDate();
        String date = ETA.substring(8);
        String month = ETA.substring(5,7);
        String year = ETA.substring(0,4);
        String ETADeliveryDate = date + "/" + month + "/" + year;
        deliveryDate.setText(ETADeliveryDate);

        return view;
    }

    private void doGetOrderQuantity(String orderID) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<ArrayList<OrderQuantity>> call = service.getTransactionProductDetails(orderID, od.getTransactionID(), od.getTransactionStatus());
        call.enqueue(new Callback<ArrayList<OrderQuantity>>() {

            @Override
            public void onResponse(Call<ArrayList<OrderQuantity>> call, Response<ArrayList<OrderQuantity>> response) {
                ArrayList<OrderQuantity> oq = response.body();
                WalletDAO.refundTransactionDetails = oq;
                mAdapter.update2(oq);
                numItems = oq.size();

                if (isEnglish.equals("Yes")){
                    if (numItems == 1){
                        itemCount.setText(" Product Details (" + numItems + " item)");
                    } else {
                        itemCount.setText(" Product Details (" + numItems + " items)");
                    }
                } else {
                    itemCount.setText(" 订单样品 (" + numItems + " 样)");
                }
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
                    + " must implement OnFragmentInteractionListener in transaction history");
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
