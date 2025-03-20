package com.nichi.nikkieindex;

import java.util.List;

public class ApiResponse {
    private double adjustedPrice;
    private double nikkeiIndex;
    private List<StockData> stocks;

    public double getAdjustedPrice() { return adjustedPrice; }
    public double getNikkeiIndex() { return nikkeiIndex; }
    public List<StockData> getStocks() { return stocks; }
}
