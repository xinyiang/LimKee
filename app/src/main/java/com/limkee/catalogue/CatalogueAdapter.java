package com.limkee.catalogue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.limkee.entity.Food;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.limkee.R;


/**
 * Created by Xin Yi on 24/4/2018.
 */

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.MyViewHolder>  {
    private ArrayList<Food> catalogueList;
    private CatalogueFragment fragment;

    public CatalogueAdapter(CatalogueFragment fragment, ArrayList<Food> catalogueList) {
        this.fragment = fragment;
        this.catalogueList = catalogueList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catalogue_products, parent, false);
        return new MyViewHolder(itemView);
    }
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Food food = catalogueList.get(position);
        holder.bindContent(food);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description,description2, unitPrice;
        Spinner minQty;

        ImageView image;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
            description2 = (TextView) view.findViewById(R.id.description2);
            //  minQty = (Spinner) findViewById(R.id.minQty);
            unitPrice = (TextView) view.findViewById(R.id.price);
            image = (ImageView) view.findViewById(R.id.image);
        }

        public void bindContent(Food food) {
            DecimalFormat df = new DecimalFormat("#.00");
            description.setText(food.getDescription());
            description2.setText(food.getDescription2());
            //minQty.setText(food.getMinQty());
            unitPrice.setText("$" + df.format(food.getUnitPrice()));


            Picasso.with(fragment.getContext()).load(food.getImageUrl())
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(image);

        }
    }

    @Override
    public int getItemCount() {
        return catalogueList.size();
    }
}
