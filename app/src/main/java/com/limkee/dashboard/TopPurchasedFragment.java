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
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private String isEnglish;
    static TopPurchasedFragment fragment;
    private Spinner spinner1;
    private static final String[] months = {"Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private Spinner spinner2;
    private static final String[] years = {"Year", "2016", "2017", "2018"};
    private Spinner spinner3;
    private static final String[] items = {"Item","5 items", "All"};
    private String selectedYear = ""; //get year from selected spinner value to pass into api call
    private String selectedMonth = ""; //get month from selected spinner value to pass into api call
    private String selectedItem = "";
    private ArrayList<String> itemNames = new ArrayList<>();
    private ArrayList<Float> amounts = new ArrayList<>();
    private TextView lbl_noOrders;

    public TopPurchasedFragment() {
    }

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

        if (isEnglish.equals("Yes")) {
            language = "eng";
        } else {
            language = "chi";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_purchased, container, false);

        doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language, selectedItem);
        
        spinner1 = (Spinner)view.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(fragment);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedMonth = arg0.getItemAtPosition(position).toString();
                doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language, selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner2 = (Spinner) view.findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(fragment);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedYear = arg0.getItemAtPosition(position).toString();
                doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language, selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner3 = (Spinner) view.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(fragment);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedItem = arg0.getItemAtPosition(position).toString();
                doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language, selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    public void showChart(ArrayList<String> itemNames, ArrayList<Float> amounts, String selectedItem) {
        HorizontalBarChart chart = view.findViewById(R.id.chart);
        try {
            chart.setDoubleTapToZoomEnabled(false);
            //chart.setFitBars(false);
            BarDataSet set1 = new BarDataSet(getDataSet(amounts), "Total amount of each item sold");
            set1.setColors(Color.parseColor("#F78B5D"));
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
            String[] values = itemNames.toArray(new String[itemNames.size()]);
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

            chart.setVisibleXRangeMaximum(5);
            chart.setVisibleXRangeMinimum(5);
            chart.moveViewTo(amounts.size() - 1, 0, YAxis.AxisDependency.LEFT);
        }catch (Exception e){
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
    }

    private void doGetTopProducts(String companyCode, String selectedMonth, String selectedYear, String language, String selectedItem) {
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
                Map<String, Integer> data = response.body();
                System.out.println("There are " + data.size() + " products.");

                ArrayList<String> sortedItemNames = new ArrayList<>();
                ArrayList<Float> sortedAmounts = new ArrayList<>();

                if (data.size() == 0) {
                    showChart(sortedItemNames, sortedAmounts, selectedItem);
                } else {
                    int i = 0;
                    Object[][] results = new Object[data.size()][2];
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String itemName = (String) entry.getKey();
                        itemNames.add(itemName);
                        int qty = data.get(itemName);
                        amounts.add((float) qty);
                        System.out.println("item name " + itemName + " has qty of " + qty);

                        results[i][0] = qty;
                        results[i][1] = itemName;
                        i++;
                    }

                    Arrays.sort(results, new Comparator<Object[]>() {
                        public int compare(Object[] o1, Object[] o2) {
                            Integer i1 = (Integer) (o1[0]);
                            Integer i2 = (Integer) (o2[0]);
                            if (i1 != null && i2 != null) {
                                return i1.compareTo(i2);
                            }else{
                                return 0;
                            }
                        }
                    });

                    try {
                        for (int j = 0; j < results.length; j++) {
                            float quantity = Float.valueOf(results[j][0] + "");
                            String sortedNames = "" + results[j][1];
                            sortedItemNames.add(sortedNames);
                            sortedAmounts.add(quantity);
                            showChart(sortedItemNames, sortedAmounts, selectedItem);
                        }
                    }catch (Exception e){}

                    System.out.println("DATA PRINTED IS " + Arrays.deepToString(results));
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