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

import io.reactivex.disposables.CompositeDisposable;

public class ScanFragment extends Fragment {

    private com.limkee.payment.ScanFragment.OnFragmentInteractionListener mListener;

    CompositeDisposable compositeDisposable;
    private View view;
    private Customer customer;
    private Context context;
    private String totalPayable;
    private String isEnglish;
    private String paperBagNeeded;

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
