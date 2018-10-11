package com.limkee1.wallet;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.entity.Customer;
import com.limkee1.entity.OrderDetails;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> {
    private WalletFragment fragment;
    private RecyclerView mRecyclerView;
    private String isEnglish;
    private ArrayList<OrderDetails> odList;
    private String orderID;
    private Customer customer;
    public RelativeLayout rel;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public WalletAdapter(WalletFragment fragment, Customer customer, String isEnglish, ArrayList<OrderDetails> odList) {
        this.fragment = fragment;
        this.customer = customer;
        this.isEnglish = isEnglish;
        this.odList = odList;
    }

    @Override
    public WalletAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_recycler_view, parent, false);

        rel= (RelativeLayout) itemView.findViewById(R.id.view_foreground);

        return new WalletAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(final WalletAdapter.MyViewHolder holder, int position) {

        OrderDetails od = odList.get(position);
        holder.bindContent(od);

        rel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = od.getOrderID();

                Intent intent = new Intent(view.getContext(), TransactionHistoryActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("customer", customer);
                intent.putExtra("orderDetails", od);
                fragment.startActivity(intent);

            }
        });

        holder.subtotal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = od.getOrderID();

                Intent intent = new Intent(view.getContext(), TransactionHistoryActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("customer", customer);
                intent.putExtra("orderDetails", od);
                fragment.startActivity(intent);

            }
        });

        holder.lbl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = od.getOrderID();

                Intent intent = new Intent(view.getContext(), TransactionHistoryActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("customer", customer);
                intent.putExtra("orderDetails", od);
                fragment.startActivity(intent);

            }
        });

        holder.orderID.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                orderID = od.getOrderID();

                Intent intent = new Intent(view.getContext(), TransactionHistoryActivity.class);
                intent.putExtra("orderID", orderID);
                intent.putExtra("language", isEnglish);
                intent.putExtra("customer", customer);
                intent.putExtra("orderDetails", od);
                fragment.startActivity(intent);

            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subtotal,lbl, orderID;

        public MyViewHolder(View view) {
            super(view);
            lbl = (TextView) view.findViewById(R.id.lbl);
            orderID = (TextView) view.findViewById(R.id.orderID);
            subtotal = (TextView) view.findViewById(R.id.subtotal);
        }

        public void bindContent(OrderDetails od) {
            DecimalFormat df = new DecimalFormat("#0.00");

            if (isEnglish.equals("Yes")) {
                 lbl.setText("from order");
            } else {
                 lbl.setText("从订单号");
            }

            double subtotalAmt = od.getSubtotal();
            double paidAmt = od.getPaidAmt();
            double refundAmt = od.getRefundSubtotal();

            //negative is redemption (paid with wallet)
            //positive is refunds (reduce, return, cancelled)

            //if refund is $0, then is redemption
            if (refundAmt == 0) {
                if (paidAmt < subtotalAmt) {
                    //wallet deduction is the difference
                    subtotal.setText("- $" + df.format(subtotalAmt - paidAmt));
                    subtotal.setTextColor(Color.parseColor("#FF0000"));     //red
                } else if (paidAmt == 0) {
                    //full deduction from wallet
                    subtotal.setText("- $" + df.format(subtotalAmt*1.07));
                    subtotal.setTextColor(Color.parseColor("#FF0000"));     //red


                } else {
                    //do not show transaction
                }
            } else {
                //refunds
                subtotal.setText("+ $" + df.format(refundAmt));
                subtotal.setTextColor(Color.parseColor("#008000"));     //green
            }

            orderID.setText("#" + od.getOrderID());
        }
    }

    public void update(ArrayList<OrderDetails> orderList){
        odList = orderList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (odList == null ) {
            return 0;
        }
        return odList.size();
    }
}


