package com.limkee.userProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limkee.R;
import com.limkee.catalogue.CatalogueAdapter;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.dao.CatalogueDAO;
import com.limkee.entity.Customer;
import com.limkee.login.LoginActivity;
import com.limkee.login.LogoutActivity;
import com.limkee.navigation.NavigationActivity;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    private UserProfileFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    private View view;
    private Customer customer;
    private ArrayList<Customer> custList;

    public UserProfileFragment(){}

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((NavigationActivity)getActivity()).setActionBarTitle("User Profile");
        compositeDisposable = new CompositeDisposable();

        /*
        Bundle bundle = getArguments();
        custList = bundle.getParcelable("customer");
        customer = custList.get(0);
        Bundle myBundle = new Bundle();
        myBundle.putParcelableArrayList("customer", custList);
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Button logout = view.findViewById(R.id.logout_btn);
        TextView name, phone,company, companyCode,  address;

        name = view.findViewById(R.id.companyName);
        phone = view.findViewById(R.id.phone);
        company = view.findViewById(R.id.companyName);
        companyCode = view.findViewById(R.id.companyCode);
        address = view.findViewById(R.id.address);
        /*
        name.setText(customer.getDebtorName());
        phone.setText(customer.getDeliverFax1());
        company.setText(customer.getCompanyName());
        companyCode.setText(customer.getCompanyCode());
        address.setText(customer.getDeliverAddr1() + " " + customer.getDeliverAddr2() + " " + customer.getDeliverAddr3() + " " + customer.getDeliverAddr1());
        */
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),LogoutActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationActivity) {
            mListener = (UserProfileFragment.OnFragmentInteractionListener) context;
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