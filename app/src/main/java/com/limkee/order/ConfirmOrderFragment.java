package com.limkee.order;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.limkee.R;
import com.limkee.catalogue.CatalogueAdapter;
import com.limkee.catalogue.CatalogueFragment;
import com.limkee.dao.CatalogueDAO;
import com.limkee.entity.Product;
import com.limkee.navigation.NavigationActivity;
import com.limkee.payment.PaymentActivity;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


public class ConfirmOrderFragment extends Fragment {

    private ConfirmOrderFragment.OnFragmentInteractionListener mListener;
    CompositeDisposable compositeDisposable;
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Button next;
    private TextView subtotalAmt;
    private double subtotal;
    private double taxAmt;
    private double totalPayable;
    private ConfirmOrderAdapter mAdapter;
    private ArrayList<Product> orderList;

    public ConfirmOrderFragment() {
        // Required empty public constructor
    }

    public static ConfirmOrderFragment newInstance() {
        ConfirmOrderFragment cf=new ConfirmOrderFragment();
        Bundle args = new Bundle();
        cf.setArguments(args);
        return cf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActivity() instanceof ConfirmOrderActivity){
            ((ConfirmOrderActivity)getActivity()).setActionBarTitle("Confirm Order");
        }

        //  Instantiate CompositeDisposable for retrofit
        compositeDisposable = new CompositeDisposable();

        Bundle bundle = getArguments();
        orderList = bundle.getParcelableArrayList("orderList");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        //if english, set label in english language
        /*
        TextView lblSubtotal =  view.findViewById(R.id.lblSubtotalAmt);
        lblSubtotal.setText("Subtotal:");
        TextView lblTax = view.findViewById(R.id.lblTaxAmt);
        lblTax.setText("GST (7%):");
        TextView lblFinalTotal = view.findViewById(R.id.lblTotalAmt);
        lblFinalTotal.setText("Total Payable:");
        */

        recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);
        mAdapter = new ConfirmOrderAdapter(this, orderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
       // mAdapter.notifyDataSetChanged();

        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.start();

        subtotal = calculateSubtotal(orderList);
        DecimalFormat df = new DecimalFormat("#0.00");

        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        subtotalAmt.setText("$" + df.format(subtotal));

        TextView tax = view.findViewById(R.id.taxAmt);
        taxAmt = subtotal*0.07;
        tax.setText("$" + df.format(taxAmt));

        TextView totalAmt = view.findViewById(R.id.totalAmt);
        totalPayable = taxAmt + subtotal;
        totalAmt.setText("$" + df.format(totalPayable));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        next = view.findViewById(R.id.btnNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {

                //go to payment activity
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("orderList", orderList);
                bundle.putDouble("subtotal", subtotal);
                bundle.putDouble("taxAmt", taxAmt);
                bundle.putDouble("totalPayable", totalPayable);
                //add in customer details and sale details order here

                Intent intent = new Intent(view.getContext(), PaymentActivity.class);
                intent.putParcelableArrayListExtra("orderList", orderList);
                intent.putExtra("subtotal", subtotal);
                intent.putExtra("taxAmt", taxAmt);
                intent.putExtra("totalPayable", totalPayable);
                //add in customer details and sale details order here
                getActivity().startActivity(intent);
            }
        });
    }

    private double calculateSubtotal(ArrayList<Product> orderList) {
        double subtotal = 0;
        for(Product p : orderList) {
            int qty = p.getDefaultQty();
            subtotal += p.getUnitPrice() * qty;
        }
        return subtotal;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
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
