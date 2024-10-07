package com.capx.investing;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AlphaVantageResponse {

    @SerializedName("Meta Data")
    private MetaData metaData;

    @SerializedName("Information")
    private String information;

    @SerializedName("Time Series (5min)")
    private Map<String, StockData> timeSeries;

    @SerializedName("Error Message")
    private String errorMessage;

    public MetaData getMetaData() {
        return metaData;
    }
    public String getInformation() {
        return information;
    }

    public Map<String, StockData> getTimeSeries() {
        return timeSeries;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public class MetaData {
        @SerializedName("2. Symbol")
        String symbol;
        @SerializedName("3. Last Refreshed")
        private String lastRefreshed;
    }

    public class StockData {
        @SerializedName("1. open")
        private String open;

        @SerializedName("2. high")
        private String high;

        @SerializedName("3. low")
        private String low;

        @SerializedName("4. close")
        private String close;

        @SerializedName("5. volume")
        private String volume;
        public String getClose() {
            return close;
        }



    }
}
