package com.limkee.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
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
import com.limkee.dao.OrderDAO;
import com.limkee.entity.Customer;
import com.limkee.entity.Order;
import com.limkee.order.CancelledOrderAdapter;
import com.limkee.order.CancelledOrderFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TotalSalesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TotalSalesFragment.OnFragmentInteractionListener mListener;
    private View view;
    private Customer customer;
    public static Retrofit retrofit;
    private  String isEnglish;
    private Spinner spinner;
    static TotalSalesFragment fragment;
    private static final String[] years = {"Year","2016","2017","2018"};
    private String selectedYear = "2018";
    private ArrayList<String> custmonth = new ArrayList<>();
    private ArrayList<String> othermonth = new ArrayList<>();
    private ArrayList<Float> amounts = new ArrayList<>();
    private ArrayList<Float> avgSales = new ArrayList<>();


    public TotalSalesFragment(){}

    public static TotalSalesFragment newInstance() {
        fragment = new TotalSalesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_total_sales, container, false);
        spinner = (Spinner)view.findViewById(R.id.spinner1);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(fragment);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedYear = arg0.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        doGetCustomerSales(customer.getCompanyCode(), selectedYear);

        doGetAverageSales(selectedYear);

        /*
        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.chart);
        BarDataSet set1;
        set1 = new BarDataSet(getDataSets(), "The year 2017");
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
*/
        return view;
    }
    public void showChart(ArrayList<String> month, ArrayList<Float> amounts) {
        HorizontalBarChart chart = view.findViewById(R.id.chart);
        chart.setFitBars(true);

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        for (int i = 0; i< amounts.size() ; i++){
            float amt = amounts.get(i);
            System.out.println("Float amount is $" + amt);
            entries.add(new BarEntry(amt,i));
        }

        BarDataSet set1 = new BarDataSet(getDataSet(amounts), "Average sales");
        set1.setColors(Color.parseColor("#F78B5D"));
        set1.setValueTextSize(12f);

        BarData data = new BarData(set1);
        data.setValueFormatter(new ValueFormatter());
        IAxisValueFormatter axisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "" + ((int) value);
            }
        };

        YAxis left = chart.getAxisLeft();
        left.setValueFormatter(axisFormatter);
        left.setGranularity(1f);
        //left.setSpaceBottom(0f);
        left.setTextSize(15f);
        left.setAxisMinimum(0f);

        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawGridLines(false);

        // X-axis labels
        String[] values = month.toArray(new String[month.size()]);
        System.out.println("Items are " + values[0]);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(15f);
        xAxis.setAxisMaximum(amounts.size() - 0.5f);
        xAxis.setAxisMinimum(0.5f);

        chart.setData(data);

        Description description = new Description();
        description.setText("");
        description.setTextSize(15);
        chart.setDescription(description);

        chart.getLegend().setEnabled(true);
        chart.getLegend().setTextSize(15f);
        chart.animateY(1000);
        chart.invalidate();

        chart.setVisibleYRangeMaximum(300, YAxis.AxisDependency.LEFT);
        chart.setVisibleXRangeMaximum(5);
        chart.moveViewTo(amounts.size() - 1,0, YAxis.AxisDependency.LEFT);
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (mValues.length == 0) {
                return "";
            } else {
                System.out.println((int) value + " checking");
                if ((int) value < mValues.length) {
                    return mValues[(int) value];
                }
                return "";
            }
        }
    }
    private ArrayList<BarEntry> getDataSet(ArrayList<Float> floats) {
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        for (int i = 0; i < floats.size(); i++) {
            BarEntry v1e1 = new BarEntry(i, floats.get(i));
            valueSet1.add(v1e1);
        }

        return valueSet1;
    }

    /*
    private ArrayList<BarEntry> getDataSets() {
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
    */

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isEnglish.equals("Yes")) {
        } else {
        }
    }

    private void doGetCustomerSales(String companyCode, String selectedYear) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<LinkedHashMap<String,Double>> call = service.getFilteredCustomerSales(companyCode, selectedYear);
        call.enqueue(new Callback<LinkedHashMap<String,Double>>() {

            @Override
            public void onResponse(Call<LinkedHashMap<String,Double>> call, Response<LinkedHashMap<String,Double>> response) {
                LinkedHashMap<String,Double> data = response.body();

                if (data.size() == 0) {
                    if (isEnglish.equals("Yes")) {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        lbl_noOrders.setText("No Sales");
                        */
                    } else {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        lbl_noOrders.setText("没有");
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        */
                    }
                } else {
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String mth = (String)entry.getKey();
                        double amt = data.get(mth);
                        System.out.println("month " + mth + " for customer have sales amt of $ " + amt);
                        custmonth.add(mth);
                        amounts.add((float) amt);
                    }
                   // showChart(custmonth, amounts);
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap<String,Double>> call, Throwable t) {
                System.out.println("error " + t.getMessage());
            }
        });

    }

    private void doGetAverageSales(String selectedYear) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<LinkedHashMap<String, Double>> call = service.getAverageSales(selectedYear);
        call.enqueue(new Callback<LinkedHashMap<String, Double>>() {

            @Override
            public void onResponse(Call<LinkedHashMap<String, Double>> call, Response<LinkedHashMap<String, Double>> response) {
                LinkedHashMap<String, Double> data = response.body();

                if (data.size() == 0) {
                    if (isEnglish.equals("Yes")) {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        lbl_noOrders.setText("No Sales");
                        */
                    } else {
                        /*
                        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
                        lbl_noOrders.setText("没有");
                        view.findViewById(R.id.lbl_noOrders).setVisibility(View.VISIBLE);
                        */

                    }
                } else {
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String mth = (String) entry.getKey();
                        double amt = data.get(mth);
                        System.out.println("month " + mth + " average sales amt is $ " + amt);
                        othermonth.add(mth);
                        avgSales.add((float) amt);
                    }

                    showChart(othermonth,avgSales);
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap<String, Double>> call, Throwable t) {
                System.out.println("error : " + t.getMessage());
            }
        });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CancelledOrderFragment.OnFragmentInteractionListener) {
            mListener = (TotalSalesFragment.OnFragmentInteractionListener) context;
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