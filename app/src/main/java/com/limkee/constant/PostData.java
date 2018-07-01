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

        @GET("get-order/latestorder")
        Call<ArrayList<Product>> getQuickOrderCatalogue(@Query("companyCode") String companyCode);

        @GET("get-order/currentorder")
        Call<ArrayList<Order>> getCurrentOrders(@Query("companyCode") String companyCode);

        @GET("get-order/currentorderdetails")
        Call<OrderDetails> getCurrentOrderDetails(@Query("orderNo") String orderNo);

        @GET("get-order/currentorderquantity")
        Call<ArrayList<OrderQuantity>> getCurrentOrderQuantity(@Query("orderNo") String orderNo);

        @GET("get-order/orderhistory")
        Call<ArrayList<Order>> getOrderHistory(@Query("companyCode") String companyCode);

        @GET("get-order/orderhistorydetails")
        Call<OrderDetails> getOrderHistoryDetails(@Query("orderNo") String orderNo);

        @GET("get-order/orderhistoryquantity")
        Call<ArrayList<OrderQuantity>> getOrderHistoryQuantity(@Query("orderNo") String orderNo);

        @GET("get-order/cancelledorder")
        Call<ArrayList<Order>> getCancelledOrder(@Query("companyCode") String companyCode);

        @GET("get-order/cancelledorderdetails")
        Call<OrderDetails> getCancelledOrderDetails(@Query("orderNo") String orderNo);

        @GET("get-order/cancelledorderquantity")
        Call<ArrayList<OrderQuantity>> getCancelledOrderQuantity(@Query("orderNo") String orderNo);

}
