package com.limkee1.constant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import com.limkee1.entity.Order;
import com.limkee1.entity.OrderDetails;
import com.limkee1.entity.OrderQuantity;
import com.limkee1.entity.Product;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostData {

    @GET("catalogue/get-catalogue")
    Call<ArrayList<Product>> getCatalogue();

    @GET("catalogue/getlastorder")
    Call<ArrayList<Product>> getQuickOrderCatalogue(@Query("customerCode") String customerCode);

    @GET("catalogue/getlastpaparbag")
    Call<Integer> getLastOrderPaperBag(@Query("customerCode") String customerCode);

    //not used
    @FormUrlEncoded
    @POST("catalogue/getcutofftime")
    Observable<String> getCutOffTime(@Field("customerCode") String customerCode);

    @GET("get-order/currentorder")
    Call<ArrayList<Order>> getCurrentOrders(@Query("customerCode") String customerCode);

    @GET("get-order/currentorderdetails")
    Call<OrderDetails> getCurrentOrderDetails(@Query("orderNo") String orderNo);

    @GET("get-order/currentorderquantity")
    Call<ArrayList<OrderQuantity>> getCurrentOrderQuantity(@Query("orderNo") String orderNo);

    @GET("get-order/orderhistory")
    Call<ArrayList<Order>> getOrderHistory(@Query("customerCode") String customerCode);

    @GET("get-order/orderhistorydetails")
    Call<OrderDetails> getOrderHistoryDetails(@Query("orderNo") String orderNo);

    @GET("get-order/orderhistoryquantity")
    Call<ArrayList<OrderQuantity>> getOrderHistoryQuantity(@Query("orderNo") String orderNo);

    @GET("get-order/cancelledorder")
    Call<ArrayList<Order>> getCancelledOrders(@Query("customerCode") String customerCode);

    @GET("get-order/cancelledorderdetails")
    Call<OrderDetails> getCancelledOrderDetails(@Query("orderNo") String orderNo);

    @GET("get-order/cancelledorderquantity")
    Call<ArrayList<OrderQuantity>> getCancelledOrderQuantity(@Query("orderNo") String orderNo);

    @FormUrlEncoded
    @POST("add-order/salesorder")
    Observable<String> addSalesOrder(@Field("customerCode") String customerCode, @Field("PaperBagRequired") int PaperBagRequired);

    @FormUrlEncoded
    @POST("add-order/salesorderdetails")
    Observable<Boolean> addSalesOrderDetails(@Field("deliveryDate") String deliveryDate, @Field("subtotal") double subtotal, @Field("paidAmt") double paidAmt, @Field("orderNo") String orderNo);

    @FormUrlEncoded
    @POST("add-order/salesorderquantity")
    Observable<Integer> addSalesOrderQuantity(@Field("itemQuantity") ArrayList<String> itemQuantity, @Field("orderNo") String orderNo);

    @GET("dashboard/gettopproducts")
    Call<Map<String,Integer>> getTopPurchasedProducts(@Query("customerCode") String customerCode, @Query("selectedMonth") String selectedMonth, @Query("selectedYear") String selectedYear, @Query("language") String language);

    @GET("dashboard/getcustomersales")
    Call<LinkedHashMap<String,Double>> getFilteredCustomerSales(@Query("customerCode") String customerCode, @Query("selectedYear") String selectedYear);

    @GET("dashboard/getaveragesales")
    Call<LinkedHashMap<String,Double>> getAverageSales(@Query("selectedYear") String selectedYear);

    //not used
    @GET("catalogue/getearliestyear")
    Call<Integer> getOrderYear(@Query("customerCode") String customerCode);

    //not used
    @FormUrlEncoded
    @POST("dashboard/getearliestorderyear")
    Observable<Integer> getEarliestOrderYear(@Field("customerCode") String customerCode);

    @GET("dashboard/getaveragequantitycatalogue")
    Call<ArrayList<Product>> getAverageQuantity(@Query("customerCode") String customerCode);

    @GET("wallet/getwalletamount")
    Call<Double> getCustomerWallet(@Query("customerCode") String customerCode);

    @GET("wallet/gettransactionhistory")
    Call<ArrayList<OrderDetails>> getTransaction(@Query("customerCode") String customerCode);

    @GET("wallet/gettransactionproductdetails")
    Call<ArrayList<OrderQuantity>> getTransactionProductDetails(@Query("orderNo") String orderNo,  @Query("transactionID") int transactionID, @Query("transactionStatus") String transactionStatus);

    @FormUrlEncoded
    @POST("wallet/reducecustomerwalletamt")
    Observable<Boolean> reduceCustomerWalletAmount(@Field("customerCode") String customerCode, @Field("reduceAmt") double reduceAmt);

    @FormUrlEncoded
    @POST("wallet/addtransaction")
    Observable<Boolean> addTransaction(@Field("orderNo") String orderNo, @Field("amount") double amount);
}

