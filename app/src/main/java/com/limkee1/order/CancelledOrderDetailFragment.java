package com.limkee1.order;

import android.content.Context;
import android.graphics.Typeface;
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
import com.limkee1.R;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.dao.OrderDAO;
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

public class CancelledOrderDetailFragment extends Fragment {
    private CancelledOrderDetailFragment.OnFragmentInteractionListener mListener;
    private CancelledOrderDetailAdapter mAdapter;
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    String orderID;
    private Customer customer;
    private String isEnglish;
    public static Retrofit retrofit;
    private String date;
    private int numItems;
    private int paperBagRequiredNeeded;

    public CancelledOrderDetailFragment() {
        // Required empty public constructor
    }

    public static CancelledOrderDetailFragment newInstance() {
        CancelledOrderDetailFragment fragment = new CancelledOrderDetailFragment();
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

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")){
            ((CancelledOrderDetailActivity) getActivity()).setActionBarTitle("Order Details");
        } else {
            ((CancelledOrderDetailActivity) getActivity()).setActionBarTitle("订单详情");
        }

        Bundle bundle = getArguments();
        customer = bundle.getParcelable("customer");
        orderID = bundle.getString("orderID");
        isEnglish = bundle.getString("language");
        date = bundle.getString("deliveryDate");
        numItems = bundle.getInt("numItems");
        doGetOrderDetails(orderID);
        doGetOrderQuantity(orderID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cancelled_order_detail, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView = (RecyclerView) view.findViewById(com.limkee1.R.id.recyclerView);

        mAdapter = new CancelledOrderDetailAdapter(this, isEnglish);
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

        TextView orderNo, cancelledStatus, address, deliveryDate, company, itemCount, orderDetails, amountDetails, deliveryDetails;
        orderNo = (TextView) view.findViewById(R.id.orderID);
        deliveryDate = (TextView) view.findViewById(R.id.deliveredDate);
        itemCount = (TextView) view.findViewById(R.id.lbl_itemsCount);
        company = (TextView) view.findViewById(R.id.companyName);
        orderDetails = (TextView) view.findViewById(R.id.lbl_order_details);
        amountDetails = (TextView) view.findViewById(R.id.lbl_amountDetails);
        deliveryDetails = (TextView) view.findViewById(R.id.lbl_deliveryDetails);

        orderNo.setText("#" + orderID);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd/MM/yyyy");
        SimpleDateFormat expectedPattern = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date datetime = expectedPattern.parse(date);
            String timestamp = formatter.format(datetime);
            deliveryDate.setText(timestamp);
        } catch (Exception e){
            deliveryDate.setText(date);
        }

        if (isEnglish.equals("Yes")){
            orderDetails.setText(" Order Details");
            amountDetails.setText(" Amount Details");
            deliveryDetails.setText(" Delivery Details");

            if (numItems == 1){
                itemCount.setText(" Product Details (" + numItems + " item)");
            } else {
                itemCount.setText(" Product Details (" + numItems + " items)");
            }

        } else {
            itemCount.setText(" 订单样品 (" + numItems + " 样)");
        }

        company.setText(customer.getCompanyName());

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
        Call<OrderDetails> call = service.getCancelledOrderDetails(orderID);
        call.enqueue(new Callback<OrderDetails>() {

            @Override
            public void onResponse(Call<OrderDetails> call, Response<OrderDetails> response) {
                OrderDetails od = response.body();
                OrderDAO.cancelledOD = od;
                mAdapter.update(od);

                //display order details
                paperBagRequiredNeeded = od.getPaperBagRequired();

                TextView paperBagRequired;
                paperBagRequired = (TextView) view.findViewById(R.id.paperBag);

                if (paperBagRequiredNeeded == 1){
                    if (isEnglish.equals("Yes")){
                        paperBagRequired.setText("Yes");
                    } else {
                        paperBagRequired.setText("需要");
                    }
                } else {
                    if (isEnglish.equals("Yes")) {
                        paperBagRequired.setText("No");
                    } else {
                        paperBagRequired.setText("不需要");
                    }
                }
                TextView subtotalAmt, tax, lbl_totalAmt, totalAmt, paidAmt, lbl_paidAmt, lbl_walletDeductedAmt, walletDeductedAmt;
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
                lbl_totalAmt = view.findViewById(R.id.lbl_total_amt);

                //show paid amt and wallet deducted amt
                double walletDeduction = (od.getSubtotal()*1.07) - od.getPaidAmt();

                if (walletDeduction <= 0){
                    //do not show wallet deduction and paid amount
                    //bold total amount and increase font size
                    totalAmt.setTypeface(null, Typeface.BOLD);
                    lbl_totalAmt.setTypeface(null, Typeface.BOLD);
                    totalAmt.setTextSize(20);
                    lbl_totalAmt.setTextSize(20);
                } else {
                    lbl_paidAmt  = view.findViewById(R.id.lbl_paid_amt);
                    paidAmt = view.findViewById(R.id.paidAmt);

                    lbl_paidAmt.setVisibility(View.VISIBLE);
                    paidAmt.setVisibility(View.VISIBLE);
                    paidAmt.setText("$" + df.format(od.getPaidAmt()));

                    lbl_walletDeductedAmt  = view.findViewById(R.id.lbl_walletDeducted_amt);
                    walletDeductedAmt = view.findViewById(R.id.walletDeductedAmt);

                    lbl_walletDeductedAmt.setVisibility(View.VISIBLE);
                    walletDeductedAmt.setVisibility(View.VISIBLE);
                    walletDeductedAmt.setText("-$" + df.format(walletDeduction));

                    //unbold totalamount and font size
                    totalAmt.setTextSize(18);
                    totalAmt.setTypeface(null, Typeface.NORMAL);
                    lbl_totalAmt.setTextSize(18);
                    lbl_totalAmt.setTypeface(null, Typeface.NORMAL);
                }

                TextView orderDateTxt = view.findViewById(R.id.orderDate);
                String orderDate = od.getOrderDate();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h.mm aa");
                    SimpleDateFormat expectedPattern = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
                    orderDateTxt.setText(formatOrderDate + " " + hr + ":" + min);
                }
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
        Call<ArrayList<OrderQuantity>> call = service.getCancelledOrderQuantity(orderID);
        call.enqueue(new Callback<ArrayList<OrderQuantity>>() {

            @Override
            public void onResponse(Call<ArrayList<OrderQuantity>> call, Response<ArrayList<OrderQuantity>> response) {
                ArrayList<OrderQuantity> oq = response.body();
                OrderDAO.cancelledOQ = oq;
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
