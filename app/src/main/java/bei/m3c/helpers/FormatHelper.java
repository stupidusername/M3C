package bei.m3c.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import bei.m3c.Application;
import bei.m3c.R;
import bei.m3c.activities.MainActivity;

/**
 * This class contains static methods that formats data to be displayed in the UI
 */
public final class FormatHelper {

    public static final String SYMBOL_CURRENCY = "$";
    public static final char SEPARATOR_DECIMAL = ',';
    public static final char SEPARATOR_GROUPING = ' ';

    public static String asCurrency(BigDecimal number) {
        String formatted = Application.getInstance().getBaseContext().getString(R.string.no_value);
        if (number != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(SEPARATOR_DECIMAL);
            symbols.setGroupingSeparator(SEPARATOR_GROUPING);
            DecimalFormat currency = new DecimalFormat("#,##0.00", symbols);
            formatted = SYMBOL_CURRENCY + currency.format(number);
        }
        return formatted;
    }

    public static String asTimerWithHours(int timeInMillis) {
        int hours = timeInMillis / (60 * 60 * 1000);
        int remainingMillis = timeInMillis - hours * (60 * 60 * 1000);
        int minutes = remainingMillis / (60 * 1000);
        remainingMillis = remainingMillis - minutes * (60 * 1000);
        long seconds = Math.round(((double) remainingMillis) / 1000);
        return String.format("%01d:%02d:%02d", hours, minutes, seconds);
    }

    public static String asTimer(int timeInMillis) {
        int minutes = timeInMillis / (60 * 1000);
        int remainingMillis = timeInMillis - minutes * (60 * 1000);
        long seconds = Math.round(((double) remainingMillis) / 1000);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String asTime(Time time) {
        if (time != null) {
            String timeFormat = MainActivity.getInstance().getString(R.string.format_time);
            return (new SimpleDateFormat(timeFormat)).format(time);
        } else {
            return MainActivity.getInstance().getString(R.string.time_default);
        }
    }

    public static String asLongDate(Date date) {
        if (date != null) {
            String longDateFormat = MainActivity.getInstance().getString(R.string.format_date_long);
            Locale locale = Application.getInstance().getBaseContext().getResources().getConfiguration().locale;
            return (new SimpleDateFormat(longDateFormat, locale)).format(date);
        } else {
            return MainActivity.getInstance().getString(R.string.no_value);
        }
    }

    public static String asAddress(String address, int port) {
        return address + ":" + port;
    }

    public static String asHexString(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        // Make room for 2 chars and " "
        int displayLength = 3;
        char[] hexChars = new char[bytes.length * displayLength - 1];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            int firstCharPosition = j * displayLength;
            int secondCharPosition = firstCharPosition + 1;
            int spaceCharPosition = secondCharPosition + 1;
            hexChars[firstCharPosition] = hexArray[v >>> 4];
            hexChars[secondCharPosition] = hexArray[v & 0x0F];
            // Don't add a space for last byte
            if (spaceCharPosition < hexChars.length) {
                hexChars[spaceCharPosition] = ' ';
            }
        }
        return new String(hexChars);
    }

    public static String asString(ArrayList<Character> chars) {
        StringBuilder builder = new StringBuilder(chars.size());
        for (Character ch : chars) {
            builder.append(ch);
        }
        return builder.toString();
    }

    public static byte[] asNumberByteArray(int number) {
        String temp = Integer.toString(number);
        byte[] byteArrayNumber = new byte[temp.length()];
        for (int i = 0; i < temp.length(); i++) {
            byteArrayNumber[i] = (byte) ((byte) temp.charAt(i) - '0');
        }
        return byteArrayNumber;
    }
}
