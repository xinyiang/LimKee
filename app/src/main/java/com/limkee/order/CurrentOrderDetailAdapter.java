package com.limkee.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limkee.R;
import com.limkee.entity.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Xin Yi on 12/6/2018.
 */

public class CurrentOrderDetailAdapter extends RecyclerView.Adapter<CurrentOrderDetailAdapter.MyViewHolder> {

    private ArrayList<Product> orderList;
    private CurrentOrderDetailFragment fragment;

    public CurrentOrderDetailAdapter(CurrentOrderDetailFragment fragment, ArrayList<Product> orderList) {
        this.fragment = fragment;
        this.orderList = orderList;
    }

    @Override
    public CurrentOrderDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_details_product, parent, false);
        return new CurrentOrderDetailAdapter.MyViewHolder(itemView);
    }
    public void onBindViewHolder(final CurrentOrderDetailAdapter.MyViewHolder holder, int position) {

        Product product = orderList.get(position);
        holder.bindContent(product);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description, qty, unitSubtotal;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
            qty = (TextView) view.findViewById(R.id.qty);
            unitSubtotal = (TextView) view.findViewById(R.id.unitSubtotal);
        }

        public void bindContent(Product product) {
            DecimalFormat df = new DecimalFormat("#0.00");
            description.setText(product.getDescription());
            qty.setText(Integer.toString(product.getDefaultQty()));
            double unitSub = product.getDefaultQty() * product.getUnitPrice();
            unitSubtotal.setText("$" + df.format(unitSub));

        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
