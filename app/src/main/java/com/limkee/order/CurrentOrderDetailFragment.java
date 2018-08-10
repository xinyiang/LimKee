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
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.dao.OrderDAO;
import com.limkee.entity.Customer;
import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            ((CurrentOrderDetailActivity) getActivity()).setActionBarTitle("Order Details");
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
        TextView orderNo, status, address, deliveryDate, deliveryTime, itemCount;
        orderNo = (TextView) view.findViewById(R.id.orderID);
        status = (TextView) view.findViewById(R.id.status);
        address = (TextView) view.findViewById(R.id.deliveryAddress);
        deliveryDate = (TextView) view.findViewById(R.id.deliveredDate);
        itemCount = (TextView) view.findViewById(R.id.lbl_itemsCount);
        deliveryTime = (TextView) view.findViewById(R.id.deliveredTime);

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
        String address3 = "";
        String address4 = "";
        if (customer.getDeliverAddr3() == null){
            address3 = "";
        }

        if (customer.getDeliverAddr4() == null){
            address4 = "";
        }

        address.setText(customer.getDeliverAddr1() + " " + customer.getDeliverAddr2() + " " + address3 + " " + address4);

        if (isEnglish.equals("Yes")){
            TextView lbl_subtotal_amt, lbl_total_amt, lbl_tax_amt, lbl_orderDetails, lbl_amtDetails, lbl_deliveryDetails;
            TextView lbl_orderID, lbl_orderDate, lbl_status, lbl_address, lbl_date, lbl_time;
            lbl_orderID  = (TextView) view.findViewById(R.id.lbl_orderID);
            lbl_orderDate  = (TextView) view.findViewById(R.id.lbl_orderDate);
            lbl_status  = (TextView) view.findViewById(R.id.lbl_status);
            lbl_subtotal_amt = (TextView) view.findViewById(R.id.lbl_subtotal_amt);
            lbl_total_amt = (TextView) view.findViewById(R.id.lbl_total_amt);
            lbl_tax_amt = (TextView) view.findViewById(R.id.lbl_tax_amt);
            lbl_address = (TextView) view.findViewById(R.id.lbl_deliveryAddress);
            lbl_date = (TextView) view.findViewById(R.id.lbl_deliveryDate);
            lbl_time = (TextView) view.findViewById(R.id.lbl_deliveryTime);
            lbl_orderDetails = (TextView) view.findViewById(R.id.lbl_order_details);
            lbl_deliveryDetails = (TextView) view.findViewById(R.id.lbl_deliveryDetails);
            lbl_amtDetails = (TextView) view.findViewById(R.id.lbl_amountDetails);

            lbl_orderDetails.setText(" Order Details");
            lbl_orderID.setText("Order ID");
            lbl_orderDate.setText("Order Date");
            lbl_status.setText("Status");

            lbl_deliveryDetails.setText(" Delivery Details");
            status.setText("Pending Delivery");
            lbl_address.setText("Delivery Address");

            lbl_date.setText("Delivery Date");
            lbl_time.setText("Delivery Time");

            lbl_amtDetails.setText(" Amount Details");
            lbl_subtotal_amt.setText("Sub Total");
            lbl_tax_amt.setText("GST (7%)");
            lbl_total_amt.setText("Total Amount");

            if (numItems == 1){
                itemCount.setText(" Product Details (" + numItems + " item)");
            } else {
                itemCount.setText(" Product Details (" + numItems + " items)");
            }

        } else {
            status.setText("待送货");
            itemCount.setText(" 订单样品 (" + numItems + " 样)");
        }

        if (deliveryShift.equals("AM")){
            deliveryTime.setText("4.30am - 6.30am");
        } else {
            deliveryTime.setText("7.50am - 12.30pm");
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

                TextView orderDateTxt = view.findViewById(R.id.orderDate);
                String orderDate = od.getOrderDate();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h.mmaa");
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
                    orderDateTxt.setText(formatOrderDate + " " + hr + "." + min);
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
