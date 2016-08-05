package bei.m3c.helpers;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This class contains static methods that formats data to be displayed in the UI
 */
public final class FormatHelper {

    public static final String SYMBOL_CURRENCY = "$";
    public static final char SEPARATOR_DECIMAL = ',';
    public static final char SEPARATOR_GROUPING = ' ';

    public static String asCurrency(BigDecimal number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(SEPARATOR_DECIMAL);
        symbols.setGroupingSeparator(SEPARATOR_GROUPING);
        DecimalFormat currency = new DecimalFormat("#,##0.00", symbols);
        return SYMBOL_CURRENCY + currency.format(number);
    }

    public static String asDateLong(Date date) {
        Locale spanish = new Locale("es", "ES");
        return (new SimpleDateFormat("d MMMM, y", spanish)).format(date);
    }

    public static String asTime(Date date) {
        return (new SimpleDateFormat("HH:mm")).format(date);
    }

    public static String asTimer(int timeInMillis) {
        int minutes = timeInMillis / (60 * 1000);
        int remainingMillis = timeInMillis - minutes * (60 * 1000);
        long seconds = Math.round(((double) remainingMillis) / 1000);
        return String.format("%02d:%02d", minutes, seconds);
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
}
