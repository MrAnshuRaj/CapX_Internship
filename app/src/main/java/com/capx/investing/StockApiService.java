package com.capx.investing;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface StockApiService {
    @GET("query")
    Call<AlphaVantageResponse> getStockPrice(
            @Query("function") String function,
            @Query("symbol") String symbol,
            @Query("interval") String interval,
            @Query("apikey") String apiKey
    );
}
