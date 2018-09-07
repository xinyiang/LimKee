package com.limkee.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
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
    private Spinner ddlYear;
    private static String[] months;
    private Spinner ddlMonth;
    private static String[] years;
    private String selectedYear = "";
    private String selectedMonth = "";
    private ArrayList<String> itemNames = new ArrayList<>();
    private ArrayList<Float> amounts = new ArrayList<>();
    private TextView lbl_noOrders;
    private String systemYear;
    private String systemMonth;
    private String systemMonthInChinese;
    private int earliestYear;
    private int length;
    private Chart chart;

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

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd hh:mm:ss");
            String today = sdf.format(new Date());
            systemYear = today.substring(0, 4);
            int numMonth = Integer.parseInt(today.substring(5, 6));
            systemMonth = getMonth(numMonth);
            systemMonthInChinese = getChineseMonth(systemMonth);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //call api to get customer's earliest year of orders
       // getEarliestYear(customer.getDebtorCode()); //assign earliestYear variable
        earliestYear = 2017;

        if (earliestYear == 0 || earliestYear == Integer.parseInt(systemYear)){
            earliestYear = Integer.parseInt(systemYear);
            length = 0;
        } else {
            length = Integer.parseInt(systemYear) - earliestYear;
        }

        years = new String[length+2];

        if (isEnglish.equals("Yes")) {
            language = "eng";
            months = new String[]{"Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            //years = new String[]{"Year", "2016", "2017", "2018"};
            years[0] = "Year";

        } else {
            language = "chi";
            months = new String[]{"月", "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
            //years = new String[]{"年", "2016", "2017", "2018"};
            years[0] = "年";
        }

        //loop to create years
        int size = 1;
        for (int i = earliestYear; i<= Integer.parseInt(systemYear);i++){
            years[size] = Integer.toString(i);
            size ++;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_purchased, container, false);
        chart = new Chart((HorizontalBarChart)view.findViewById(R.id.chart));

        doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language);

        ddlYear = (Spinner)view.findViewById(R.id.ddl_year);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddlYear.setAdapter(adapter);
        ddlYear.setOnItemSelectedListener(fragment);

        //set selected year
        for (int i = 1; i < years.length; i++) {
            if (ddlYear.getItemAtPosition(i).equals(systemYear)) {
                ddlYear.setSelection(i);
                break;
            }
        }

        ddlYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedYear = arg0.getItemAtPosition(position).toString();
                if (selectedYear.equals("Year") || selectedYear.equals("年")){
                    chart.hide(isEnglish);
                } else {
                    doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ddlMonth = (Spinner) view.findViewById(R.id.ddl_month);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, months);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddlMonth.setAdapter(adapter2);
        ddlMonth.setOnItemSelectedListener(fragment);

        //set selected month
        for (int i = 1; i <= 12; i++) {
            if (ddlMonth.getItemAtPosition(i).equals(systemMonth) || ddlMonth.getItemAtPosition(i).equals(systemMonthInChinese)) {
                ddlMonth.setSelection(i);
                break;
            }
        }

        ddlMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedMonth = arg0.getItemAtPosition(position).toString();

                if (selectedMonth.equals("一月")){
                    selectedMonth = "Jan";
                } else if (selectedMonth.equals("二月")){
                    selectedMonth = "Feb";
                } else if (selectedMonth.equals("三月")){
                    selectedMonth = "Mar";
                } else if (selectedMonth.equals("四月")){
                    selectedMonth = "Apr";
                } else if (selectedMonth.equals("五月")){
                    selectedMonth = "May";
                } else if (selectedMonth.equals("六月")){
                    selectedMonth = "Jun";
                } else if (selectedMonth.equals("七月")){
                    selectedMonth = "Jul";
                } else if (selectedMonth.equals("八月")){
                    selectedMonth = "Aug";
                } else if (selectedMonth.equals("九月")){
                    selectedMonth = "Sep";
                } else if (selectedMonth.equals("十月")){
                    selectedMonth = "Oct";
                } else if (selectedMonth.equals("十一月")){
                    selectedMonth = "Nov";
                } else if (selectedMonth.equals("十二月")) {
                    selectedMonth = "Dec";
                } else {
                    selectedMonth = arg0.getItemAtPosition(position).toString();
                }

                if (selectedMonth.equals("Month") || selectedMonth.equals("月")){
                    chart.hide(isEnglish);
                } else {
                    doGetTopProducts(customer.getCompanyCode(), selectedMonth, selectedYear, language);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    public void showChart(ArrayList<String> itemNames, ArrayList<Float> amounts) {
        HorizontalBarChart chart = view.findViewById(R.id.chart);
        try {
            chart.setDoubleTapToZoomEnabled(false);
            //chart.setFitBars(false);
            BarDataSet set1 = new BarDataSet(getDataSet(amounts), "Quantity");
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
            Paint p = chart.getPaint(com.github.mikephil.charting.charts.Chart.PAINT_INFO);
            p.setTextSize(60);
            chart.setNoDataTextColor(R.color.colorAccent);
            if (language.equals("eng")) {
                chart.setNoDataText("No data");
            } else {
                chart.setNoDataText("没有资料");
            }
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
                Map<String, Integer> data = response.body();
                ArrayList<String> sortedItemNames = new ArrayList<>();
                ArrayList<Float> sortedAmounts = new ArrayList<>();

                if (data.size() == 0) {
                    showChart(sortedItemNames, sortedAmounts);
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
                            showChart(sortedItemNames, sortedAmounts);
                        }
                    } catch (Exception e){}
                }
            }

            @Override
            public void onFailure(Call<Map<String,Integer>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

   private void getEarliestYear(String companyCode) {

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Integer> call = service.getEarliestYear(companyCode);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int data = response.body();
                System.out.println("Year is " + data + ".");
                earliestYear = data;
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    private String getChineseMonth(String engMonth){

        String chineseMth = "";

        if (engMonth.equals("Jan")){
            chineseMth = "一月";
        } else if (engMonth.equals("Feb")){
            chineseMth = "二月";
        } else if (engMonth.equals("Mar")){
            chineseMth = "三月";
        } else if (engMonth.equals("Apr")){
            chineseMth = "四月";
        } else if (engMonth.equals("May")){
            chineseMth = "五月";
        } else if (engMonth.equals("Jun")){
            chineseMth = "六月";
        } else if (engMonth.equals("Jul")){
            chineseMth = "七月";
        } else if (engMonth.equals("Aug")){
            chineseMth = "八月";
        } else if (engMonth.equals("Sep")){
            chineseMth = "九月";
        } else if (engMonth.equals("Oct")){
            chineseMth = "十月";
        } else if (engMonth.equals("Nov")){
            chineseMth = "十一月";
        } else if (engMonth.equals("Dec")){
            chineseMth = "十二月";
        } else {
            //nothing
        }

        return  chineseMth;
    }

    private String getMonth(int numMonth){
        String engMonth = "";

        if (numMonth == 1) {
            engMonth = "Jan";
        } else if (numMonth == 2) {
            engMonth = "Feb";
        } else if (numMonth == 3) {
            engMonth = "Mar";
        } else if (numMonth == 4) {
            engMonth = "Apr";
        } else if (numMonth == 5) {
            engMonth = "May";
        } else if (numMonth == 6) {
            engMonth = "Jun";
        } else if (numMonth == 7) {
            engMonth = "Jul";
        } else if (numMonth == 8) {
            engMonth = "Aug";
        } else if (numMonth == 9) {
            engMonth = "Sep";
        } else if (numMonth == 10) {
            engMonth = "Oct";
        } else if (numMonth == 11) {
            engMonth = "Nov";
        }  else if (numMonth == 12) {
            engMonth = "Dec";
        } else{
            //nothing
        }
        return engMonth;
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