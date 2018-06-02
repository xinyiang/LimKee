package com.limkee.payment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.limkee.R;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.dao.CatalogueDAO;
import com.limkee.entity.Product;
import com.limkee.navigation.NavigationActivity;
import com.limkee.order.ConfirmOrderFragment;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


public class PaymentFragment extends Fragment {

    private PaymentFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    private ProgressBar progressBar;
    private View view;
    private ArrayList<Product> orderList;


    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment pf = new PaymentFragment();
        Bundle args = new Bundle();
        pf.setArguments(args);
        return pf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() instanceof PaymentActivity){
            ((PaymentActivity)getActivity()).setActionBarTitle("Make Payment");
        }

        //  Instantiate CompositeDisposable for retrofit
        compositeDisposable = new CompositeDisposable();

        Bundle bundle = getArguments();
        orderList = bundle.getParcelableArrayList("orderList");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        }.start();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button next = view.findViewById(R.id.btnNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {

               //add in stripe code

                //add in insert SQL code

                //reset order list to be the same as catalogue by default
                CatalogueDAO.order_list = CatalogueDAO.catalogue_list;

                Toast.makeText(view.getContext(), "Payment Successful! Please view your orders under My Orders.", Toast.LENGTH_SHORT).show();
                //redirect

            }
        });
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
}
