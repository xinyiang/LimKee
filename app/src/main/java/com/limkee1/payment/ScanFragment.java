package com.limkee1.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.limkee1.R;
import com.limkee1.entity.Customer;

import io.reactivex.disposables.CompositeDisposable;

public class ScanFragment extends Fragment {
    private com.limkee1.payment.ScanFragment.OnFragmentInteractionListener mListener;

    CompositeDisposable compositeDisposable;
    private View view;
    private Customer customer;
    private Context context;
    private String totalPayable;
    private String isEnglish;
    private String paperBagNeeded;
    private String cardNum;
    private EditText cardNumber;

    public ScanFragment() {
    }

    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ScanActivity)getActivity()).setActionBarTitle("付款");
        compositeDisposable = new CompositeDisposable();

        Bundle bundle = getArguments();
        isEnglish = bundle.getString("language");

        if (getActivity() instanceof ScanActivity) {
            if (isEnglish.equals("Yes")) {
                ((ScanActivity) getActivity()).setActionBarTitle("Payment");
            } else {
                ((ScanActivity) getActivity()).setActionBarTitle("付款");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scan, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        cardNumber = (EditText) getActivity().findViewById(R.id.cardNumber);
        cardNum = bundle.getString("cardNumber");
        cardNumber.setText(cardNum);

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
            mListener = (ScanFragment.OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
