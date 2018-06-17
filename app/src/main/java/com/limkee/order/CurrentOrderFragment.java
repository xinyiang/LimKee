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

public class CurrentOrderFragment extends Fragment {

    private CurrentOrderFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    public static Bundle myBundle = new Bundle();
    private CurrentOrderAdapter mAdapter;
    private View view;
    private RecyclerView recyclerView;
    private ConstraintLayout coordinatorLayout;
    private Customer customer;

    public CurrentOrderFragment(){}

    public static CurrentOrderFragment newInstance() {
        CurrentOrderFragment fragment = new CurrentOrderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NavigationActivity)getActivity()).setActionBarTitle("My Orders");


        compositeDisposable = new CompositeDisposable();
        Bundle bundle = getArguments();
        //customer = (Customer) savedInstanceState.getSerializable("customer");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_order, container, false);


        recyclerView = view.findViewById(R.id.currentOrderRecyclerView);
        Bundle bundle = getArguments();
        //(Serializable) customer
        //customer = bundle.getParcelableArrayList("customer");
        doGetCurrentOrders();

        return view;
    }

    private void doGetCurrentOrders() {
        recyclerView = (RecyclerView) view.findViewById(R.id.currentOrderRecyclerView);
        mAdapter = new CurrentOrderAdapter(this, OrderDAO.currentOrdersList, customer);

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
            mListener = (CurrentOrderFragment.OnFragmentInteractionListener) context;
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
