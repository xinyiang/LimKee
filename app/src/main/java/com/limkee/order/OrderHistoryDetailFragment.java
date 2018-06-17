package com.limkee.order;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limkee.R;
import com.limkee.entity.Customer;

import io.reactivex.disposables.CompositeDisposable;


public class OrderHistoryDetailFragment extends Fragment {
    private OrderHistoryDetailFragment.OnFragmentInteractionListener mListener;
    private View view;
    CompositeDisposable compositeDisposable;
    private String orderID;
    private Customer customer;

    public OrderHistoryDetailFragment() {
        // Required empty public constructor
    }

    public static OrderHistoryDetailFragment newInstance() {
        OrderHistoryDetailFragment fragment = new OrderHistoryDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((OrderHistoryDetailActivity) getActivity()).setActionBarTitle("Order history details");

        Bundle bundle = getArguments();
        //customer = (Customer) savedInstanceState.getSerializable("customer");
        orderID = bundle.getString("orderID");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history_detail, container, false);
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
