package com.limkee1.catalogue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.limkee1.entity.Product;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.limkee1.R;

public class QuickReorderConfirmOrderAdapter extends RecyclerView.Adapter<QuickReorderConfirmOrderAdapter.MyViewHolder>  {
    private ArrayList<Product> orderList;
    private QuickReorderConfirmOrderFragment fragment;
    private String isEnglish;
    private String uom = "";

    public QuickReorderConfirmOrderAdapter(QuickReorderConfirmOrderFragment fragment, ArrayList<Product> orderList, String isEnglish) {
        this.fragment = fragment;
        this.orderList = orderList;
        this.isEnglish = isEnglish;
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
        TextView description, qty, unitSubtotal, unitOfMetric;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
            qty = (TextView) view.findViewById(R.id.qty);
            unitSubtotal = (TextView) view.findViewById(R.id.unitSubtotal);
            unitOfMetric = (TextView) view.findViewById(R.id.uom);
        }

        public void bindContent(Product product) {
            DecimalFormat df = new DecimalFormat("#0.00");
            if (isEnglish.equals("Yes")) {
                description.setText(product.getDescription());
                if (product.getItemCode().equals("CS")){
                    uom = "btl";
                } else {
                    uom = "pcs";
                }
            } else {
                description.setText(product.getDescription2());
                uom = product.getUom();
            }

            qty.setText(Integer.toString(product.getDefaultQty()));
            unitOfMetric.setText(uom);
            double unitSub = product.getDefaultQty() * product.getUnitPrice();
            unitSubtotal.setText("$" + df.format(unitSub));
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

}
