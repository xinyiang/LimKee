package com.limkee.catalogue;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.limkee.R;
import com.limkee.constant.HttpConstant;
import com.limkee.constant.PostData;
import com.limkee.dao.CatalogueDAO;
import com.limkee.entity.Customer;
import com.limkee.entity.Product;
import com.limkee.navigation.NavigationActivity;
import com.limkee.notification.AlarmReceiver;
import com.limkee.order.ConfirmOrderActivity;
import android.support.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.app.AlarmManager.INTERVAL_DAY;
import static android.content.Context.MODE_PRIVATE;

public class CatalogueFragment extends Fragment {
    private CatalogueFragment.OnFragmentInteractionListener mListener;
    public static View view;
    private CatalogueAdapter mAdapter;
    private ProgressBar progressBar;
    public static RecyclerView recyclerView;
    public static Button confirmOrder;
    public static TextView subtotalAmt;
    public static TextView lbl_subtotal;
    public static double subtotal;
    public static ArrayList<Product> tempOrderList = new ArrayList<>();
    private String isEnglish;
    private AlertDialog.Builder builder;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    public static Retrofit retrofit;
    private Customer customer;
    private String deliveryShift;
    private static CatalogueFragment fragment;
    private AlarmManager alarmManager;
    private Calendar calendar;
    String invalidDesc;
    String invalidDesc2;
    int qtyMultiples;
    String cutoffTime;
    private String selectedProductName;
    private String selectedProductUOM;
    private String selectedItemCode;
    private int orderedQty;

    public CatalogueFragment(){
    }

    public static CatalogueFragment newInstance() {
        fragment = new CatalogueFragment();
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
        cutoffTime = bundle.getString("cutofftime");
        deliveryShift = bundle.getString("deliveryShift");

        if (isEnglish.equals("Yes")){
            ((NavigationActivity)getActivity()).setActionBarTitle("Order Slip");
        } else {
            ((NavigationActivity)getActivity()).setActionBarTitle("订单表");
        }

        builder= new AlertDialog.Builder(getContext());
        loginPreferences = getContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        loginPrefsEditor.commit();

        //alert dialogue show is for today/tmr's time based on current timestamp
        Date currentTimestamp = new Date();
        String time = cutoffTime.substring(0,cutoffTime.length()-3);
        String hour = time.substring(0,2);
        String mins = time.substring(3,5);

        //Date cutoffTimestamp = new Date();
        //cutoffTimestamp.setHours(Integer.parseInt(hour));
        //cutoffTimestamp.setMinutes(Integer.parseInt(mins));
        Calendar cutoffTimeCalendar = Calendar.getInstance();
        cutoffTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        cutoffTimeCalendar.set(Calendar.MINUTE, Integer.parseInt(mins));
        Date cutoffTimestamp = cutoffTimeCalendar.getTime();

        String notif = "";
        //compare current time is < cut off time
        if (currentTimestamp.before(cutoffTimestamp)) {
            System.out.println("current time before cut off");
            if(isEnglish.equals("Yes")) {
                //format cut off time to remove seconds
                notif = "Please place order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM for today's delivery";
                builder.setMessage("Please place order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM for today's delivery");
            } else {
                notif = "今日订单请在早上" + getChineseTime(cutoffTime.substring(0,cutoffTime.length()-3)) + "前下单";
                builder.setMessage("今日订单请在早上" + getChineseTime(cutoffTime.substring(0,cutoffTime.length()-3)) + "前下单");
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            final AlertDialog ad = builder.create();
            ad.show();
        } else {
            System.out.println("current time after cut off");
            if(isEnglish.equals("Yes")) {
                notif = "Please place order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM for tomorrow's delivery";
                builder.setMessage("Please place order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM for tomorrow's delivery");
            } else {
                notif = "明日订单请在早上" + getChineseTime(cutoffTime.substring(0,cutoffTime.length()-3)) + "前下单";
                builder.setMessage("明日订单请在早上" + getChineseTime(cutoffTime.substring(0,cutoffTime.length()-3)) + "前下单");
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            final AlertDialog ad = builder.create();
            ad.show();
        }

        scheduleNotification(getContext(), notif);
    }

    public void scheduleNotification(Context context, String content) {
        String time = cutoffTime.substring(0,cutoffTime.length()-3);
        String hour = time.substring(0,2);
        String mins = time.substring(3,5);
        int notificationId = Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(new Date()));

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour) - 1);
        calendar.set(Calendar.MINUTE, Integer.parseInt(mins));

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra("notif_content", content);
        notificationIntent.putExtra("isEnglish", isEnglish);
        notificationIntent.putExtra("notif_id", notificationId);
        notificationIntent.putExtra("hour", "" + (Integer.parseInt(hour) - 1));
        notificationIntent.putExtra("mins", mins);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        loginPreferences = getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (loginPreferences.getBoolean("FirstTimeLogin", true)) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            loginPrefsEditor.putBoolean("FirstTimeLogin", false);
            loginPrefsEditor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_catalogue, container, false);

        doGetAverageQty(customer.getCompanyCode());
       // doGetCatalogue();

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        confirmOrder = (Button) view.findViewById(R.id.btnNext);
        lbl_subtotal = (TextView) view.findViewById(R.id.lblSubtotalAmt);

        if (isEnglish.equals("Yes")) {
            lbl_subtotal.setText("Sub Total");
            confirmOrder.setText("Next");
        } else {
            lbl_subtotal.setText("小计");
            confirmOrder.setText("下订单");
        }

        mAdapter = new CatalogueAdapter(this, isEnglish, customer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        new CountDownTimer(400, 100) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.start();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //proceed to confirm order
        confirmOrder = (Button) view.findViewById(R.id.btnNext);
        confirmOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                recyclerView.setItemViewCacheSize(tempOrderList.size());

                tempOrderList = CatalogueAdapter.getOrderList();
                final ArrayList <Product> orderList = new ArrayList<>();
                int invalidItem = 0;
                invalidDesc = "";
                invalidDesc2 = "";
                qtyMultiples = 0;


                for (Product p : tempOrderList) {
                    int quantity = p.getDefaultQty();
                    //show error message when products that has wrong quantity
                    if (quantity != 0) {
                        int multiples = p.getQtyMultiples();

                        if (quantity % multiples != 0) {
                            invalidItem++;
                            if (invalidItem == 1) {
                                invalidDesc = p.getDescription();
                                invalidDesc2 = p.getDescription2();
                                qtyMultiples = p.getQtyMultiples();
                                selectedItemCode = p.getItemCode();
                                selectedProductUOM = p.getUom();
                                orderedQty = quantity;
                            }
                        } else {
                            orderList.add(p);
                        }
                    }
                }

            //check if subtotal hits minimum requirements
                if (calculateSubtotal(orderList) < 30) {
                    if (isEnglish.equals("Yes")) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Minimum order is $30.00.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setMessage("订单总额最少要 $30.00")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                })
                                .show();
                    }

                } else {
                    if (invalidItem >= 1) {
                        if (isEnglish.equals("Yes")) {
                            new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                    .setMessage("Incorrect quantity for " + invalidDesc + ". Quantity must be in multiples of " + qtyMultiples + ". Eg: " + qtyMultiples + " , " + (qtyMultiples + qtyMultiples) + ", " + (qtyMultiples + qtyMultiples + qtyMultiples) + " and so on.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                            //reset quantity to default prefix
                                            //p.setDefaultQty(p.getDefaultQty());
                                        }
                                    })
                                    .show();
                        } else {
                            new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                    .setMessage(invalidDesc2 + "的数量有误, 数量必须是" + qtyMultiples + "的倍数，例如" + qtyMultiples + "，" + (qtyMultiples + qtyMultiples) + "等等")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                            //reset quantity to default prefix
                                            // p.setDefaultQty(p.getDefaultQty());
                                        }
                                    })
                                    .show();
                        }
                    }
                        else {

                            DecimalFormat df = new DecimalFormat("#0.00");
                            subtotalAmt = view.findViewById(R.id.subtotalAmt);
                            subtotal = calculateSubtotal(orderList);
                            subtotalAmt.setText("$" + df.format(subtotal));

                            // //updateSubtotal(orderList);
                            CatalogueDAO.order_list = orderList;

                            //store all products with qty > 1 into a temporary arraylist of products
                            Intent intent = new Intent(view.getContext(), ConfirmOrderActivity.class);
                            intent.putParcelableArrayListExtra("orderList", orderList);
                            intent.putExtra("language", isEnglish);
                            intent.putExtra("orderList", orderList);
                            intent.putExtra("customer", customer);
                            intent.putExtra("deliveryShift", deliveryShift);
                            intent.putExtra("cutoffTime", cutoffTime);
                            getActivity().startActivity(intent);
                        }
                    }
                }
        });
    }

    public static String getChineseTime(String time){
        String minutes = time.substring(3,time.length());
        String chineseHour = "";
        String chineseTime;

        time = time.substring(0,2);
        //check hour
        if (time.equals("01")){
            chineseHour = "一";
        } else if (time.equals("02")){
            chineseHour = "二";
        } else if (time.equals("03")){
            chineseHour = "三";
        } else if (time.equals("04")){
            chineseHour = "四";
        }  else if (time.equals("05")){
            chineseHour = "五";
        } else if (time.equals("06")){
            chineseHour = "六";
        } else if (time.equals("07")){
            chineseHour = "七";
        } else if (time.equals("08")){
            chineseHour = "八";
        } else if (time.equals("09")){
            chineseHour = "九";
        } else if (time.equals("10")) {
            chineseHour = "十";
        } else if (time.equals("11")) {
            chineseHour = "十一";
        } else if (time.equals("12")) {
            chineseHour = "十二";
        } else {
            chineseHour = "";
        }

        //check if got mins
        if (minutes.equals("00")){
            chineseTime = chineseHour + "点";
        } else if (minutes.equals("30")){
            chineseTime = chineseHour + "点半";
        } else{
            chineseTime = chineseHour + "点" + getNumber(minutes) + "分";
        }
        return chineseTime;
    }

    public static String getNumber(String number){
        String chineseNumber = "";

        if (number.equals("05")){
            chineseNumber = "零五";
        } else if (number.equals("10")){
            chineseNumber = "十";
        } else if (number.equals("15")){
            chineseNumber = "十五";
        } else if (number.equals("20")){
            chineseNumber = "二十";
        } else if (number.equals("25")){
            chineseNumber = "二十五";
        } else if (number.equals("35")){
            chineseNumber = "三十五";
        } else if (number.equals("40")){
            chineseNumber = "四十";
        } else if (number.equals("45")){
            chineseNumber = "四十五";
        } else if (number.equals("50")){
            chineseNumber = "五十";
        } else if (number.equals("55")){
            chineseNumber = "五十五";
        }  else {
            chineseNumber = "零";
        }
        return chineseNumber;
    }

    private void doGetCatalogue() {
        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<ArrayList<Product>> call = service.getCatalogue();
        call.enqueue(new Callback<ArrayList<Product>>() {

            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                ArrayList<Product> data = response.body();
                CatalogueDAO.catalogue_list = data;

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);

                //by default, let order list be the same as catalogue. if there is any change in qty, it will be updated.
                tempOrderList = CatalogueDAO.catalogue_list;
                String[] qtyDataSet= new String[tempOrderList.size()];
                for (int i = 0; i <tempOrderList.size(); i++) {
                    Product p = tempOrderList.get(i);
                    qtyDataSet[i] = Integer.toString(p.getDefaultQty());
                }

                mAdapter.update(qtyDataSet, CatalogueDAO.catalogue_list, tempOrderList);
                recyclerView.setItemViewCacheSize(qtyDataSet.length);

                DecimalFormat df = new DecimalFormat("#0.00");
                subtotalAmt.setText("$" + df.format(calculateSubtotal(CatalogueDAO.catalogue_list)));
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    private void doGetAverageQty(String companyCode) {
        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<ArrayList<Product>> call = service.getAverageQuantity(companyCode);
        call.enqueue(new Callback<ArrayList<Product>>() {

            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                ArrayList<Product> data = response.body();
                CatalogueDAO.catalogue_list = data;

                if (data == null || data.size() == 0){
                    //show default catalogue
                    doGetCatalogue();
                }

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView = (RecyclerView) view.findViewById(com.limkee.R.id.recyclerView);

                tempOrderList = CatalogueDAO.catalogue_list;
                String[] qtyDataSet= new String[tempOrderList.size()];
                for (int i = 0; i <tempOrderList.size(); i++) {
                    Product p = tempOrderList.get(i);
                    qtyDataSet[i] = Integer.toString(p.getDefaultQty());
                }

                mAdapter.update(qtyDataSet, CatalogueDAO.catalogue_list, tempOrderList);
                recyclerView.setItemViewCacheSize(qtyDataSet.length);

                DecimalFormat df = new DecimalFormat("#0.00");
                subtotalAmt.setText("$" + df.format(calculateSubtotal(CatalogueDAO.catalogue_list)));
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    private double calculateSubtotal(ArrayList<Product> orderList) {
        double subtotal = 0;
        for(Product p : orderList) {
            int qty = p.getDefaultQty();
            subtotal += p.getUnitPrice() * qty;
        }
        return subtotal;
    }

    public static void updateSubtotal(ArrayList<Product> orderList) {
        double subtotal = 0;
        for(Product p : orderList) {
            int qty = p.getDefaultQty();
            subtotal += p.getUnitPrice() * qty;
        }

        DecimalFormat df = new DecimalFormat("#0.00");
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        subtotalAmt.setText("$" + df.format(subtotal));
        recyclerView.setItemViewCacheSize(orderList.size());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationActivity) {
            mListener = (CatalogueFragment.OnFragmentInteractionListener) context;
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
