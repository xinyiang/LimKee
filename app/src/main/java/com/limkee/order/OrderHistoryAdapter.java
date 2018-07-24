package com.limkee.order;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.limkee.R;

import com.limkee.entity.Customer;
import com.limkee.entity.Order;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {
    OrderHistoryFragment fragment;
    private ArrayList<Order> orderHistoryList;
    private String orderID;
    private Customer customer;
    private String isEnglish;
    private String ETADeliveryDate;
    public RelativeLayout rel;


    public OrderHistoryAdapter(OrderHistoryFragment fragment, ArrayList<Order> ohl,Customer customer,  String isEnglish) {
        this.fragment = fragment;
        this.orderHistoryList = ohl;
        this.customer = customer;
        this.isEnglish = isEnglish;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_recycler_view, parent, false);
        rel= (RelativeLayout) itemView.findViewById(R.id.view_foreground);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Order order = orderHistoryList.get(position);
        holder.bindContent(order);

        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderID = order.getOrderID();

                String ETA = order.getDeliveryDate();
                String date = ETA.substring(8);
                String month = ETA.substring(5,7);
                String year = ETA.substring(0,4);
                ETADeliveryDate = date + "/" + month + "/" + year;

                Intent intent = new Intent(view.getContext(), OrderHistoryDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("deliveryDate", ETADeliveryDate);
                intent.putExtra("numItems", order.getNoOfItems());
                intent.putExtra("deliveryShift", order.getDeliveryShift());
                intent.putExtra("customer", customer);
                fragment.startActivity(intent);
            }

        });

        holder.orderID.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = order.getOrderID();

                String ETA = order.getDeliveryDate();
                String date = ETA.substring(8);
                String month = ETA.substring(5,7);
                String year = ETA.substring(0,4);
                ETADeliveryDate = date + "/" + month + "/" + year;

                Intent intent = new Intent(view.getContext(), OrderHistoryDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("deliveryDate", ETADeliveryDate);
                intent.putExtra("numItems", order.getNoOfItems());
                intent.putExtra("deliveryShift", order.getDeliveryShift());
                intent.putExtra("customer", customer);
                fragment.startActivity(intent);

            }
        });

        holder.deliveryDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = order.getOrderID();

                String ETA = order.getDeliveryDate();
                String date = ETA.substring(8);
                String month = ETA.substring(5,7);
                String year = ETA.substring(0,4);
                ETADeliveryDate = date + "/" + month + "/" + year;

                Intent intent = new Intent(view.getContext(), OrderHistoryDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("deliveryDate", ETADeliveryDate);
                intent.putExtra("numItems", order.getNoOfItems());
                intent.putExtra("deliveryShift", order.getDeliveryShift());
                intent.putExtra("customer", customer);
                fragment.startActivity(intent);

            }
        });

        holder.noOfItems.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = order.getOrderID();

                String ETA = order.getDeliveryDate();
                String date = ETA.substring(8);
                String month = ETA.substring(5,7);
                String year = ETA.substring(0,4);
                ETADeliveryDate = date + "/" + month + "/" + year;

                Intent intent = new Intent(view.getContext(), OrderHistoryDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("deliveryDate", ETADeliveryDate);
                intent.putExtra("numItems", order.getNoOfItems());
                intent.putExtra("deliveryShift", order.getDeliveryShift());
                intent.putExtra("customer", customer);
                fragment.startActivity(intent);

            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, deliveryDate, noOfItems, lbl_deliveryDate;
        public RelativeLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            orderID = (TextView) view.findViewById(R.id.orderID);
            deliveryDate = (TextView) view.findViewById(R.id.deliveryDate);
            noOfItems = (TextView) view.findViewById(R.id.noOfitems);
            lbl_deliveryDate = (TextView) view.findViewById(R.id.lbl_deliveryDate);
        }

        public void bindContent(Order order) {

            orderID.setText(order.getOrderID());

            String ETA = order.getDeliveryDate();
            String date = ETA.substring(8);
            String month = ETA.substring(5,7);
            String year = ETA.substring(0,4);
            ETADeliveryDate = date + "/" + month + "/" + year;
            deliveryDate.setText(ETADeliveryDate);

            if (isEnglish.equals("Yes")){

                if (order.getNoOfItems() == 1){
                    noOfItems.setText(order.getNoOfItems() + " item");
                } else {
                    noOfItems.setText(order.getNoOfItems() + " items");
                }
            } else {
                noOfItems.setText(order.getNoOfItems() + " 样");
            }

        }
    }

    public void update(ArrayList<Order> orderList){
        orderHistoryList = orderList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return orderHistoryList.size();

    }
}
