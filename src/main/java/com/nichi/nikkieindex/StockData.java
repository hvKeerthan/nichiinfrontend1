package com.nichi.nikkieindex;


public class StockData {
    private String code;
    private String dt;
    private String classification;
    private String code_name;
    private double paf;
    private double price;
    private String sector;
    private double divisor;


    public StockData(String code, String dt, String classification, String codeName, double paf, double price, String sector, Double divisor) {
        this.code = code;
        this.dt = dt;
        this.classification = classification;
        this.code_name = codeName;
        this.paf = paf;
        this.price = price;
        this.sector = sector;
        this.divisor = divisor;

    }

    public String getCode() { return code; }
    public String getDt() { return dt; }
    public String getClassification() { return classification; }
    public String getCode_name() { return code_name; }
    public double getPaf() { return paf; }
    public double getPrice() { return price; }
    public String getSector() { return sector; }
    public double getDivisor() { return divisor; }
    public double getAdjustedPrice() { return price * paf; }
    public double getNikkei225Price(double totalAdjustedPrice, double totalDivisor) {
        return totalDivisor == 0 ? 0 : totalAdjustedPrice / totalDivisor;
    }
}
