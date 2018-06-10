package com.limkee.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.limkee.R;
import com.limkee.entity.Product;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

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
        /*
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
        */
        CardInputWidget mCardInputWidget = (CardInputWidget) view.findViewById(R.id.card_input_widget);
        Card cardToSave = mCardInputWidget.getCard();
        cardToSave.setName("Customer Name");
        if (cardToSave == null) {
            System.out.println("Invalid Card Data");
            return;
        }
        Stripe stripe = new Stripe(getContext(), "pk_test_6pRNASCoBOKtIshFeQd4XMUh");
        stripe.createToken(
                cardToSave,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                    }
                    public void onError(Exception error) {
                        // Show localized error message
                        //Toast.makeText(getContext(),
                        //error.getLocalizedString(getContext()),
                        //Toast.LENGTH_LONG).show();
                    }
                }
        );
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