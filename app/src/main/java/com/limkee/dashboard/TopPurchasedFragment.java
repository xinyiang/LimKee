package com.limkee.dashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.entity.Customer;
import com.limkee.order.CancelledOrderFragment;
import java.util.Iterator;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TopPurchasedFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private TopPurchasedFragment.OnFragmentInteractionListener mListener;
    private View view;
    public static Retrofit retrofit;
    private Customer customer;
    private String month;
    private String language;
    private  String isEnglish;
    static TopPurchasedFragment fragment;
    private Spinner spinner1;
    private static final String[] paths1 = {"3 Items", "5 Items", "10 Items"};
    private Spinner spinner2;
    private static final String[] paths2 = {"Month", "Year"};

    public TopPurchasedFragment(){}

    public static TopPurchasedFragment newInstance() {
        fragment = new TopPurchasedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        isEnglish = bundle.getString("language");
        customer = bundle.getParcelable("customer");

        if(isEnglish.equals("Yes")){
            language = "eng";
        } else {
            language = "chi";
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_purchased, container, false);

        //get month from dropdown list value
        month = "Aug";
        doGetTopProducts(customer.getCompanyCode(), month, language);

        spinner1 = (Spinner)view.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, paths1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(fragment);

        spinner2 = (Spinner)view.findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, paths2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(fragment);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")) {
        } else {
        }
    }

    private void doGetTopProducts(String companyCode, String month, String language) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Map<String,Integer>> call = service.getTopPurchasedProducts(companyCode, month, language);
        call.enqueue(new Callback<Map<String,Integer>>() {

            @Override
            public void onResponse(Call<Map<String,Integer>> call, Response<Map<String,Integer>> response) {
                Map<String,Integer> data = response.body();
                System.out.println("There are " + data.size() + " products.");

                if (data.size() == 0) {
                    if (isEnglish.equals("Yes")) {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        lbl_noOrders.setText("No products");
                        */
                    } else {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        lbl_noOrders.setText("没有物品");
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        */
                    }
                } else {
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String itemName = (String)entry.getKey();
                        int qty = data.get(itemName);
                        System.out.println("item name " + itemName + " has qty of " + qty);
                    }

                }
            }

            @Override
            public void onFailure(Call<Map<String,Integer>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CancelledOrderFragment.OnFragmentInteractionListener) {
            mListener = (TopPurchasedFragment.OnFragmentInteractionListener) context;
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}