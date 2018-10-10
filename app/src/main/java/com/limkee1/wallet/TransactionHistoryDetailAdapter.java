package com.limkee1.wallet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.limkee1.R;
import com.limkee1.entity.OrderDetails;
import com.limkee1.entity.OrderQuantity;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TransactionHistoryDetailAdapter  extends RecyclerView.Adapter<TransactionHistoryDetailAdapter.MyViewHolder>  {
    private ArrayList<OrderQuantity> oqList;
    private OrderDetails od;
    private TransactionHistoryDetailsFragment fragment;
    private String isEnglish;
    private String uom = "";
    private String status;

    public TransactionHistoryDetailAdapter(TransactionHistoryDetailsFragment fragment, String isEnglish, String status) {
        this.fragment = fragment;
        this.isEnglish = isEnglish;
        this.status = status;
    }

    @Override
    public TransactionHistoryDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_details_product, parent, false);
        return new TransactionHistoryDetailAdapter.MyViewHolder(itemView);
    }
    public void onBindViewHolder(final TransactionHistoryDetailAdapter.MyViewHolder holder, int position) {

        OrderQuantity product = oqList.get(position);
        holder.bindContent(product);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description, qty, unitSubtotal, unitOfMetric;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
            qty = (TextView) view.findViewById(R.id.qty);
            unitSubtotal = (TextView) view.findViewById(R.id.unitSubtotal);
            unitOfMetric = (TextView) view.findViewById(R.id.uom);
        }

        public void bindContent(OrderQuantity product) {
            DecimalFormat df = new DecimalFormat("#0.00");
            if (isEnglish.equals("Yes")) {
                description.setText(product.getDescription());
                if (product.getDescription().equals("CS")){
                    uom = "btl";
                } else {
                    uom = "pcs";
                }
            } else {
                description.setText(product.getDescription2());
                uom = product.getUom();
            }

            double unitSub = 0;

            if (status.equals("Pending Delivery")) {
                qty.setText(Integer.toString(product.getReduceQty()));
                unitSub = product.getReduceQty() * product.getUnitPrice();
            } else if (status.equals("Delivered")){
                qty.setText(Integer.toString(product.getReturnQty()));
                unitSub = product.getReturnQty() * product.getUnitPrice();
            } else if (status.equals("Cancelled")){
                qty.setText(Integer.toString(product.getQty()));
                unitSub = product.getQty() * product.getUnitPrice();
            } else {
                //do nothing
            }

            unitOfMetric.setText(uom);

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
