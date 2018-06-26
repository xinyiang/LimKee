package com.limkee.constant;

import java.util.ArrayList;
import com.limkee.entity.Order;
import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;
import com.limkee.entity.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Xin Yi on 20/5/2018.
 */


public interface PostData {

        @GET("get-catalogue")
        Call<ArrayList<Product>> getCatalogue();

        @GET("get-order/currentorder")
        Call<ArrayList<Order>> getCurrentOrders(@Query("companyCode") String companyCode);

        @GET("get-order/currentorderdetails")
        Call<OrderDetails> getCurrentOrderDetails(@Query("orderNo") String orderNo);

        @GET("get-order/currentorderquantity")
        Call<ArrayList<OrderQuantity>> getCurrentOrderQuantity(@Query("orderNo") String orderNo);

}
