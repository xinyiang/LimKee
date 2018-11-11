package com.limkee1.dashboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.limkee1.R;
import com.limkee1.Utility.DateUtility;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.entity.Customer;
import com.limkee1.order.CancelledOrderFragment;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private String isEnglish;
    private Spinner ddlYear;
    static TotalSalesFragment fragment;
    private static String[] years;
    private String selectedYear = "";
    private HashMap<String, Integer> monthConvert = new HashMap<>();
    private String systemYear;
    private CheckBox checkBox;
    private Chart chart;
    private boolean checkBoxStatus;
    boolean hasInternet;
    private int earliestYear;
    private int length;
    CompositeDisposable compositeDisposable  = new CompositeDisposable();

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

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd hh:mm:ss");
            String today = sdf.format(new Date());
            systemYear = today.substring(0, 4);

        } catch (Exception e) {
            e.printStackTrace();
        }

        earliestYear = 2017;

        //this method did not re-intialise earliestYear variable from server database
        getEarliestYear(customer.getDebtorCode());

        if (earliestYear == 0 || earliestYear == Integer.parseInt(systemYear)){
            earliestYear = Integer.parseInt(systemYear);
            length = 0;
        } else {
            length = Integer.parseInt(systemYear) - earliestYear;
        }

        years = new String[length+2];

        if (isEnglish.equals("Yes")){
            //years = new String[]{"Year","2017","2018"};
            years[0] = "Year";
        } else {
            //years = new String []{"年","2017","2018"};
            years[0] = "年";

        }

        int size = 1;
        for (int i = earliestYear; i<= Integer.parseInt(systemYear);i++){
            years[size] = Integer.toString(i);
            size ++;
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
        TextView lbl_noInternet = view.findViewById(R.id.lbl_noOrders);
        ddlYear = (Spinner) view.findViewById(R.id.ddl_year);
        chart = new Chart((HorizontalBarChart) view.findViewById(R.id.chart));
        checkBox = ((CheckBox) view.findViewById(R.id.cb_onlyMe));

        hasInternet = isNetworkAvailable();
        if (!hasInternet) {
            lbl_noInternet.setVisibility(View.VISIBLE);
            ddlYear.setVisibility(View.INVISIBLE);
            checkBox.setVisibility(View.INVISIBLE);
            chart.hideWithNoInternet();

            if (isEnglish.equals("Yes")) {
                lbl_noInternet.setText("No internet connection");
            } else {
                lbl_noInternet.setText("没有网络");
            }
        } else {

            boolean isChecked = ((CheckBox) view.findViewById(R.id.cb_onlyMe)).isChecked();

            chart.loading();

            if (isEnglish.equals("Yes")) {
                checkBox.setText("Show only me");
            } else {
                checkBox.setText("只是我");
            }
            checkBox.setChecked(true);
            checkBoxStatus = checkBox.isChecked();
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

            chart.loading();

            ddlYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    selectedYear = arg0.getItemAtPosition(position).toString();
                    if (selectedYear.equals("Year") || selectedYear.equals("年")) {
                        chart.hide(isEnglish);
                    } else {
                        if (!isChecked) {
                            doGetAverageSales(selectedYear);
                        }
                        doGetCustomerSales(customer.getDebtorCode(), selectedYear);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkBoxStatus = isChecked;
                    if (selectedYear.equals("Year") || selectedYear.equals("年")) {
                        chart.hide(isEnglish);
                    } else {
                        if (isChecked) {
                            doGetCustomerSales(customer.getDebtorCode(), selectedYear);
                        } else {
                            doGetCustomerSales(customer.getDebtorCode(), selectedYear);
                            doGetAverageSales(selectedYear);
                        }
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void doGetCustomerSales(String customerCode, String selectedYear) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);

        Call<LinkedHashMap<String,Double>> call = service.getFilteredCustomerSales(customerCode, selectedYear);
        call.enqueue(new Callback<LinkedHashMap<String,Double>>() {

            @Override
            public void onResponse(Call<LinkedHashMap<String,Double>> call, Response<LinkedHashMap<String,Double>> response) {
                LinkedHashMap<String,Double> data = response.body();
                System.out.println("data is " + data + " and size is " + data.size());

                ArrayList<String> sortedMonths = new ArrayList<>();
                ArrayList<Float> sortedAmounts = new ArrayList<>();

                if (data == null || data.size() == 0) {
                    chart.hide(isEnglish);
                } else {
                    int i = 0;
                    Object[][] results = new Object[data.size()][2];
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String mth = (String)entry.getKey();
                        double amt = data.get(mth);
                        results[i][0] = mth;
                        results[i][1] = amt;
                        i++;
                    }

                    Arrays.sort(results, new Comparator<Object[]>() {
                        public int compare(Object[] o1, Object[] o2) {
                            Integer i1 = (Integer) (monthConvert.get(o1[0]));
                            Integer i2 = (Integer) (monthConvert.get(o2[0]));
                            if (i1 != null && i2 != null) {
                                return i2.compareTo(i1);
                            } else{
                                return 0;
                            }
                        }
                    });

                    for (int j = 0; j < results.length; j++) {
                        float quantity = Float.valueOf(results[j][1] + "");
                        String sortedNames = "" + results[j][0];
                        sortedMonths.add(sortedNames);
                        sortedAmounts.add(quantity);
                    }
                    chart.updateDataSet("customer", sortedMonths, sortedAmounts, isEnglish);
                    chart.showChart(checkBoxStatus, isEnglish);
                    chart.showChart(checkBoxStatus, isEnglish);
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
                ArrayList<String> sortedMonths = new ArrayList<>();
                ArrayList<Float> sortedAmounts = new ArrayList<>();

                if (data == null || data.size() == 0) {
                    chart.hide(isEnglish);
                } else {
                    int i = 0;
                    Object[][] results = new Object[data.size()][2];
                    Iterator entries = data.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String mth = (String) entry.getKey();
                        double amt = data.get(mth);
                        results[i][0] = mth;
                        results[i][1] = amt;
                        i++;
                    }
                    Arrays.sort(results, new Comparator<Object[]>() {
                        public int compare(Object[] o1, Object[] o2) {
                            Integer i1 = (Integer) (monthConvert.get(o1[0]));
                            Integer i2 = (Integer) (monthConvert.get(o2[0]));
                            if (i1 != null && i2 != null) {
                                return i2.compareTo(i1);
                            } else{
                                return 0;
                            }
                        }
                    });

                    for (int j = 0; j < results.length; j++) {
                        float quantity = Float.valueOf(results[j][1] + "");
                        String sortedNames = "" + results[j][0];
                        sortedMonths.add(sortedNames);
                        sortedAmounts.add(quantity);
                    }
                    chart.updateDataSet("average", sortedMonths, sortedAmounts, isEnglish);
                    chart.showChart(checkBoxStatus, isEnglish);
                    chart.showChart(checkBoxStatus, isEnglish);
                }
            }
            @Override
            public void onFailure(Call<LinkedHashMap<String, Double>> call, Throwable t) {
                System.out.println("error : " + t.getMessage());
            }
        });
    }

    private void getEarliestYear(String customerCode) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        PostData postData = new Retrofit.Builder()
                .baseUrl(HttpConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build().create(PostData.class);

        compositeDisposable.add(postData.getEarliestOrderYear(customerCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(int year) {
        //return the correct earliest year based on past orders but variable not set
        earliestYear = year;
    }

    private void handleError(Throwable error) {
        System.out.println("Error " + error.getMessage());
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