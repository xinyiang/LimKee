package com.limkee1.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.limkee1.R;
import com.limkee1.Utility.DateUtility;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.entity.Customer;
import com.limkee1.order.CancelledOrderFragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private int numMonth;
    boolean hasInternet;

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
        earliestYear = bundle.getInt("earliestYear");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd hh:mm:ss");
            String today = sdf.format(new Date());
            systemYear = today.substring(0, 4);
            numMonth = Integer.parseInt(today.substring(5, 6));
            systemMonth = DateUtility.getMonth(numMonth);
            systemMonthInChinese = DateUtility.getChineseMonth(systemMonth);

        } catch (Exception e) {
            e.printStackTrace();
        }

        earliestYear = 2018;

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
            years[0] = "Year";

        } else {
            language = "chi";
            months = new String[]{"月", "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
            years[0] = "年";
        }

        int size = 1;
        for (int i = earliestYear; i<= Integer.parseInt(systemYear);i++){
            years[size] = Integer.toString(i);
            size ++;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_purchased, container, false);
        lbl_noOrders = view.findViewById(R.id.lbl_noOrders);
        ddlYear = (Spinner) view.findViewById(R.id.ddl_year);
        ddlMonth = (Spinner) view.findViewById(R.id.ddl_month);
        chart = new Chart((HorizontalBarChart) view.findViewById(R.id.chart));

        hasInternet = isNetworkAvailable();
        if (!hasInternet) {
            lbl_noOrders.setVisibility(View.VISIBLE);
            ddlYear.setVisibility(View.INVISIBLE);
            ddlMonth.setVisibility(View.INVISIBLE);
            chart.hideWithNoInternet();

            if (isEnglish.equals("Yes")) {
                lbl_noOrders.setText("No internet connection");
            } else {
                lbl_noOrders.setText("没有网络");
            }
        } else {

            doGetTopProducts(customer.getDebtorCode(), systemMonth, selectedYear, language);
            doGetTopProducts(customer.getDebtorCode(), systemMonth, selectedYear, language);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ddlYear.setAdapter(adapter);
            ddlYear.setOnItemSelectedListener(fragment);

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
                    if (selectedYear.equals("Year") || selectedYear.equals("年")) {
                        chart.hide(isEnglish);
                    } else {
                        doGetTopProducts(customer.getDebtorCode(), selectedMonth, selectedYear, language);
                        doGetTopProducts(customer.getDebtorCode(), selectedMonth, selectedYear, language);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, months);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ddlMonth.setAdapter(adapter2);
            ddlMonth.setOnItemSelectedListener(fragment);

            for (int i = 1; i <= 12; i++) {
                if (ddlMonth.getItemAtPosition(i).equals(systemMonth) || ddlMonth.getItemAtPosition(i).equals(systemMonthInChinese)) {
                    ddlMonth.setSelection(i);
                    break;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            String today = sdf.format(new Date());
            numMonth = Integer.parseInt(today.substring(5, 7));
            ddlMonth.setSelection(numMonth);
            ddlMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    selectedMonth = arg0.getItemAtPosition(position).toString();

                    if (selectedMonth.equals("一月")) {
                        selectedMonth = "Jan";
                    } else if (selectedMonth.equals("二月")) {
                        selectedMonth = "Feb";
                    } else if (selectedMonth.equals("三月")) {
                        selectedMonth = "Mar";
                    } else if (selectedMonth.equals("四月")) {
                        selectedMonth = "Apr";
                    } else if (selectedMonth.equals("五月")) {
                        selectedMonth = "May";
                    } else if (selectedMonth.equals("六月")) {
                        selectedMonth = "Jun";
                    } else if (selectedMonth.equals("七月")) {
                        selectedMonth = "Jul";
                    } else if (selectedMonth.equals("八月")) {
                        selectedMonth = "Aug";
                    } else if (selectedMonth.equals("九月")) {
                        selectedMonth = "Sep";
                    } else if (selectedMonth.equals("十月")) {
                        selectedMonth = "Oct";
                    } else if (selectedMonth.equals("十一月")) {
                        selectedMonth = "Nov";
                    } else if (selectedMonth.equals("十二月")) {
                        selectedMonth = "Dec";
                    } else {
                        selectedMonth = arg0.getItemAtPosition(position).toString();
                    }

                    if (selectedMonth.equals("Month") || selectedMonth.equals("月")) {
                        chart.hide(isEnglish);
                    } else {
                        doGetTopProducts(customer.getDebtorCode(), selectedMonth, selectedYear, language);
                        doGetTopProducts(customer.getDebtorCode(), selectedMonth, selectedYear, language);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
        return view;
    }

    public void showChart(ArrayList<String> itemNames, ArrayList<Float> amounts) {
        HorizontalBarChart chart = view.findViewById(R.id.chart);
        chart.fitScreen();
        try {
            chart.setDoubleTapToZoomEnabled(false);
            BarDataSet set1;
            if (isEnglish.equals("Yes")){
                 set1 = new BarDataSet(getDataSet(amounts), "Quantity");
            } else {
                set1 = new BarDataSet(getDataSet(amounts), "数量");
            }

            set1.setColors(Color.parseColor("#F78B5D"));
            set1.setValueTextSize(15f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);

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
            left.setAxisMinimum(0);

            YAxis right = chart.getAxisRight();
            right.setDrawLabels(false);
            right.setDrawGridLines(false);

            String[] values = itemNames.toArray(new String[itemNames.size()]);
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setTextSize(15f);
            xAxis.setAxisMaximum(amounts.size() - 0.5f);
            xAxis.setAxisMinimum(-0.5f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            chart.setData(data);

            Description description = new Description();
            description.setText("");
            chart.setDescription(description);
            chart.setXAxisRenderer(new MyXAxisRenderer(chart.getViewPortHandler(), xAxis, chart.getTransformer(YAxis.AxisDependency.LEFT), chart));

            chart.getLegend().setEnabled(true);
            chart.getLegend().setTextSize(17f);

            chart.animateY(1000);
            chart.setViewPortOffsets(280,50,50,100);
            chart.invalidate();

            chart.setVisibleXRangeMaximum(5);
            chart.setVisibleXRangeMinimum(5);

            chart.moveViewTo(amounts.size() - 1, 0, YAxis.AxisDependency.LEFT);
        } catch (Exception e){
            chart.setData(null);
            chart.invalidate();
            Paint p = chart.getPaint(com.github.mikephil.charting.charts.Chart.PAINT_INFO);
            p.setTextSize(60);
            chart.setNoDataTextColor(R.color.colorAccent);
            if (language.equals("eng")) {
                chart.setNoDataText("No data");
            } else {
                chart.setNoDataText("没有数据");
            }
        }
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (mValues.length == 0) {
                return "";
            } else {
                if ((int) value < mValues.length) {
                    switch(mValues[(int) value]){
                        default: return mValues[(int) value];
                        case "咖喱鸡肉包": return "咖喱\n鸡肉包";
                        case "Lian Yong Pau": return "Lian Yong\nPau";
                        case "Stewed Pork Pau": return "Stewed\nPork Pau";
                        case "Curry Chicken Pau": return "Curry\nChicken Pau";
                        case "Char Siew Pau": return "Char Siew\nPau";
                        case "Tau Sar Pau": return "Tau Sar\nPau";
                        case "Pork Siew Mai": return "Pork\nSiew Mai";
                        case "Vegetable Pau": return "Vegetable\nPau";
                        case "Pumpkin Pau": return "Pumpkin\nPau";
                        case "Chicken Pau": return "Chicken\nPau";
                        case "Coffee Pau": return "Coffee\nPau";
                    }
                }
            }
                return "";
        }
    }

    public class MyXAxisRenderer extends XAxisRendererHorizontalBarChart {
        MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, BarChart chart) {
            super(viewPortHandler, xAxis, trans, chart);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split("\n");
            if(formattedLabel.contains("\n")){
                Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
                Utils.drawXAxisValue(c, line[1], x, y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
            }else{
                Utils.drawXAxisValue(c, formattedLabel, x, y, mAxisLabelPaint, anchor, angleDegrees);
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

    private void doGetTopProducts(String customerCode, String selectedMonth, String selectedYear, String language) {
        System.out.println("purchase data is entered");
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Map<String,Integer>> call = service.getTopPurchasedProducts(customerCode, selectedMonth, selectedYear, language);
        call.enqueue(new Callback<Map<String,Integer>>() {
            @Override
            public void onResponse(Call<Map<String,Integer>> call, Response<Map<String,Integer>> response) {
                Map<String, Integer> data = response.body();
                ArrayList<String> sortedItemNames = new ArrayList<>();
                ArrayList<Float> sortedAmounts = new ArrayList<>();
                System.out.println("purchase data is returned");
                if (data == null || data.size() == 0) {
                    chart.hide(isEnglish);
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
                        if (results.length < 5){
                            for (int j = 0; j < 5 - results.length; j++){
                                float quantity = 0;
                                String sortedNames = "";
                                sortedItemNames.add(sortedNames);
                                sortedAmounts.add(quantity);
                            }
                        }
                        for (int j = 0; j < results.length; j++) {
                            float quantity = Float.valueOf(results[j][0] + "");
                            String sortedNames = "" + results[j][1];
                            sortedItemNames.add(sortedNames);
                            sortedAmounts.add(quantity);
                        }
                        showChart(sortedItemNames, sortedAmounts);
                    } catch (Exception e){}
                }
            }

            @Override
            public void onFailure(Call<Map<String,Integer>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void getEarliestYear(String customerCode) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<Integer> call = service.getEarliestYear(customerCode);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int data = response.body();
                System.out.println("data is " + data);
                earliestYear = data;
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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