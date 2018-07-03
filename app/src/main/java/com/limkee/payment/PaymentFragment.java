package com.limkee.payment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limkee.R;
import com.limkee.entity.Customer;
import com.limkee.login.LogoutActivity;
import com.limkee.navigation.NavigationActivity;
import com.limkee.order.ConfirmOrderFragment;
import com.limkee.userProfile.UserProfileFragment;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
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
