package com.limkee.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.limkee.entity.Product;
import com.limkee.order.ConfirmOrderActivity;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.limkee.R;

import org.w3c.dom.Text;

/**
 * Created by Xin Yi on 24/5/2018.
 */

public class ConfirmOrderAdapter extends RecyclerView.Adapter<ConfirmOrderAdapter.MyViewHolder>  {
        private ArrayList<Product> orderList;
        private ConfirmOrderFragment fragment;

        public ConfirmOrderAdapter(ConfirmOrderFragment fragment, ArrayList<Product> orderList) {
            this.fragment = fragment;
            this.orderList = orderList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.confirm_order_products, parent, false);
            return new MyViewHolder(itemView);
        }
        public void onBindViewHolder(final MyViewHolder holder, int position) {

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
