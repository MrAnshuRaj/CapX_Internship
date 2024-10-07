package com.capx.investing;

public class StockDetails {
    private String name;
    private String price;
    private double percentChange;

    public StockDetails(String name, String price, double percentChange) {
        this.name = name;
        this.price = price;
        this.percentChange = percentChange;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public double getPercentChange() {
        return percentChange;
    }
}
