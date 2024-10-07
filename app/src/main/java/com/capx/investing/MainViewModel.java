package com.capx.investing;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<StockDetails> stockDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<StockDetails> getStockDetails() {
        return stockDetails;
    }

    public LiveData<Boolean> getLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public void loadStockData(String ticker) {
        isLoading.setValue(true);
        loadStockPrice(ticker, new StockPriceCallback() {
            @Override
            public void onSuccess(String price, double percentChange) {
                fetchStockName(ticker, new StockNameCallback() {
                    @Override
                    public void onSuccess(String name) {
                        stockDetails.setValue(new StockDetails(name, price, percentChange));
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onFailure(String error) {
                        errorMessage.setValue(error);
                        isLoading.setValue(false);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }
    private void loadStockPrice(String ticker, StockPriceCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.alphavantage.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StockApiService apiService = retrofit.create(StockApiService.class);
        Call<AlphaVantageResponse> call = apiService.getStockPrice(
                "TIME_SERIES_INTRADAY",
                ticker,
                "5min",
                "PJN1Q0KOMIB7ZDCI"
        );

        call.enqueue(new Callback<AlphaVantageResponse>() {
            @Override
            public void onResponse(Call<AlphaVantageResponse> call, Response<AlphaVantageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlphaVantageResponse stockResponse = response.body();
                    if (stockResponse.getInformation() != null) {
                        callback.onFailure("API rate limit reached: " + stockResponse.getInformation());
                        return;
                    }
                    if (stockResponse.getErrorMessage() != null) {
                        callback.onFailure("Invalid stock symbol. Please try again.");
                    } else {
                        Map<String, AlphaVantageResponse.StockData> timeSeries = stockResponse.getTimeSeries();
                        List<String> times = new ArrayList<>(timeSeries.keySet());
                        Collections.sort(times, Collections.reverseOrder());

                        if (times.size() >= 2) {
                            AlphaVantageResponse.StockData latestStock = timeSeries.get(times.get(0));
                            AlphaVantageResponse.StockData previousStock = timeSeries.get(times.get(1));

                            double latestPrice = Double.parseDouble(latestStock.getClose());
                            double previousPrice = Double.parseDouble(previousStock.getClose());
                            double percentageChange = ((latestPrice - previousPrice) / previousPrice) * 100;

                            callback.onSuccess(String.format("$%.2f", latestPrice), percentageChange);
                        } else {
                            callback.onFailure("Error fetching stock data.");
                        }
                    }
                } else {
                    callback.onFailure("Error fetching stock data.");
                }
            }

            @Override
            public void onFailure(Call<AlphaVantageResponse> call, Throwable t) {
                callback.onFailure("Network error.");
            }
        });
    }
    private void fetchStockName(String ticker, StockNameCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.api-ninjas.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiNinjasService apiNinjasService = retrofit.create(ApiNinjasService.class);
        Call<ApiNinjasResponse> call = apiNinjasService.getStockName(ticker, "OPakK7lmBBhCx+Lakh1IGQ==14OypK9nRf0bFDPG");

        call.enqueue(new Callback<ApiNinjasResponse>() {
            @Override
            public void onResponse(Call<ApiNinjasResponse> call, Response<ApiNinjasResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String stockNameStr = response.body().getName();
                    callback.onSuccess(stockNameStr);
                } else {
                    callback.onFailure("Failed to fetch stock name.");
                }
            }

            @Override
            public void onFailure(Call<ApiNinjasResponse> call, Throwable t) {
                callback.onFailure("Network error.");
            }
        });
    }
    interface StockPriceCallback {
        void onSuccess(String price, double percentChange);
        void onFailure(String error);
    }
    interface StockNameCallback {
        void onSuccess(String name);
        void onFailure(String error);
    }
}
