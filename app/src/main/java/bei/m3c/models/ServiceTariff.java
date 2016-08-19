package bei.m3c.models;

import java.math.BigDecimal;

public class ServiceTariff {

    public int id;
    public String roomCategoryName;
    public BigDecimal priceShift;
    public BigDecimal priceOvernight;
    public boolean showPriceOvernight;
    public String turnDuration;
    public String overnightStart;
    public String overnightFinish;
    public String longTurnStart;
    public String longTurnFinish;
    public boolean showOvernightStartFinish;
    public boolean showLongTurnStartFinish;

    public ServiceTariff(int id, String roomCategoryName, BigDecimal priceShift, BigDecimal priceOvernight,
                         boolean showPriceOvernight, String turnDuration, boolean showLongTurnStartFinish,
                         String overnightStart, String overnightFinish, String longTurnStart, String longTurnFinish,
                         boolean showOvernightStartFinish) {
        this.id = id;
        this.roomCategoryName = roomCategoryName;
        this.priceShift = priceShift;
        this.priceOvernight = priceOvernight;
        this.showPriceOvernight = showPriceOvernight;
        this.turnDuration = turnDuration;
        this.showLongTurnStartFinish = showLongTurnStartFinish;
        this.overnightStart = overnightStart;
        this.overnightFinish = overnightFinish;
        this.longTurnStart = longTurnStart;
        this.longTurnFinish = longTurnFinish;
        this.showOvernightStartFinish = showOvernightStartFinish;
    }
}
