package com.limkee.catalogue;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.dao.CatalogueDAO;
import com.limkee.entity.Product;
import com.limkee.navigation.NavigationActivity;
import com.limkee.order.ConfirmOrderActivity;
import android.support.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CatalogueFragment extends Fragment {
    private CatalogueFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    public static Bundle myBundle = new Bundle();
    public static CatalogueFragment fragment;
    public static View view;
    private CatalogueAdapter mAdapter;
    private ProgressBar progressBar;
    public static RecyclerView recyclerView;
    public static Button confirmOrder;
    public static TextView subtotalAmt;
    public static double subtotal;
    public static ArrayList<Product> tempOrderList = new ArrayList<>();

    public CatalogueFragment(){
    }

    public static CatalogueFragment newInstance() {
        fragment = new CatalogueFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NavigationActivity)getActivity()).setActionBarTitle("Catalogue");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        //by default, temporary order list is catalogue list
        DecimalFormat df = new DecimalFormat("#0.00");
        subtotalAmt.setText("$" + df.format(calculateSubtotal(CatalogueDAO.catalogue_list)));

        doGetCatalogue();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //proceed to confirm order
        confirmOrder = (Button) view.findViewById(R.id.btnNext);

        confirmOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                recyclerView.setItemViewCacheSize(tempOrderList.size());

                tempOrderList = CatalogueAdapter.getOrderList();
                ArrayList <Product> orderList = new ArrayList<>();

                //remove products that has 0 quantity
                for (Product p : tempOrderList){
                    if (p.getDefaultQty() != 0) {
                        orderList.add(p);
                    }
                }

                //check if subtotal hits minimum requirements
                if(calculateSubtotal(orderList) < 30){
                    final Toast tag = Toast.makeText(view.getContext(), "Minimum order is $30.00.",  Toast.LENGTH_SHORT);
                    new CountDownTimer(20000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tag.show();
                        }

                        public void onFinish() {
                            tag.show();
                        }

                    }.start();
                } else {
                    DecimalFormat df = new DecimalFormat("#0.00");
                    subtotalAmt = view.findViewById(R.id.subtotalAmt);
                    subtotal =  calculateSubtotal(orderList);
                    subtotalAmt.setText("$" + df.format(subtotal));

                    // //updateSubtotal(orderList);
                    CatalogueDAO.order_list = orderList;

                    //store all products with qty > 1 into a temporary arraylist of products
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("orderList", orderList);

                    Intent intent = new Intent(view.getContext(), ConfirmOrderActivity.class);
                    intent.putParcelableArrayListExtra("orderList", orderList);
                    getActivity().startActivity(intent);
                    }
                }
        });
    }
/*
    private void doGetCatalogue() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(PostData.class);

        Map<String, String> fieldsMap = new HashMap<>();
        //fieldsMap.put("api", HttpConstant.API_KEY);


        //compositeDisposable.add(postData.getProducts(fieldsMap)
                compositeDisposable.add(postData.getCatalogue()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(ArrayList<Product> productList) {
        CatalogueDAO.catalogue_list = productList;
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);
        mAdapter = new CatalogueAdapter(this, CatalogueDAO.catalogue_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void handleError(Throwable error) {

    }
*/

    private void doGetCatalogue() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);

        //by default, let order list be the same as catalogue. if there is any change in qty, it will be updated.
        tempOrderList = CatalogueDAO.catalogue_list;
        String[] qtyDataSet= new String[tempOrderList.size()];
        for (int i = 0; i <tempOrderList.size(); i++) {
            Product p = tempOrderList.get(i);
            qtyDataSet[i] = Integer.toString(p.getDefaultQty());
        }


        mAdapter = new CatalogueAdapter(this, CatalogueDAO.catalogue_list, qtyDataSet, tempOrderList);
        recyclerView.setItemViewCacheSize(qtyDataSet.length);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private double calculateSubtotal(ArrayList<Product> orderList) {
        double subtotal = 0;
        for(Product p : orderList) {
            int qty = p.getDefaultQty();
            subtotal += p.getUnitPrice() * qty;
        }
        return subtotal;
    }

    public static void updateSubtotal(ArrayList<Product> orderList) {
        double subtotal = 0;
        for(Product p : orderList) {
            int qty = p.getDefaultQty();
            subtotal += p.getUnitPrice() * qty;
        }

        DecimalFormat df = new DecimalFormat("#0.00");
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        subtotalAmt.setText("$" + df.format(subtotal));
        recyclerView.setItemViewCacheSize(orderList.size());
    }

        @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationActivity) {
            mListener = (CatalogueFragment.OnFragmentInteractionListener) context;
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
