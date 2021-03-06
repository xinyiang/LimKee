package com.limkee1.wallet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import com.limkee1.R;
import com.limkee1.dao.WalletDAO;
import com.limkee1.entity.OrderDetails;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.entity.Customer;
import com.limkee1.navigation.NavigationActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalletFragment extends Fragment {
    private WalletFragment.OnFragmentInteractionListener mListener;
    private static WalletFragment fragment;
    private String isEnglish;
    private Customer customer;
    public static View view;
    public static Retrofit retrofit;
    public static RecyclerView recyclerView;
    public static TextView walletAmt;
    public static TextView lbl_noOrders;
    boolean hasInternet;

    private WalletAdapter mAdapter;

    public WalletFragment() {
    }

    public static WalletFragment newInstance() {
        fragment = new WalletFragment();
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

        if (isEnglish.equals("Yes")){
            ((NavigationActivity)getActivity()).setActionBarTitle("Wallet");
        } else {
            ((NavigationActivity)getActivity()).setActionBarTitle("钱包");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallet, container, false);
        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
        CardView cv = view.findViewById(R.id.cardDetails);

        hasInternet = isNetworkAvailable();
        if (!hasInternet) {
            lbl_noOrders.setVisibility(View.VISIBLE);
            cv.setVisibility(View.INVISIBLE);

            if (isEnglish.equals("Yes")) {
                lbl_noOrders.setText("No internet connection");
            } else {
                lbl_noOrders.setText("没有网络");
            }
        } else {

            doGetWalletAmt(customer.getDebtorCode());

            recyclerView = view.findViewById(R.id.recyclerView);
            walletAmt = view.findViewById(R.id.walletAmt);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

            doGetTransactionHistory(customer.getDebtorCode());
            mAdapter = new WalletAdapter(this, customer, isEnglish, WalletDAO.refundTransaction);

            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(mAdapter);
        }
        return view;
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
                walletAmt.setText("$" + df.format(data));
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void doGetTransactionHistory(String customerCode) {
        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<ArrayList<OrderDetails>> call = service.getTransaction(customerCode);
        call.enqueue(new Callback<ArrayList<OrderDetails>>() {

            @Override
            public void onResponse(Call<ArrayList<OrderDetails>> call, Response<ArrayList<OrderDetails>> response) {
                ArrayList<OrderDetails> data = response.body();
                WalletDAO.refundTransaction = data;
                mAdapter.update(WalletDAO.refundTransaction);
                if (data == null || data.size() == 0) {
                    if (isEnglish.equals("Yes")) {
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        lbl_noOrders.setText("No Transaction History");
                    } else {
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        lbl_noOrders.setText("没有历史");
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderDetails>> call, Throwable t) {
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
        if (context instanceof NavigationActivity) {
            mListener = (WalletFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener in wallet");
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
