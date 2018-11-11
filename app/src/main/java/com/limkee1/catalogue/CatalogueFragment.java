package com.limkee1.catalogue;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.limkee1.R;
import com.limkee1.Utility.DateUtility;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.dao.CatalogueDAO;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.limkee1.navigation.NavigationActivity;
import com.limkee1.notification.AlarmReceiver;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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
    boolean hasInternet;

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

        if (isEnglish.equals("Yes")){
            ((NavigationActivity)getActivity()).setActionBarTitle("Order Slip");
        } else {
            ((NavigationActivity)getActivity()).setActionBarTitle("订单表");
        }

        builder= new AlertDialog.Builder(getContext());

        //alert dialogue show is for today/tmr's time based on current timestamp
        Date currentTimestamp = new Date();
        String time = cutoffTime.substring(0,cutoffTime.length()-3);
        String hour = time.substring(0,2);
        String mins = time.substring(3,5);

        Calendar cutoffTimeCalendar = Calendar.getInstance();
        cutoffTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        cutoffTimeCalendar.set(Calendar.MINUTE, Integer.parseInt(mins));
        Date cutoffTimestamp = cutoffTimeCalendar.getTime();

        String notif = "";
        //compare current time is < cut off time
        if (currentTimestamp.before(cutoffTimestamp)) {
            System.out.println("current time before cut off");

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                SimpleDateFormat sundayFormat = new SimpleDateFormat("EEEE");

                String dayOfWeek = "";
                String currentDate = "";

                Date timeNow = new Date();
                currentDate = sdf.format(timeNow);

                String date = currentDate.substring(0, 10);

                //tomorrow date is +1 of today date
                int tmrDate = Integer.parseInt(date.substring(8, date.length()));
                tmrDate += 1;
                String day = Integer.toString(tmrDate);
                String month = date.substring(5, 7);
                String yr = date.substring(0, 4);

                //if today is sunday, do not show delivery is over
                String todayDay = date.substring(8, date.length());

                Date todayDate = new Date(yr + "/" + month + "/" + todayDay);

                dayOfWeek = sundayFormat.format(todayDate);

                int lastDay = DateUtility.getLastDayOfMonth(month);

                if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {

                    int followingDay = Integer.parseInt(day);
                        followingDay = followingDay + 1;

                        if (followingDay > lastDay) {
                            followingDay = lastDay;
                        }

                        String currentDay = "";
                        if (followingDay < 10){
                            currentDay = "0" + followingDay;
                        } else {
                            currentDay = Integer.toString(followingDay);
                        }

                        if (month.length() == 1){
                            month = "0" + month;
                        }

                    if (isEnglish.equals("Yes")) {
                        notif = "Please place order before tomorrow " + cutoffTime.substring(0, cutoffTime.length() - 3) + " AM for tomorrow's delivery";
                        builder.setMessage("For tomorrow's delivery, please place order before tomorrow (" + currentDay + "/" + month + "/" + yr + ") " + cutoffTime.substring(0, cutoffTime.length() - 3) + " AM");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        notif = "若要在明天送货, 请在明天早上" + cutoffTime.substring(0, cutoffTime.length() - 3) + "前下单";
                        builder.setMessage("若要在明天送货，请在明天 (" + currentDay + "/" + month + "/" + yr + ") 早上" + cutoffTime.substring(0, cutoffTime.length() - 3) + "前下单");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    }
                    final AlertDialog ad = builder.create();
                    ad.show();
                    TextView textView = (TextView) ad.findViewById(android.R.id.message);
                    textView.setTextSize(20);
                } else {
                    if (isEnglish.equals("Yes")) {
                        //format cut off time to remove seconds
                        notif = "Please place order before " + cutoffTime.substring(0, cutoffTime.length() - 3) + " AM for today's delivery";
                        builder.setMessage("For today's delivery, please place order before " + cutoffTime.substring(0, cutoffTime.length() - 3) + " AM today");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        notif = "今日订单请在早上" + cutoffTime.substring(0, cutoffTime.length() - 3) + "前下单";
                        builder.setMessage("若要今日送货，请在今天早上" + cutoffTime.substring(0, cutoffTime.length() - 3) + "前下单");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    }

                    final AlertDialog ad = builder.create();
                    ad.show();
                    TextView textView = (TextView) ad.findViewById(android.R.id.message);
                    textView.setTextSize(20);
                }
            } catch (Exception e){

            }
        } else {
            System.out.println("current time after cut off");
            //check if tomorrow is sunday
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                SimpleDateFormat sundayFormat = new SimpleDateFormat("EEEE");

                String dayOfWeek = "";
                String currentDate = "";

                Date timeNow = new Date();
                currentDate = sdf.format(timeNow);

                String date = currentDate.substring(0, 10);

                //tomorrow date is +1 of today date
                int tmrDate = Integer.parseInt(date.substring(8, date.length()));
                tmrDate+=1;
                String day = Integer.toString(tmrDate);
                String month = date.substring(5, 7);
                String yr = date.substring(0, 4);

                //need to check if is new month/new year
                int lastDay = DateUtility.getLastDayOfMonth(month);

                Date tomorrowDate = new Date(yr + "/" +  month + "/" + day);

                dayOfWeek = sundayFormat.format(tomorrowDate);

                int followingDay = Integer.parseInt(day);
                if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")) {
                    followingDay = tmrDate + 1;

                    if (followingDay > lastDay) {
                        followingDay = lastDay;
                    }

                    String currentDay = "";
                    if (followingDay < 10){
                        currentDay = "0" + followingDay;
                    } else {
                        currentDay = Integer.toString(followingDay);
                    }

                    if (month.length() == 1){
                        month = "0" + month;
                    }

                    if(isEnglish.equals("Yes")) {
                        //do not push message on Sunday
                        notif = "Please place order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM on Monday for Monday's delivery";
                        builder.setMessage("Today's delivery is over! For Monday's delivery, please place order before Monday ("  + currentDay + "/" + month + "/" + yr + ") " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        //do not push message on Sunday
                        notif = "星期一订单请在当日早上" + cutoffTime.substring(0,cutoffTime.length()-3) + "前下单";
                        builder.setMessage("今日送货已结束! 若要在星期一送货，请在当日 (" + currentDay + "/" + month + "/" + yr + ") 早上" + cutoffTime.substring(0,cutoffTime.length()-3) + "前下单");

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    }
                    final AlertDialog ad = builder.create();
                    ad.show();
                    TextView textView = (TextView) ad.findViewById(android.R.id.message);
                    textView.setTextSize(20);
                } else {

                    if (tmrDate > lastDay) {
                        tmrDate = lastDay;
                    }

                    String currentDay = "";
                    if (tmrDate < 10) {
                        currentDay = "0" + followingDay;
                    } else {
                        currentDay = Integer.toString(followingDay);
                    }

                    if (month.length() == 1) {
                        month = "0" + month;
                    }

                    //if today is sunday, do not show delivery is over
                    String todayDay = date.substring(8, date.length());

                    Date todayDate = new Date(yr + "/" + month + "/" + todayDay);

                    dayOfWeek = sundayFormat.format(todayDate);

                    if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Sun")){
                        if(isEnglish.equals("Yes")) {
                            notif = "Please place order before tomorrow " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM for tomorrow's delivery";
                            builder.setMessage("For tomorrow's delivery, please place order before tomorrow (" +  currentDay + "/" + month + "/" + yr  + ") " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            notif = "若要在明天送货, 请在明天早上" + cutoffTime.substring(0,cutoffTime.length()-3) + "前下单";
                            builder.setMessage("若要在明天送货，请在明天 (" + currentDay + "/" + month + "/" + yr + ") 早上" + cutoffTime.substring(0,cutoffTime.length()-3) +"前下单");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                        }
                        final AlertDialog ad = builder.create();
                        ad.show();
                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                        textView.setTextSize(20);
                    } else {
                        if(isEnglish.equals("Yes")) {
                            notif = "Please place order before " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM for tomorrow's delivery";
                           builder.setMessage("Today's delivery is over! For tomorrow's delivery, please place order before tomorrow (" +  currentDay + "/" + month + "/" + yr  + ") " + cutoffTime.substring(0,cutoffTime.length()-3) + " AM");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            notif = "明日订单请在早上" + cutoffTime.substring(0,cutoffTime.length()-3) + "前下单";
                            builder.setMessage("今日送货已结束! 若要在明天送货，请在明天 (" + currentDay + "/" + month + "/" + yr + ") 早上" + cutoffTime.substring(0,cutoffTime.length()-3) +"前下单");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                        }
                        final AlertDialog ad = builder.create();
                        ad.show();
                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                        textView.setTextSize(20);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error" + e.getMessage());
            }
        }

        //commented out for push notification
        //scheduleNotification(getContext(), notif);
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
        loginPrefsEditor = loginPreferences.edit();
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

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        confirmOrder = (Button) view.findViewById(R.id.btnNext);
        lbl_subtotal = (TextView) view.findViewById(R.id.lblSubtotalAmt);

        hasInternet = isNetworkAvailable();
        if (!hasInternet) {
            TextView lbl_noInternet = view.findViewById(R.id.lbl_noOrders);
            lbl_noInternet.setVisibility(View.VISIBLE);

            if (isEnglish.equals("Yes")) {
                lbl_noInternet.setText("No internet connection");
            } else {
                lbl_noInternet.setText("没有网络");
            }

        } else {


            doGetLastWeekAverageForCatalogue(customer.getCompanyCode());
            // doGetCatalogue();


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
        }
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

                for (Product product : tempOrderList) {
                    int quantity = product.getDefaultQty();
                    //check if qty is correct
                    if (quantity != 0) {
                        int multiples = product.getQtyMultiples();

                        if (quantity % multiples != 0) {
                            invalidItem++;
                            if (invalidItem == 1) {
                                invalidDesc = product.getDescription();
                                invalidDesc2 = product.getDescription2();
                                qtyMultiples = product.getQtyMultiples();
                                selectedItemCode = product.getItemCode();
                                selectedProductUOM = product.getUom();
                                orderedQty = 0;
                            }
                        } else {
                            orderList.add(product);
                        }
                    }
                }


                //for the last product that has qty being edited: when user did not click tick in keyboard and click back and Next button
                if (invalidItem >= 1) {
                    if (isEnglish.equals("Yes")) {
                        final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                .setMessage("Incorrect quantity for " + invalidDesc + ". Quantity must be in multiples of " + qtyMultiples + ". Eg: " + qtyMultiples + " , " + (qtyMultiples + qtyMultiples) + ", " + (qtyMultiples + qtyMultiples + qtyMultiples) + " and so on.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                        //reset edit text quantity to 0 in edit text
                                        //dialog.getButton(Dialog.BUTTON_POSITIVE).setTextSize(40);

                                    }
                                })

                                .show();
                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                        textView.setTextSize(20);
                    } else {
                        final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(view.getContext())
                        //new android.support.v7.app.AlertDialog.Builder(view.getContext())
                                .setMessage(invalidDesc2 + "的数量有误, 数量必须是" + qtyMultiples + "的倍数，例如" + qtyMultiples + "，" + (qtyMultiples + qtyMultiples) + "等等")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                        //reset edit text quantity to 0 in edit text
                                    }
                                })
                                .show();
                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                        textView.setTextSize(20);
                    }
                } else {
                    //check if subtotal hits minimum requirements
                    if (calculateSubtotal(orderList) < 30) {
                        if (isEnglish.equals("Yes")) {
                            final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(getContext())
                            //new AlertDialog.Builder(getContext())
                                    .setMessage("Minimum order is $30.00.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                        }
                                    })
                                    .show();
                            TextView textView = (TextView) ad.findViewById(android.R.id.message);
                            textView.setTextSize(20);
                        } else {
                            final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(getContext())
                            //new AlertDialog.Builder(getContext())
                                    .setMessage("订单总额最少要 $30.00")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                        }
                                    })
                                    .show();
                            TextView textView = (TextView) ad.findViewById(android.R.id.message);
                            textView.setTextSize(20);
                        }

                    } else {
                        DecimalFormat df = new DecimalFormat("#0.00");
                        subtotalAmt = view.findViewById(R.id.subtotalAmt);
                        subtotal = calculateSubtotal(orderList);
                        subtotalAmt.setText("$" + df.format(subtotal));

                        //updateSubtotal(orderList);
                        CatalogueDAO.order_list = orderList;

                        //store all products with qty > 1 into a temporary arraylist of products
                        Intent intent = new Intent(view.getContext(), ConfirmOrderActivity.class);
                        intent.putParcelableArrayListExtra("orderList", orderList);
                        intent.putExtra("language", isEnglish);
                        intent.putExtra("orderList", orderList);
                        intent.putExtra("customer", customer);
                        intent.putExtra("cutoffTime", cutoffTime);
                        getActivity().startActivity(intent);
                    }
                }
            }
        });
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
                recyclerView = (RecyclerView) view.findViewById(com.limkee1.R.id.recyclerView);

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

    private void doGetLastWeekAverageForCatalogue(String companyCode) {
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
                    //show default catalogue where all qty is 0
                    doGetCatalogue();
                }

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView = (RecyclerView) view.findViewById(com.limkee1.R.id.recyclerView);

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
