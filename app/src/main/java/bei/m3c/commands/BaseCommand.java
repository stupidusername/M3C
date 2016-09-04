package bei.m3c.commands;

import android.util.Log;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseCommand {

    public static final String DATE_FORMAT = "ddMMyy";
    public static final String TIME_FORMAT = "HH:mm";

    public String tag;
    public byte value;
    public byte[] params;

    public BaseCommand(String tag, byte value, byte... params) {
        this.tag = tag;
        this.value = value;
        this.params = params;
    }

    public byte toByte(boolean value) {
        return (byte) (value ? 1 : 0);
    }

    public boolean toBoolean(byte value) {
        return value != 0;
    }

    public String toString(byte[] value) {
        return (new String(value, StandardCharsets.US_ASCII)).trim();
    }

    /**
     * Returns null if the date cannot be parsed.
     *
     * @param value
     * @return parsed date object
     */
    public Date toDate(byte[] value) {
        String dateString = toString(value);
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(tag, "Value cannot be parsed to Date.", e);
            return null;
        }
    }

    /**
     * Returns null if the time cannot be parsed.
     *
     * @param value
     * @return parsed time object
     */
    public Time toTime(byte[] value) {
        String timeString = toString(value);
        DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        try {
            return new Time(dateFormat.parse(timeString).getTime());
        } catch (ParseException e) {
            Log.e(tag, "Value cannot be parsed to Date.", e);
            return null;
        }
    }

    public BigDecimal toBigDecimal(byte[] value) {
        String valueString = toString(value);
        valueString = valueString.replaceAll("[^0-9]+", "");
        return new BigDecimal(valueString);
    }
}
