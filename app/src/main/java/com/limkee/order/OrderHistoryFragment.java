package com.limkee.order;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limkee.R;
import com.limkee.dao.OrderDAO;
import com.limkee.entity.Customer;
import com.limkee.navigation.NavigationActivity;

import io.reactivex.disposables.CompositeDisposable;


public class OrderHistoryFragment extends Fragment {

    private OrderHistoryFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    public static Bundle myBundle = new Bundle();
    private OrderHistoryAdapter mAdapter;
    private View view;
    private RecyclerView recyclerView;
    private ConstraintLayout coordinatorLayout;
    private Customer customer;

    public OrderHistoryFragment(){}

    public static OrderHistoryFragment newInstance() {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NavigationActivity)getActivity()).setActionBarTitle("Order History");


        compositeDisposable = new CompositeDisposable();
        Bundle bundle = getArguments();
        //customer = (Customer) savedInstanceState.getSerializable("customer");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_history, container, false);


        recyclerView = view.findViewById(R.id.orderHistoryRecyclerView);
        Bundle bundle = getArguments();
        //(Serializable) customer
        //customer = bundle.getParcelableArrayList("customer");
        doGetOrderHistory();

        return view;
    }

    private void doGetOrderHistory() {
        recyclerView = (RecyclerView) view.findViewById(R.id.orderHistoryRecyclerView);
        mAdapter = new OrderHistoryAdapter(this, OrderDAO.historyOrdersList,customer);

        coordinatorLayout = view.findViewById(com.limkee.R.id.constraint_layout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationActivity) {
            mListener = (OrderHistoryFragment.OnFragmentInteractionListener) context;
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
