package com.limkee.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limkee.R;
import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Xin Yi on 12/6/2018.
 */

public class CurrentOrderDetailAdapter extends RecyclerView.Adapter<CurrentOrderDetailAdapter.MyViewHolder> {

    private ArrayList<OrderQuantity> oqList;
    private OrderDetails od;
    private CurrentOrderDetailFragment fragment;
    private String isEnglish;

    public CurrentOrderDetailAdapter(CurrentOrderDetailFragment fragment, String isEnglish) {
        this.fragment = fragment;
        this.isEnglish = isEnglish;
    }

    @Override
    public CurrentOrderDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_details_product, parent, false);
        return new CurrentOrderDetailAdapter.MyViewHolder(itemView);
    }
    public void onBindViewHolder(final CurrentOrderDetailAdapter.MyViewHolder holder, int position) {

        OrderQuantity product = oqList.get(position);
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

        public void bindContent(OrderQuantity product) {
            DecimalFormat df = new DecimalFormat("#0.00");
            if (isEnglish.equals("Yes")) {
                description.setText(product.getDescription());
            } else {
                description.setText(product.getDescription2());
            }

            qty.setText(Integer.toString(product.getQty()));
            double unitSub = product.getQty() * product.getUnitPrice();
            unitSubtotal.setText("$" + df.format(unitSub));

        }
    }
    public void update(OrderDetails od){
        this.od = od;
        notifyDataSetChanged();
    }

    public void update2(ArrayList<OrderQuantity> orderList){
        oqList = orderList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (oqList == null ) {
            return 0;
        }
        return oqList.size();
    }
}
