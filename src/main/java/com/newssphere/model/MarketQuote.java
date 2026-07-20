package com.newssphere.model;

/**
 * POJO tạm thời cho widget thị trường (vàng, crypto, chứng khoán) bên cột phải.
 * TODO: Thay bằng service gọi API giá thật (vd. CoinGecko, Alpha Vantage...) sau này.
 */
public class MarketQuote {

    private String symbol;
    private String name;
    private String price;
    private double changePercent;
    private String assetClass; // "stocks" | "crypto" | "commodities"

    public MarketQuote() {}

    public MarketQuote(String symbol, String name, String price, double changePercent) {
        this(symbol, name, price, changePercent, "stocks");
    }

    public MarketQuote(String symbol, String name, String price, double changePercent, String assetClass) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.changePercent = changePercent;
        this.assetClass = assetClass;
    }

    public String getAssetClass() { return assetClass; }
    public void setAssetClass(String assetClass) { this.assetClass = assetClass; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }

    public boolean isPositive() { return changePercent >= 0; }
}
