package com.limkee.catalogue;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.dao.OrderDAO;
import com.limkee.entity.Customer;
import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;
import com.limkee.entity.Product;
import com.squareup.picasso.Picasso;
import com.stripe.android.model.Source;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductDetailsFragment extends Fragment {
    private ProductDetailsFragment.OnFragmentInteractionListener mListener;
    private View view;
    private ProgressBar progressBar;
    private Product product;
    private String isEnglish;


    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    public static ProductDetailsFragment newInstance() {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        product = bundle.getParcelable("product");
        isEnglish = bundle.getString("language");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")){
            ((ProductDetailsActivity) getActivity()).setActionBarTitle("Product Details");
        } else {
            ((ProductDetailsActivity) getActivity()).setActionBarTitle("物品详情");
        }

        Bundle bundle = getArguments();
        product = bundle.getParcelable("product");
        isEnglish = bundle.getString("language");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_product_details, container, false);
        /*
        progressBar = view.findViewById(R.id.progressBar);

        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);

            }
        }.start();

         */

        //if english, change label to english
        ImageView image;
        TextView productName, price;
        image = view.findViewById(R.id.productImg);
        productName = (TextView) view.findViewById(R.id.pname);
        price = (TextView) view.findViewById(R.id.price);

        System.out.println("Product image is " + product.getImageUrl());

        Picasso.with(getContext()).load(product.getImageUrl())
                .error(R.mipmap.launchicon)
                .resize(310,250)
                .onlyScaleDown()
                .into(image);
        //.fit();
        //.placeholder(R.mipmap.launchicon)

        DecimalFormat df = new DecimalFormat("#0.00");

        if (isEnglish.equals("Yes")){
            productName.setText(product.getDescription());
            if (product.getItemCode().equals("CS")){
                price.setText("$" + df.format(product.getUnitPrice()) + "/bottle");
            } else {
                price.setText("$" + df.format(product.getUnitPrice()) + "/piece");
            }
        } else {
            productName.setText(product.getDescription2());
            price.setText("$" + df.format(product.getUnitPrice()) + "/" + product.getUom());
        }

        return view;
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
