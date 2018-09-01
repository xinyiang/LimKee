package com.limkee.dashboard;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.entity.Customer;
import com.limkee.order.CancelledOrderFragment;

import java.util.ArrayList;
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
    private String language;
    private  String isEnglish;
    static TopPurchasedFragment fragment;
    private Spinner spinner1;
    private static final String[] months = {"Month","Jan", "Feb", "Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private Spinner spinner2;
    private static final String[] years = {"Year","2016","2017","2018"};
    private Spinner spinner3;
    private static final String[] items = {"Item","5 items", "All"};
    private String selectedYear = ""; //get year from selected spinner value to pass into api call
    private String selectedMonth = ""; //get month from selected spinner value to pass into api call
    private String selectedItem = "";

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

        doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language);

        spinner1 = (Spinner)view.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(fragment);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
              selectedMonth = arg0.getItemAtPosition(position).toString();
          }
           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {
           }
        });

        spinner2 = (Spinner)view.findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(fragment);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedYear = arg0.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner3 = (Spinner)view.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(fragment);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedItem = arg0.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.chart);
        BarDataSet set1;
        set1 = new BarDataSet(getDataSet(), "The year 2017");
        set1.setColors(Color.parseColor("#F78B5D"), Color.parseColor("#FCB232"), Color.parseColor("#FDD930"), Color.parseColor("#ADD137"), Color.parseColor("#A0C25A"));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        // hide Y-axis
        YAxis left = chart.getAxisLeft();
        left.setDrawLabels(false);

        // custom X-axis labels
        String[] values = new String[] { "1 star", "2 stars", "3 stars", "4 stars", "5 stars"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        chart.setData(data);

        // custom description
        Description description = new Description();
        description.setText("Rating");
        chart.setDescription(description);

        // hide legend
        chart.getLegend().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();

        return view;
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }

    }

    private ArrayList<BarEntry> getDataSet() {
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e2 = new BarEntry(1, 4341f);
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(2, 3121f);
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(3, 5521f);
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(4, 10421f);
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(5, 27934f);
        valueSet1.add(v1e6);

        return valueSet1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void doGetTopProducts(String companyCode, String selectedMonth, String selectedYear, String language) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Map<String,Integer>> call = service.getTopPurchasedProducts(companyCode, selectedMonth, selectedYear, language);
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
                break;
            case 1:
                break;
            case 2:
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