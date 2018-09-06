package com.limkee.constant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import com.limkee.entity.Order;
import com.limkee.entity.OrderDetails;
import com.limkee.entity.OrderQuantity;
import com.limkee.entity.Product;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
        Call<ArrayList<Order>> getCancelledOrders(@Query("companyCode") String companyCode);

        @GET("get-order/cancelledorderdetails")
        Call<OrderDetails> getCancelledOrderDetails(@Query("orderNo") String orderNo);

        @GET("get-order/cancelledorderquantity")
        Call<ArrayList<OrderQuantity>> getCancelledOrderQuantity(@Query("orderNo") String orderNo);

        @FormUrlEncoded
        @POST("add-order/salesorder")
        Observable<String> addSalesOrder(@Field("debtorCode") String debtorCode, @Field("PaperBagRequired") int PaperBagRequired);

        @FormUrlEncoded
        @POST("add-order/salesorderdetails")
        Observable<Boolean> addSalesOrderDetails(@Field("deliveryDate") String deliveryDate, @Field("subtotal") double subtotal, @Field("orderNo") String orderNo);

        @FormUrlEncoded
        @POST("add-order/salesorderquantity")
        Observable<Integer> addSalesOrderQuantity(@Field("itemQuantity") ArrayList<String> itemQuantity, @Field("orderNo") String orderNo);

        @GET("dashboard/gettopproducts")
        Call<Map<String,Integer>> getTopPurchasedProducts(@Query("companyCode") String companyCode, @Query("selectedMonth") String selectedMonth, @Query("selectedYear") String selectedYear, @Query("language") String language);

        @GET("dashboard/getcustomersales")
        Call<LinkedHashMap<String,Double>> getFilteredCustomerSales(@Query("companyCode") String companyCode, @Query("selectedYear") String selectedYear);

        @GET("dashboard/getaveragesales")
        Call<LinkedHashMap<String,Double>> getAverageSales(@Query("selectedYear") String selectedYear);

        @GET("dashboard/getearliestyear")
        Call<Integer> getEarliestYear(@Query("companyCode") String companyCode);

}

