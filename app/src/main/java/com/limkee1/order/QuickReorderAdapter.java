package com.limkee1.order;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.limkee1.catalogue.ProductDetailsActivity;
import com.limkee1.entity.Customer;
import com.limkee1.entity.Product;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.limkee1.R;

public class QuickReorderAdapter extends RecyclerView.Adapter<QuickReorderAdapter.ViewHolder>  {
    private ArrayList<Product> catalogueList;
    private QuickReorderFragment fragment;
    public static ArrayList<Product> orderList;
    private String[] qtyDataSet;
    boolean valueChanged;
    View itemView;
    String isEnglish;
    public RecyclerView mRecyclerView;
    private String uom ="";
    public Customer customer;


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public QuickReorderAdapter(QuickReorderFragment fragment, String isEnglish, Customer customer) {
        this.fragment = fragment;
        this.isEnglish = isEnglish;
        this.customer = customer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catalogue_products, parent, false);
        ViewHolder vh = new ViewHolder(itemView, new QuantityEditTextListener());
        QuickReorderFragment.confirmOrder.setVisibility(View.VISIBLE);
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
        TextView description, unitOfMetric, unitPrice, unitSubtotal;
        EditText qty;
        Button confirmOrder;
        public QuantityEditTextListener quantityEditTextListener;

        ImageView image;

        public ViewHolder(View view, QuantityEditTextListener quantityEditTextListener) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
            qty = (EditText) view.findViewById(R.id.qty);
            unitOfMetric = (TextView) view.findViewById(R.id.uom);
            unitPrice = (TextView) view.findViewById(R.id.price);
            image = (ImageView) view.findViewById(R.id.image);
            confirmOrder = (Button) view.findViewById(R.id.btnPlaceOrder);
            unitSubtotal = (TextView) view.findViewById(R.id.unitSubtotal);
            this.quantityEditTextListener = quantityEditTextListener;
            this.qty.addTextChangedListener(quantityEditTextListener);
        }

        public void bindContent(final Product product) {
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
            unitPrice.setText(df.format(product.getUnitPrice()));
            double unitSub = product.getDefaultQty() * product.getUnitPrice();
            unitSubtotal.setText("$" + df.format(unitSub));

            Picasso.with(fragment.getContext()).load(product.getImageUrl())
                    .error(R.mipmap.launchicon)
                    .placeholder(R.mipmap.launchicon)
                    .into(image);

            //update item subtotal in the particular row once user select tick in keyboard
            qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId== EditorInfo.IME_ACTION_DONE){
                        //Clear focus here from edittext.
                        qty.clearFocus();
                        InputMethodManager imm = (InputMethodManager)itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(qty.getWindowToken(), 0);
                        QuickReorderFragment.confirmOrder.setVisibility(View.VISIBLE);
                        QuickReorderFragment.lbl_subtotal.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });

            qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (qty.getText().toString().equals("0")) {
                            qty.setText("");
                        }
                    } else {
                        if (!hasFocus && valueChanged) {
                            QuickReorderFragment.confirmOrder.setVisibility(View.VISIBLE);
                            QuickReorderFragment.lbl_subtotal.setVisibility(View.VISIBLE);
                            //check that if quantity is left blank, set qty to 0
                            if (qty.getText().toString().equals("")) {
                                product.setDefaultQty(0);
                                qty.setText("0");
                                //update subtotal
                                QuickReorderFragment.updateSubtotal(orderList);
                                //update unit subtotal
                                DecimalFormat df = new DecimalFormat("#0.00");
                                double unitSub = 0 * product.getUnitPrice();
                                unitSubtotal.setText("$" + df.format(unitSub));

                            } else {
                                //check if qty is in correct multiples
                                int qtyMultiples = product.getQtyMultiples();
                                int quantity = Integer.parseInt(qty.getText().toString());

                                if (quantity % qtyMultiples != 0) {
                                    if (isEnglish.equals("Yes")) {
                                        final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(itemView.getContext())
                                                .setMessage("Incorrect quantity for " + product.getDescription() + ". Quantity must be in multiples of " + qtyMultiples + ". Eg: " + qtyMultiples + " , " + (qtyMultiples + qtyMultiples) + ", " + (qtyMultiples + qtyMultiples + qtyMultiples) + " and so on.")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //finish();
                                                        //reset quantity to 0 if input is wrong
                                                        int quantity = 0;
                                                        product.setDefaultQty(quantity);
                                                        qty.setText(Integer.toString(product.getDefaultQty()));
                                                        DecimalFormat df = new DecimalFormat("#0.00");
                                                        double unitSub = quantity * product.getUnitPrice();
                                                        unitSubtotal.setText("$" + df.format(unitSub));
                                                    }
                                                })
                                                .show();
                                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                        textView.setTextSize(20);
                                    } else {
                                        final android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(itemView.getContext())
                                                .setMessage(product.getDescription2() + "的数量有误, 数量必须是" + qtyMultiples + "的倍数，例如" + qtyMultiples + "，" + (qtyMultiples + qtyMultiples) + "等等")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //finish();
                                                        //reset quantity to 0 if input is wrong
                                                        int quantity = 0;
                                                        product.setDefaultQty(quantity);
                                                        qty.setText(Integer.toString(product.getDefaultQty()));
                                                        DecimalFormat df = new DecimalFormat("#0.00");
                                                        double unitSub = quantity * product.getUnitPrice();
                                                        unitSubtotal.setText("$" + df.format(unitSub));
                                                    }
                                                })
                                                .show();
                                        TextView textView = (TextView) ad.findViewById(android.R.id.message);
                                        textView.setTextSize(20);
                                    }

                                } else {
                                    product.setDefaultQty(quantity);
                                    //update subtotal
                                    QuickReorderFragment.updateSubtotal(orderList);
                                    //update unit subtotal
                                    DecimalFormat df = new DecimalFormat("#0.00");
                                    double unitSub = quantity * product.getUnitPrice();
                                    unitSubtotal.setText("$" + df.format(unitSub));
                                }
                            }
                        }
                        valueChanged = false;
                    }
                }
            });

            //update when on text change instead of clicking tick in keyboard
            qty.clearFocus();
            qty.addTextChangedListener(new TextWatcher() {
                int quantity = 0;
                public void afterTextChanged(Editable s) {
                    try {

                        if (qty.getText().toString().equals("")){
                            quantity = 0;
                        } else {
                            quantity = Integer.parseInt(qty.getText().toString());
                        }

                        product.setDefaultQty(quantity);           //did not validate qty multiples, do it at Next button
                        //update unit subtotal
                        DecimalFormat df = new DecimalFormat("#0.00");
                        double unitSub = quantity * product.getUnitPrice();
                        unitSubtotal.setText("$" + df.format(unitSub));
                        //update subtotal
                        QuickReorderFragment.updateSubtotal(orderList);

                    } catch(Exception e) {
                        quantity = 0;

                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            image.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    System.out.println(product.getDescription());
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("product", product);
                    bundle.putString("language", isEnglish);
                    Intent intent = new Intent(view.getContext(), ProductDetailsActivity.class);
                    intent.putExtra("product", product);
                    intent.putExtra("language",isEnglish);
                    fragment.startActivity(intent);
                }
            });
        }
    }

    public void update(String[] qtyDataSet, ArrayList<Product> catalogueList, ArrayList<Product> tempOrderList){
        this.qtyDataSet = qtyDataSet;
        this.catalogueList = catalogueList;
        this.orderList = tempOrderList;
        QuickReorderFragment.confirmOrder.setVisibility(View.VISIBLE);
        QuickReorderFragment.lbl_subtotal.setVisibility(View.VISIBLE);

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
