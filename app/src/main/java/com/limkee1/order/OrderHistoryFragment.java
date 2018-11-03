package com.limkee1.order;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.dao.OrderDAO;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Order;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderHistoryFragment extends Fragment {

    private OrderHistoryFragment.OnFragmentInteractionListener mListener;
    private OrderHistoryAdapter mAdapter;
    private View view;
    private RecyclerView recyclerView;
    private Customer customer;
    public static Retrofit retrofit;
    private String isEnglish;
    private Bundle myBundle = new Bundle();
    TextView lbl_noOrders;
    boolean hasInternet;

    public OrderHistoryFragment(){}

    public static OrderHistoryFragment newInstance() {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
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
        myBundle.putParcelable("customer", customer);
        myBundle.putString("language", isEnglish);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_history, container, false);

        TextView lbl_orderIDHeader, lbl_numItemsHeader, lbl_deliveryDateHeader;

        lbl_orderIDHeader = (TextView) view.findViewById(R.id.lbl_orderIDHeader);
        lbl_deliveryDateHeader = (TextView) view.findViewById(R.id.lbl_deliveryDateHeader);
        lbl_numItemsHeader = (TextView) view.findViewById(R.id.lbl_numItemsHeader);

        if (isEnglish.equals("Yes")) {
            lbl_orderIDHeader.setText("Order ID");
            lbl_deliveryDateHeader.setText("Delivery Date");
            lbl_numItemsHeader.setText("Total No.");
        }


        hasInternet = isNetworkAvailable();
        if (!hasInternet) {
            TextView lbl_noInternet = view.findViewById(R.id.lbl_noOrders);
            lbl_noInternet.setVisibility(View.VISIBLE);

            if (isEnglish.equals("Yes")) {
                lbl_noInternet.setText("No internet connection");
            } else {
                lbl_noInternet.setText("没有网络");
            }

        } else {
            recyclerView = view.findViewById(R.id.orderHistoryRecyclerView);
            recyclerView = (RecyclerView) view.findViewById(R.id.orderHistoryRecyclerView);
            mAdapter = new OrderHistoryAdapter(this, OrderDAO.historyOrdersList, customer, isEnglish);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(mAdapter);
            doGetOrderHistory(customer.getDebtorCode());
        }
        return view;
    }

    private void doGetOrderHistory(String customerCode) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<ArrayList<Order>> call = service.getOrderHistory(customerCode);
        call.enqueue(new Callback<ArrayList<Order>>() {

            @Override
            public void onResponse(Call<ArrayList<Order>> call, Response<ArrayList<Order>> response) {
                ArrayList<Order> data = response.body();
                OrderDAO.historyOrdersList = data;
                System.out.println("Order History Size is " + data.size());
                mAdapter.update(OrderDAO.historyOrdersList);

                if (data.size() == 0) {
                    lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                    view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);

                    if (isEnglish.equals("Yes")) {
                        lbl_noOrders.setText("No order history");
                    } else {
                        lbl_noOrders.setText("没有历史订单");
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Order>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
