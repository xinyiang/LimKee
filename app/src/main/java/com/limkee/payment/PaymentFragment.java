package com.limkee.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limkee.R;
import com.limkee.entity.Customer;
import com.limkee.order.ConfirmOrderActivity;
import com.stripe.android.view.CardInputWidget;

import io.reactivex.disposables.CompositeDisposable;

public class PaymentFragment extends Fragment {
    private com.limkee.payment.PaymentFragment.OnFragmentInteractionListener mListener;

    CompositeDisposable compositeDisposable;
    private View view;
    private Customer customer;
    private CardInputWidget mCardInputWidget;
    private Context context;
    private String totalPayable;
    private String isEnglish;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PaymentActivity)getActivity()).setActionBarTitle("付款");
        compositeDisposable = new CompositeDisposable();

        Bundle bundle = getArguments();
        customer = bundle.getParcelable("customer");
        totalPayable = String.valueOf(bundle.getDouble("totalPayable"));
        isEnglish = bundle.getString("language");

        if (getActivity() instanceof PaymentActivity) {
            if (isEnglish.equals("Yes")) {
                ((PaymentActivity) getActivity()).setActionBarTitle("Payment");
            } else {
                ((PaymentActivity) getActivity()).setActionBarTitle("付款");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment, container, false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (PaymentFragment.OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    }
