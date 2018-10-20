package com.limkee1.payment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.limkee1.R;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.stripe.android.view.CardInputWidget;
import java.util.ArrayList;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import io.reactivex.disposables.CompositeDisposable;

public class PaymentFragment extends Fragment {
    private com.limkee1.payment.PaymentFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    private View view;
    private Customer customer;
    private CardInputWidget mCardInputWidget;
    private Context context;
    private String totalPayable;
    private String isEnglish;
    private int paperBagNeeded;
    private String deliveryDate;
    private ArrayList<Product> orderList;
    private  double totalAmount;
    private  double walletDeduction;
    static PaymentFragment fragment;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance() {
        fragment = new PaymentFragment();
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
        paperBagNeeded = bundle.getInt("paperBagRequired");
        deliveryDate = bundle.getString("deliveryDate");
        orderList = bundle.getParcelableArrayList("orderList");
        totalAmount = bundle.getDouble("totalAmount");
        walletDeduction = bundle.getDouble("walletDeduction");

        if (getActivity() instanceof PaymentActivity) {
            if (isEnglish.equals("Yes")) {
                ((PaymentActivity) getActivity()).setActionBarTitle("Payment");
            } else {
                ((PaymentActivity) getActivity()).setActionBarTitle("付款");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                Intent intent = new Intent(getActivity().getBaseContext(), ScanActivity.class);
                intent.putExtra("totalPayable", totalPayable);
                intent.putExtra("walletDeduction", walletDeduction);
                intent.putExtra("totalAmount", totalAmount);
                intent.putExtra("cardnum", ""+ scanResult.getFormattedCardNumber());
                intent.putExtra("language", isEnglish);
                intent.putExtra("paperBagNeeded", paperBagNeeded);
                intent.putExtra("customer", customer);
                intent.putExtra("deliveryDate", deliveryDate);
                intent.putParcelableArrayListExtra("orderList", orderList);
                getActivity().startActivity(intent);
            } else{
                getActivity().getFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment, container, false);
        Button scan = view.findViewById(R.id.onScanPress);
        scan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent scanIntent = new Intent(getActivity(), CardIOActivity.class);
                startActivityForResult(scanIntent, 1);
            }
        });
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
