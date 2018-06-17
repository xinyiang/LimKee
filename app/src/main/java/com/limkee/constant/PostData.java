package com.limkee.constant;

import java.util.ArrayList;
import java.util.Map;
import com.limkee.entity.Product;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import io.reactivex.Observable;

/**
 * Created by Xin Yi on 20/5/2018.
 */


public interface PostData {

        @GET("get-catalogue")
       // Observable<ArrayList<Product>> getCatalogue(@FieldMap Map<String, String> fieldsMap);
        Call<Product> getCatalogue();

}
