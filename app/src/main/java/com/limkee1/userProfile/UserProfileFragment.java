package com.limkee1.userProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.entity.Customer;
import com.limkee1.login.LogoutActivity;
import com.limkee1.navigation.NavigationActivity;

import io.reactivex.disposables.CompositeDisposable;


public class UserProfileFragment extends Fragment {
    private UserProfileFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    private View view;
    private Customer customer;
    private String isEnglish;

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
        ((NavigationActivity)getActivity()).setActionBarTitle("用户资料");
        compositeDisposable = new CompositeDisposable();

        Bundle bundle = getArguments();
        customer = bundle.getParcelable("customer");
        isEnglish = bundle.getString("language");
        if (isEnglish.equals("Yes")) {
            ((NavigationActivity)getActivity()).setActionBarTitle("User Profile");
        } else {
            ((NavigationActivity)getActivity()).setActionBarTitle("用户资料");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Button logout = view.findViewById(R.id.logout_btn);
        TextView name, phone, phone2, company, companyCode,  address;

        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        company = view.findViewById(R.id.companyName);
        companyCode = view.findViewById(R.id.companyCode);
        address = view.findViewById(R.id.address);
        phone2 = view.findViewById(R.id.phone2);

        if (isEnglish.equals("Yes")){
            TextView lbl_companyName, lbl_companyCode, lbl_name, lbl_phone, lbl_address;
            //change label
            lbl_companyName = view.findViewById(R.id.lbl_companyName);
            lbl_companyCode = view.findViewById(R.id.lbl_companyCode);
            lbl_name = view.findViewById(R.id.lbl_name);
            lbl_phone = view.findViewById(R.id.lbl_phone);
            lbl_address = view.findViewById(R.id.lbl_address);

            lbl_companyName.setText("Company Name");
            lbl_companyCode.setText("Username");
            lbl_name.setText("Name");
            lbl_phone.setText("Contact Number");
            lbl_address.setText("Address");
        }

        name.setText(customer.getDebtorName());
        phone.setText(customer.getDeliveryContact());
        company.setText(customer.getCompanyName());
        companyCode.setText(customer.getCompanyCode());

        String contact2 = "";
        if (customer.getDeliveryContact2() == null || customer.getDeliveryContact2().length() == 0){
            contact2 = "";
        } else {
            contact2 = customer.getDeliveryContact2();
        }
        phone2.setText(contact2);

        String address2 = "";
        String address3 = "";
        String address4 = "";

        if (customer.getDeliverAddr2() == null  || customer.getDeliverAddr2().length() == 0){
            address2 = "";
        } else {
            address2 = customer.getDeliverAddr2();
        }

        if (customer.getDeliverAddr3() == null || customer.getDeliverAddr3().length() == 0){
            address3 = "";
        } else {
            address3 = customer.getDeliverAddr3();
        }


        if (customer.getDeliverAddr4() == null || customer.getDeliverAddr4().length() == 0){
            address4 = "";
        } else {
            address4 = customer.getDeliverAddr4();
        }

        address.setText(customer.getDeliverAddr1() + " " + address2 + " " + address3 + " " + address4);

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