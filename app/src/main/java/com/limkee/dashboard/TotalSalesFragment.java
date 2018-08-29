package com.limkee.dashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.dao.OrderDAO;
import com.limkee.entity.Customer;
import com.limkee.entity.Order;
import com.limkee.order.CancelledOrderAdapter;
import com.limkee.order.CancelledOrderFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TotalSalesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TotalSalesFragment.OnFragmentInteractionListener mListener;
    private View view;
    private Customer customer;
    public static Retrofit retrofit;
    private  String isEnglish;
    private Spinner spinner;
    static TotalSalesFragment fragment;
    private static final String[] paths = {"3 months", "6 months", "12 months"};
    private String selectedYear;

    public TotalSalesFragment(){}

    public static TotalSalesFragment newInstance() {
        fragment = new TotalSalesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        isEnglish = bundle.getString("language");
        customer = bundle.getParcelable("customer");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_total_sales, container, false);
        spinner = (Spinner)view.findViewById(R.id.spinner1);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, paths );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(fragment);

        //get month from dropdown list value
        selectedYear = "2018"; //remove this when done

        doGetCustomerSales(customer.getCompanyCode(), selectedYear);

        doGetAllSales(selectedYear);

        //doGetAverageSales(selectedYear);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")) {
        } else {
        }
    }

    private void doGetCustomerSales(String companyCode, String selectedYear) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Map<Integer,Double>> call = service.getFilteredCustomerSales(companyCode, selectedYear);
        call.enqueue(new Callback<Map<Integer,Double>>() {

            @Override
            public void onResponse(Call<Map<Integer,Double>> call, Response<Map<Integer,Double>> response) {
                Map<Integer,Double> data = response.body();

                if (data.size() == 0) {
                    if (isEnglish.equals("Yes")) {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        lbl_noOrders.setText("No Sales");
                        */
                    } else {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        lbl_noOrders.setText("没有");
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        */
                    }
                } else {
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        int mth = (Integer)entry.getKey();
                        double amt = data.get(mth);
                        System.out.println("month " + mth + " for customer have sales amt of $ " + amt);
                    }

                }
            }

            @Override
            public void onFailure(Call<Map<Integer,Double>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    private void doGetAllSales(String selectedYear) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Map<Integer, Double>> call = service.getFilteredAllCustomerSales(selectedYear);
        call.enqueue(new Callback<Map<Integer, Double>>() {

            @Override
            public void onResponse(Call<Map<Integer, Double>> call, Response<Map<Integer, Double>> response) {
                Map<Integer, Double> data = response.body();

                if (data.size() == 0) {
                    if (isEnglish.equals("Yes")) {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        lbl_noOrders.setText("No Sales");
                        */
                    } else {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        lbl_noOrders.setText("没有");
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        */
                    }
                } else {
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        int mth = (Integer) entry.getKey();
                        double amt = data.get(mth);
                        System.out.println("month " + mth + " total sales amt is $ " + amt);
                    }

                }
            }

            @Override
            public void onFailure(Call<Map<Integer, Double>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void doGetAverageSales(String selectedYear) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Map<Integer, Double>> call = service.getAverageSales(selectedYear);
        call.enqueue(new Callback<Map<Integer, Double>>() {

            @Override
            public void onResponse(Call<Map<Integer, Double>> call, Response<Map<Integer, Double>> response) {
                Map<Integer, Double> data = response.body();

                if (data.size() == 0) {
                    if (isEnglish.equals("Yes")) {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        lbl_noOrders.setText("No Sales");
                        */
                    } else {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        lbl_noOrders.setText("没有");
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        */
                    }
                } else {
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        int mth = (Integer) entry.getKey();
                        double amt = data.get(mth);
                        System.out.println("month " + mth + " average sales amt is $ " + amt);
                    }

                }
            }

            @Override
            public void onFailure(Call<Map<Integer, Double>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CancelledOrderFragment.OnFragmentInteractionListener) {
            mListener = (TotalSalesFragment.OnFragmentInteractionListener) context;
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}