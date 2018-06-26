package com.limkee.catalogue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.limkee.entity.Order;
import com.limkee.entity.Product;
import com.limkee.order.ConfirmOrderActivity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.limkee.R;


/**
 * Created by Xin Yi on 24/4/2018.
 */

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder>  {
    private ArrayList<Product> catalogueList;
    private CatalogueFragment fragment;
    public static ArrayList<Product> orderList;
    private String[] qtyDataSet;
    boolean valueChanged;
    View itemView;
    boolean focus = true;
    String isEnglish;
    private RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public CatalogueAdapter(CatalogueFragment fragment, String isEnglish) {
        this.fragment = fragment;

        this.isEnglish = isEnglish;
    }


    public CatalogueAdapter(CatalogueFragment fragment, ArrayList<Product> catalogueList, String[] qtyDataSet, ArrayList<Product> tempOrderList, String isEnglish) {
        this.fragment = fragment;
        this.catalogueList = catalogueList;
        this.qtyDataSet = qtyDataSet;
        this.orderList = tempOrderList;
        this.isEnglish = isEnglish;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
         itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catalogue_products, parent, false);
        ViewHolder vh = new ViewHolder(itemView, new QuantityEditTextListener());
        CatalogueFragment.confirmOrder.setVisibility(View.VISIBLE);
        return vh;
    }
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Product product = catalogueList.get(position);
        holder.bindContent(product);
        holder.quantityEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.qty.setText(qtyDataSet[holder.getAdapterPosition()]);

        DecimalFormat df = new DecimalFormat("#0.00");
        double unitSub = product.getDefaultQty() * product.getUnitPrice();
        holder.unitSubtotal.setText("$" + df.format(unitSub));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView description, description2, unitPrice, unitSubtotal;
        EditText qty;
        Button confirmOrder;
        public QuantityEditTextListener quantityEditTextListener;

        ImageView image;

        public ViewHolder(View view, QuantityEditTextListener quantityEditTextListener) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
            qty = (EditText) view.findViewById(R.id.qty);
            unitPrice = (TextView) view.findViewById(R.id.price);
            image = (ImageView) view.findViewById(R.id.image);
            confirmOrder = (Button) view.findViewById(R.id.btnNext);
            unitSubtotal = (TextView) view.findViewById(R.id.unitSubtotal);
            this.quantityEditTextListener = quantityEditTextListener;
            this.qty.addTextChangedListener(quantityEditTextListener);
        }

        public void bindContent(final Product product) {
            DecimalFormat df = new DecimalFormat("#0.00");

            if (isEnglish.equals("Yes")) {
                description.setText(product.getDescription());
            } else {
                description.setText(product.getDescription2());
            }
            qty.setText(Integer.toString(product.getDefaultQty()));
            unitPrice.setText(df.format(product.getUnitPrice()));
            double unitSub = product.getDefaultQty() * product.getUnitPrice();
            unitSubtotal.setText("$" + df.format(unitSub));

            Picasso.with(fragment.getContext()).load(product.getImageUrl())
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(image);


        /*
        //hide next button when edit text is clicked
            qty.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    CatalogueFragment.confirmOrder.setVisibility(View.INVISIBLE);

                    return false;
                }
            });
        */
            qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {

                    if(!hasFocus && valueChanged) {

                        //CatalogueFragment.confirmOrder.setVisibility(View.VISIBLE);

                        //check that quantity is not left blank. If not, reset it back to default prefix quantity
                        if (qty.getText().toString().equals("")){

                            if (isEnglish.equals("Yes")){
                                final Toast tag = Toast.makeText(itemView.getContext(), "Please fill in quantity for " + product.getDescription() + ".",  Toast.LENGTH_SHORT);
                                new CountDownTimer(10000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        tag.show();
                                    }

                                    public void onFinish() {
                                        tag.show();
                                    }

                                }.start();
                            } else {
                                final Toast tag = Toast.makeText(itemView.getContext(), "请填写" + product.getDescription2() + "的数量",  Toast.LENGTH_SHORT);
                                new CountDownTimer(10000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        tag.show();
                                    }

                                    public void onFinish() {
                                        tag.show();
                                    }

                                }.start();
                            }

                            qty.setText(Integer.toString(product.getDefaultQty()));

                        } else {
                            //check if qty is in correct multiples
                            int qtyMultiples = product.getQtyMultiples();
                            int quantity = Integer.parseInt(qty.getText().toString());

                            //show error message for 0
                            if (product.getDefaultQty() != 0 && quantity == 0) {
                                if (isEnglish.equals("Yes")) {
                                    final Toast tag = Toast.makeText(itemView.getContext(), "Quantity cannot be 0 for " + product.getDescription() + ".", Toast.LENGTH_SHORT);
                                    tag.show();
                                    new CountDownTimer(10000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            tag.show();
                                        }

                                        public void onFinish() {
                                            tag.show();
                                        }

                                    }.start();
                                } else {
                                    final Toast tag = Toast.makeText(itemView.getContext(), product.getDescription2() + "的数量有误， 数量不能是零", Toast.LENGTH_SHORT);
                                    tag.show();
                                    new CountDownTimer(10000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            tag.show();
                                        }

                                        public void onFinish() {
                                            tag.show();
                                        }

                                    }.start();
                                }

                                //reset quantity to default prefix
                                product.setDefaultQty(product.getDefaultQty());
                                qty.setText(Integer.toString(product.getDefaultQty()));
                                DecimalFormat df = new DecimalFormat("#0.00");
                                double unitSub = product.getDefaultQty() * product.getUnitPrice();
                                unitSubtotal.setText("$" + df.format(unitSub));
                            }

                            //show error message for abt 10s
                            if (quantity % qtyMultiples != 0) {
                                if (isEnglish.equals("Yes")){
                                    final Toast tag = Toast.makeText(itemView.getContext(), "Incorrect quantity for " + product.getDescription() + ". Quantity must be in multiples of " + qtyMultiples + ". Eg: " + qtyMultiples + " , " + (qtyMultiples + qtyMultiples) + ", " + (qtyMultiples + qtyMultiples + qtyMultiples) + " and so on.", Toast.LENGTH_SHORT);
                                    tag.show();
                                    new CountDownTimer(10000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            tag.show();
                                        }

                                        public void onFinish() {
                                            tag.show();
                                        }

                                    }.start();
                                } else {
                                    final Toast tag = Toast.makeText(itemView.getContext(), product.getDescription2() + "的数量有误. 数量必须是" + qtyMultiples + "的倍数，例如" + qtyMultiples + "，"+ (qtyMultiples+qtyMultiples) + "等等", Toast.LENGTH_SHORT);
                                    tag.show();
                                    new CountDownTimer(10000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            tag.show();
                                        }

                                        public void onFinish() {
                                            tag.show();
                                        }

                                    }.start();
                                }

                                //reset quantity to default prefix
                                product.setDefaultQty(product.getDefaultQty());
                                qty.setText(Integer.toString(product.getDefaultQty()));
                                DecimalFormat df = new DecimalFormat("#0.00");
                                double unitSub = product.getDefaultQty() * product.getUnitPrice();
                                unitSubtotal.setText("$" + df.format(unitSub));

                            } else {
                                //recalculate unit subtotal and total subtotal
                                product.setDefaultQty(quantity);
                                //update subtotal
                                CatalogueFragment.updateSubtotal(orderList);
                                //update unit subtotal
                                DecimalFormat df = new DecimalFormat("#0.00");
                                double unitSub = product.getDefaultQty() * product.getUnitPrice();
                                unitSubtotal.setText("$" + df.format(unitSub));
                            }
                        }
                    }
                    valueChanged = false;
                }
            });
        }
    }

    public void update(String[] qtyDataSet, ArrayList<Product> catalogueList, ArrayList<Product> tempOrderList){
        this.qtyDataSet = qtyDataSet;
        this.catalogueList = catalogueList;
        this.orderList = tempOrderList;
        notifyDataSetChanged();
    }

    private class QuantityEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            qtyDataSet[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            valueChanged = true;
        }
    }

    @Override
    public int getItemCount() {
       // return catalogueList.size();
        if ( qtyDataSet != null){
            return  qtyDataSet.length;
        }
        else {
            return 0;
        }

    }

    public static ArrayList<Product> getOrderList(){
        return orderList;
    }
}
