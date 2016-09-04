package bei.m3c.commands;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Arrays;
import java.util.Date;

public class TPCAccountInfo extends BaseCommand {

    public static final String TAG = "TPCAccountInfo";
    public static final byte VALUE = 6;
    public static final int OFFSET_DATE = 1;
    public static final int OFFSET_TIME = 7;
    public static final int OFFSET_SERVICE_OPEN = 11;
    public static final int OFFSET_SHIFT_START_TIME = 12;
    public static final int OFFSET_SHIFT_END_TIME = 16;
    public static final int OFFSET_ALARM_TIME = 20;
    public static final int OFFSET_BILL_LODGING = 24;
    public static final int OFFSET_BILL_SURCHARGE = 30;
    public static final int OFFSET_BILL_BAR = 36;
    public static final int OFFSET_BILL_BONUS = 42;
    public static final int OFFSET_BILL_DISCOUNT = 48;
    public static final int OFFSET_BILL_PAID = 54;
    public static final int OFFSET_BILL_TOTAL = 60;
    public static final int OFFSET_SPECIAL_OFFER = 66;
    public static final int COMMAND_LENGTH = 96;

    public Date date;
    public Time time;
    public boolean serviceOpen;
    public Time shiftStartTime;
    public Time shiftEndTime;
    public Time alarmTime;
    public BigDecimal billLodging;
    public BigDecimal billSurcharge;
    public BigDecimal billBar;
    public BigDecimal billBonus;
    public BigDecimal billDiscount;
    public BigDecimal billPaid;
    public BigDecimal billTotal;
    public String specialOffer;

    public TPCAccountInfo(byte[] command) {
        super(TAG, VALUE, Arrays.copyOfRange(command, OFFSET_DATE, COMMAND_LENGTH));
        date = toDate(Arrays.copyOfRange(command, OFFSET_DATE, OFFSET_TIME));
        time = toTime(Arrays.copyOfRange(command, OFFSET_TIME, OFFSET_SERVICE_OPEN));
        serviceOpen = toBoolean(command[OFFSET_SERVICE_OPEN]);
        shiftStartTime = toTime(Arrays.copyOfRange(command, OFFSET_SHIFT_START_TIME, OFFSET_SHIFT_END_TIME));
        shiftEndTime = toTime(Arrays.copyOfRange(command, OFFSET_SHIFT_END_TIME, OFFSET_ALARM_TIME));
        alarmTime = toTime(Arrays.copyOfRange(command, OFFSET_ALARM_TIME, OFFSET_BILL_LODGING));
        billLodging = toBigDecimal(Arrays.copyOfRange(command, OFFSET_BILL_LODGING, OFFSET_BILL_SURCHARGE));
        billSurcharge = toBigDecimal(Arrays.copyOfRange(command, OFFSET_BILL_SURCHARGE, OFFSET_BILL_BAR));
        billBar = toBigDecimal(Arrays.copyOfRange(command, OFFSET_BILL_BAR, OFFSET_BILL_BONUS));
        billBonus = toBigDecimal(Arrays.copyOfRange(command, OFFSET_BILL_BONUS, OFFSET_BILL_DISCOUNT));
        billDiscount = toBigDecimal(Arrays.copyOfRange(command, OFFSET_BILL_DISCOUNT, OFFSET_BILL_TOTAL));
        billPaid = toBigDecimal(Arrays.copyOfRange(command, OFFSET_BILL_PAID, OFFSET_BILL_TOTAL));
        billTotal = toBigDecimal(Arrays.copyOfRange(command, OFFSET_BILL_TOTAL, OFFSET_SPECIAL_OFFER));
        specialOffer = toString(Arrays.copyOfRange(command, OFFSET_SPECIAL_OFFER, COMMAND_LENGTH));
    }
}