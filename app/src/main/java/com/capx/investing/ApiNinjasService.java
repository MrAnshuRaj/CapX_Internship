package com.capx.investing;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiNinjasService {
    @GET("v1/stockprice")
    Call<ApiNinjasResponse> getStockName(
            @Query("ticker") String ticker,
            @Header("X-Api-Key") String apiKey
    );
}
