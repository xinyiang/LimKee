package com.limkee1.catalogue;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.entity.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

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

        if (product.getItemCode().equals("CS")){
            Picasso.with(getContext()).load(product.getImageUrl())
                    .error(R.mipmap.launchicon)
                    .resize(300,360)
                    .onlyScaleDown()
                    .into(image);
        } else {
            Picasso.with(getContext()).load(product.getImageUrl())
                    .error(R.mipmap.launchicon)
                    .resize(330,230)
                    .onlyScaleDown()
                    .into(image);
        }

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
