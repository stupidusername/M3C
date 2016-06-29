package bei.m3c.models;

import java.math.BigDecimal;
import java.sql.Time;

import javax.xml.datatype.Duration;

public class ServiceTariff {

    public int id;
    public String roomCategoryName;
    public BigDecimal priceShift;
    public BigDecimal priceOvernight;
    public boolean showPriceOvernight;
    public Duration turnDuration;
    public Time overnightStart;
    public Time overnightFinish;
    public Time longTurnStart;
    public Time longTurnFinish;
    public boolean showOvernightStartFinish;
    public boolean showLongTurnStartFinish;
}
