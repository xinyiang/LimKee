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
import android.widget.CheckBox;
import android.widget.Spinner;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.entity.Customer;
import com.limkee.order.CancelledOrderFragment;
import com.stripe.android.model.Source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
    private Spinner ddlYear;
    static TotalSalesFragment fragment;
    private static String[] years;
    private String selectedYear = "";
    private ArrayList<String> custmonth = new ArrayList<>();
    private ArrayList<String> othermonth = new ArrayList<>();
    private ArrayList<Float> amounts = new ArrayList<>();
    private ArrayList<Float> avgSales = new ArrayList<>();
    private HashMap<String, Integer> monthConvert = new HashMap<>();
    private String systemYear;

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

        if (isEnglish.equals("Yes")){
            years = new String[]{"Year","2016","2017","2018"};
        } else {
            years = new String []{"年","2016","2017","2018"};
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd hh:mm:ss");
            String today = sdf.format(new Date());
            systemYear = today.substring(0, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        monthConvert.put("Dec",12);
        monthConvert.put("Nov",11);
        monthConvert.put("Oct",10);
        monthConvert.put("Sep",9);
        monthConvert.put("Aug",8);
        monthConvert.put("Jul",7);
        monthConvert.put("Jun",6);
        monthConvert.put("May",5);
        monthConvert.put("Apr",4);
        monthConvert.put("Mar",3);
        monthConvert.put("Feb",2);
        monthConvert.put("Jan",1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_total_sales, container, false);
        ddlYear = (Spinner)view.findViewById(R.id.ddl_year);

        boolean isChecked = ((CheckBox) view.findViewById(R.id.checkBox2)).isChecked();
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddlYear.setAdapter(adapter);
        ddlYear.setOnItemSelectedListener(fragment);

        for (int i = 1; i < years.length; i++) {
            if (ddlYear.getItemAtPosition(i).equals(systemYear)) {
                ddlYear.setSelection(i);
                selectedYear =  ddlYear.getItemAtPosition(i).toString();
                break;
            }
        }
        doGetCustomerSales(customer.getCompanyCode(), selectedYear);
        System.out.println("selected year is for sales " + selectedYear);
        ddlYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedYear = arg0.getItemAtPosition(position).toString();
                if (!isChecked) {
                    doGetAverageSales(selectedYear);
                }
                doGetCustomerSales(customer.getCompanyCode(), selectedYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    public void showChart(ArrayList<String> custmonth, ArrayList<Float> amounts, String color) {
        HorizontalBarChart chart = view.findViewById(R.id.chart);
        try {
            //chart.setScaleEnabled(false);
            chart.setDoubleTapToZoomEnabled(false);
            //chart.setFitBars(false);
            BarDataSet set1 = new BarDataSet(getDataSet(amounts), "Total amount spent");
            set1.setColors(Color.parseColor(color));
            set1.setValueTextSize(15f);

            BarData data = new BarData(set1);
            data.setValueFormatter(new ValueFormatter());
            IAxisValueFormatter axisFormatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return "" + ((int) value);
                }
            };
            data.setBarWidth(0.5f);

            YAxis left = chart.getAxisLeft();
            left.setValueFormatter(axisFormatter);
            left.setGranularity(1f);
            left.setTextSize(15f);
            left.setAxisMinimum(0f);

            YAxis right = chart.getAxisRight();
            right.setDrawLabels(false);
            right.setDrawGridLines(false);

            // X-axis labels
            String[] values = custmonth.toArray(new String[custmonth.size()]);
            System.out.println("Items are " + values[0]);
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setTextSize(15f);
            xAxis.setAxisMaximum(amounts.size() - 0.5f);
            xAxis.setAxisMinimum(-0.5f);
            xAxis.setLabelRotationAngle(-15);

            chart.setData(data);

            Description description = new Description();
            description.setText("");
            description.setTextSize(15);
            chart.setDescription(description);

            chart.getLegend().setEnabled(true);
            chart.getLegend().setTextSize(15f);
            chart.animateY(1000);
            chart.invalidate();

            //chart.setVisibleYRangeMaximum(300, YAxis.AxisDependency.LEFT);
            chart.setVisibleXRangeMaximum(5);
            chart.setVisibleXRangeMinimum(5);
            chart.moveViewTo(amounts.size() - 1, 0, YAxis.AxisDependency.LEFT);
        } catch (Exception e){
            chart.setData(null);
            chart.invalidate();
        }
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

                custmonth = new ArrayList<>();
                amounts = new ArrayList<>();

                ArrayList<String> sortedMonths = new ArrayList<>();
                ArrayList<Float> sortedAmounts = new ArrayList<>();

                if (data.size() == 0) {
                    showChart(custmonth, amounts, "#F78B5D");
                } else {
                    int i = 0;
                    Object[][] results = new Object[data.size()][2];
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String mth = (String)entry.getKey();
                        double amt = data.get(mth);
                        System.out.println("month " + mth + " for customer have sales amt of $ " + amt);
                        custmonth.add(mth);
                        amounts.add((float) amt);

                        results[i][0] = mth;
                        results[i][1] = amt;
                        i++;
                    }

                    Arrays.sort(results, new Comparator<Object[]>() {
                        public int compare(Object[] o1, Object[] o2) {
                            Integer i1 = (Integer) (monthConvert.get(o1[0]));
                            Integer i2 = (Integer) (monthConvert.get(o2[0]));
                            if (i1 != null && i2 != null) {
                                if (i2 > i1) {
                                    return -1;
                                }else{
                                    return 1;
                                }
                            }else{
                                return 0;
                            }
                        }
                    });

                    try {
                        for (int j = 0; j < results.length; j++) {
                            float quantity = Float.valueOf(results[j][0] + "");
                            String sortedNames = "" + results[j][1];
                            sortedMonths.add(sortedNames);
                            sortedAmounts.add(quantity);
                            showChart(sortedMonths,sortedAmounts, "#F78B5D");
                        }
                    }catch (Exception e){}
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

                othermonth = new ArrayList<>();
                avgSales = new ArrayList<>();

                ArrayList<String> sortedMonths = new ArrayList<>();
                ArrayList<Float> sortedAmounts = new ArrayList<>();

                if (data.size() == 0) {
                    showChart(othermonth, avgSales, "#A0C25A");
                } else {
                    int i = 0;
                    Object[][] results = new Object[data.size()][2];
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String mth = (String) entry.getKey();
                        double amt = data.get(mth);
                        System.out.println("month " + mth + " average sales amt is $ " + amt);
                        othermonth.add(mth);
                        avgSales.add((float) amt);
                        results[i][0] = mth;
                        results[i][1] = amt;
                        i++;
                    }

                    Arrays.sort(results, new Comparator<Object[]>() {
                        public int compare(Object[] o1, Object[] o2) {
                            Integer i1 = (Integer) (monthConvert.get(o1[0]));
                            Integer i2 = (Integer) (monthConvert.get(o2[0]));
                            if (i1 != null && i2 != null) {
                                if (i2 > i1) {
                                    return -1;
                                }else{
                                    return 1;
                                }
                            }else{
                                return 0;
                            }
                        }
                    });

                    try {
                        for (int j = 0; j < results.length; j++) {
                            float quantity = Float.valueOf(results[j][0] + "");
                            String sortedNames = "" + results[j][1];
                            sortedMonths.add(sortedNames);
                            sortedAmounts.add(quantity);
                            showChart(sortedMonths,sortedAmounts, "#F78B5D");
                        }
                    }catch (Exception e){}

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