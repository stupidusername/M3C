package bei.m3c.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class ServiceTariff {

    public int id;

    @SerializedName("room_category_name")
    public String roomCategoryName;

    @SerializedName("price_shift")
    public BigDecimal priceShift;

    @SerializedName("price_overnight")
    public BigDecimal priceOvernight;

    @SerializedName("show_price_overnight")
    public boolean showPriceOvernight;

    @SerializedName("turn_duration")
    public String turnDuration;

    public ServiceTariff(int id, String roomCategoryName, BigDecimal priceShift, BigDecimal priceOvernight,
                         boolean showPriceOvernight, String turnDuration) {
        this.id = id;
        this.roomCategoryName = roomCategoryName;
        this.priceShift = priceShift;
        this.priceOvernight = priceOvernight;
        this.showPriceOvernight = showPriceOvernight;
        this.turnDuration = turnDuration;
    }
}
