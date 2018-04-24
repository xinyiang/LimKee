package com.limkee.catalogue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        System.out.println("FOOOOOD " + food + " NAME IS  " + food.getName());
        holder.bindContent(food);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.itemName);
            price = (TextView) view.findViewById(R.id.price);
            thumbnail = (ImageView) view.findViewById(R.id.image);
        }

        public void bindContent(Food food) {
            DecimalFormat df = new DecimalFormat("#.00");
            name.setText(food.getName());
            price.setText("$" + df.format(food.getPrice()));

            /*
            Picasso.with(fragment.getContext()).load(food.getImageUrl())
                    .error(R.drawable.img_placeholder_oneshop)
                    .placeholder(R.drawable.img_placeholder_oneshop)
                    .into(thumbnail);
              */
        }
    }

    @Override
    public int getItemCount() {
        return catalogueList.size();
    }
}
