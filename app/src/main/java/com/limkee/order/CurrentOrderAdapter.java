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

/**
 * Created by Xin Yi on 12/6/2018.
 */

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.MyViewHolder> {
    CurrentOrderFragment fragment;
    private ArrayList<Order> currentOrderList;
    private String orderID;
    private Customer customer;


    public CurrentOrderAdapter(CurrentOrderFragment fragment, ArrayList<Order> col,Customer customer) {
        this.fragment = fragment;
        this.currentOrderList = col;
        this.customer = customer;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_recycler_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Order order = currentOrderList.get(position);
        holder.bindContent(order);

        holder.orderID.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = order.getOrderID();

                Intent intent = new Intent(view.getContext(), CurrentOrderDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("customer", (Serializable) customer);
                fragment.startActivity(intent);

            }
        });

        holder.deliveryDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = order.getOrderID();

                Intent intent = new Intent(view.getContext(), CurrentOrderDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("customer", (Serializable) customer);
                fragment.startActivity(intent);

            }
        });

        holder.noOfItems.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = order.getOrderID();

                Intent intent = new Intent(view.getContext(), CurrentOrderDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("customer", (Serializable) customer);
                fragment.startActivity(intent);

            }
        });

        holder.lbl_deliveryDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = order.getOrderID();

                Intent intent = new Intent(view.getContext(), CurrentOrderDetailActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("customer", (Serializable) customer);
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
            deliveryDate.setText(order.getDeliveryDate());

            //if english
            /*
            if (){
                lbl_deliveryDate.setText("Delivering on");

                if (order.getNoOfItems() == 1){
                    noOfItems.setText(order.getNoOfItems() + " item");
                } else {
                    noOfItems.setText(order.getNoOfItems() + " items");
                }
            }
            */

            noOfItems.setText(order.getNoOfItems() + " 样");


        }
    }


    @Override
    public int getItemCount() {
        return currentOrderList.size();

    }
}
