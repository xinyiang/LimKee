package com.limkee1.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.limkee1.R;
import com.limkee1.entity.Customer;

public class NonPaymentFragment extends Fragment {
    private NonPaymentFragment.OnFragmentInteractionListener mListener;
    private View view;
    private Customer customer;
    private  String isEnglish;

    public NonPaymentFragment(){}

    public static NonPaymentFragment newInstance() {
        NonPaymentFragment fragment = new NonPaymentFragment();
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

        if (isEnglish.equals("Yes")){
            ((NonPaymentActivity)getActivity()).setActionBarTitle("Place Order");
        } else {
            ((NonPaymentActivity)getActivity()).setActionBarTitle("下单");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_non_payment, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NonPaymentFragment.OnFragmentInteractionListener) {
            mListener = (NonPaymentFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
