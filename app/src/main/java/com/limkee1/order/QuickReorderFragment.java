package com.limkee1.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.limkee1.Utility.ChineseCharUtility;
import com.limkee1.constant.HttpConstant;
import com.limkee1.constant.PostData;
import com.limkee1.dao.CatalogueDAO;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.limkee1.navigation.NavigationActivity;
import android.support.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuickReorderFragment extends Fragment {
    private QuickReorderFragment.OnFragmentInteractionListener mListener;
    public static View view;
    private QuickReorderAdapter mAdapter;
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
    String invalidDesc;
    String invalidDesc2;
    int qtyMultiples;
    String cutoffTime;
    private String selectedProductName;
    private String selectedProductUOM;
    private String selectedItemCode;
    private int orderedQty;

    public QuickReorderFragment(){
    }

    public static QuickReorderFragment newInstance() {
        QuickReorderFragment fragment = new QuickReorderFragment();
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
            ((NavigationActivity)getActivity()).setActionBarTitle("Quick Reorder");
        } else {
            ((NavigationActivity)getActivity()).setActionBarTitle("快速下单");
        }

        builder= new AlertDialog.Builder(getContext());
        // AlertDialog ad = builder.create();
        //TextView textView = (TextView) ad.findViewById(android.R.id.message);
        //textView.setTextSize(40);
        //ad.setView(textView);
        loginPreferences = getContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        cutoffTime = loginPreferences.getString("cutofftime", "");

        loginPrefsEditor.commit();

        //alert dialogue show is for today/tmr's time based on current timestamp
        Date currentTimestamp = new Date();
        String time = cutoffTime.substring(0,cutoffTime.length()-3);
        String hour = time.substring(0,2);
        String mins = time.substring(3,5);

        Date cutoffTimestamp = new Date();
        cutoffTimestamp.setHours(Integer.parseInt(hour));
        cutoffTimestamp.setMinutes(Integer.parseInt(mins));

        //compare current time is < cut off time
        if (currentTimestamp.before(cutoffTimestamp)) {
            if(isEnglish.equals("Yes")) {
                //format cut off time to remove seconds
                builder.setMessage("Please place order before " + cutoffTime.substring(0, cutoffTime.length()-3) + " AM for today's delivery");
            } else {
                builder.setMessage("今日订单请在早上" + ChineseCharUtility.getChineseTime(cutoffTime.substring(0, cutoffTime.length()-3)) + "前下单");
            }

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.dismiss();
                }
            });
            final AlertDialog ad = builder.create();
            ad.show();
        } else {
            if(isEnglish.equals("Yes")) {
                builder.setMessage("Please place order before " + cutoffTime.substring(0, cutoffTime.length()-3) + " AM for tomorrow's delivery");
            } else {
                builder.setMessage("明日订单请在早上" + ChineseCharUtility.getChineseTime(cutoffTime.substring(0, cutoffTime.length()-3)) + "前下单");
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.dismiss();
                }
            });
            final AlertDialog ad = builder.create();
            ad.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_quick_reorder, container, false);

        doGetLastOrder(customer.getDebtorCode());

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        subtotalAmt = view.findViewById(R.id.subtotalAmt);
        confirmOrder = view.findViewById(R.id.btnPlaceOrder);
        lbl_subtotal = (TextView) view.findViewById(R.id.lblSubtotalAmt);

        if (isEnglish.equals("Yes")) {
            lbl_subtotal.setText("Sub Total");
            confirmOrder.setText("Next");
        } else {
            lbl_subtotal.setText("小计");
            confirmOrder.setText("下订单");
        }

        mAdapter = new QuickReorderAdapter(this, isEnglish, customer);
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
        confirmOrder = (Button) view.findViewById(R.id.btnPlaceOrder);

        confirmOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                recyclerView.setItemViewCacheSize(tempOrderList.size());

                tempOrderList = QuickReorderAdapter.getOrderList();
                final ArrayList<Product> orderList = new ArrayList<>();
                int invalidItem = 0;
                invalidDesc = "";
                invalidDesc2 = "";
                qtyMultiples = 0;
                //remove products that has 0 quantity

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
                                orderedQty = p.getDefaultQty();
                            }
                        } else {
                            orderList.add(p);
                        }
                    }
                }

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
                } else {
                    //check if subtotal hits minimum requirements
                    if (calculateSubtotal(orderList) < 30) {
                        if (isEnglish.equals("Yes")) {
                            new AlertDialog.Builder(getContext())
                                    .setMessage("Minimum order is $30.00.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                        }
                                    })
                                    .show();
                        } else {
                            new AlertDialog.Builder(getContext())
                                    .setMessage("订单总额最少要 $30.00")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                        }
                                    })
                                    .show();
                        }
                    } else{
                        DecimalFormat df = new DecimalFormat("#0.00");
                        subtotalAmt = view.findViewById(R.id.subtotalAmt);
                        subtotal = calculateSubtotal(orderList);
                        subtotalAmt.setText("$" + df.format(subtotal));

                        //updateSubtotal(orderList);
                        CatalogueDAO.order_list = orderList;

                        //store all products with qty > 1 into a temporary arraylist of products
                        Intent intent = new Intent(view.getContext(), QuickReorderConfirmOrderActivity.class);
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

    private void doGetLastOrder(String customerCode) {
        if (retrofit == null) {

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(HttpConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PostData service = retrofit.create(PostData.class);
        Call<ArrayList<Product>> call = service.getQuickOrderCatalogue(customerCode);
        call.enqueue(new Callback<ArrayList<Product>>() {

            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                ArrayList<Product> data = response.body();
                CatalogueDAO.quickReorder_list = data;

                if (data == null || data.size() == 0) {
                    //show default catalogue
                    doGetCatalogue();
                } else {

                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView = (RecyclerView) view.findViewById(com.limkee1.R.id.recyclerView);

                    //by default, let order list be the same as catalogue. if there is any change in qty, it will be updated.
                    tempOrderList = CatalogueDAO.quickReorder_list;
                    String[] qtyDataSet = new String[tempOrderList.size()];
                    for (int i = 0; i < tempOrderList.size(); i++) {
                        Product p = tempOrderList.get(i);
                        qtyDataSet[i] = Integer.toString(p.getDefaultQty());
                    }

                    mAdapter.update(qtyDataSet, CatalogueDAO.quickReorder_list, tempOrderList);
                    recyclerView.setItemViewCacheSize(qtyDataSet.length);

                    DecimalFormat df = new DecimalFormat("#0.00");
                    subtotalAmt.setText("$" + df.format(calculateSubtotal(CatalogueDAO.quickReorder_list)));
                }
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
                CatalogueDAO.quickReorder_list = data;

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView = (RecyclerView) view.findViewById(com.limkee1.R.id.recyclerView);

                //by default, let order list be the same as catalogue. if there is any change in qty, it will be updated.
                tempOrderList = CatalogueDAO.quickReorder_list;
                String[] qtyDataSet= new String[tempOrderList.size()];
                for (int i = 0; i <tempOrderList.size(); i++) {
                    Product p = tempOrderList.get(i);
                    qtyDataSet[i] = Integer.toString(p.getDefaultQty());
                }

                mAdapter.update(qtyDataSet, CatalogueDAO.quickReorder_list, tempOrderList);
                recyclerView.setItemViewCacheSize(qtyDataSet.length);

                DecimalFormat df = new DecimalFormat("#0.00");
                subtotalAmt.setText("$" + df.format(calculateSubtotal(CatalogueDAO.quickReorder_list)));
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationActivity) {
            mListener = (QuickReorderFragment.OnFragmentInteractionListener) context;
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
