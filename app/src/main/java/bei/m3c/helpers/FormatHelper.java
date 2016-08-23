package bei.m3c.helpers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import bei.m3c.Application;
import bei.m3c.R;

/**
 * This class contains static methods that formats data to be displayed in the UI
 */
public final class FormatHelper {

    public static final String SYMBOL_CURRENCY = "$";
    public static final char SEPARATOR_DECIMAL = ',';
    public static final char SEPARATOR_GROUPING = ' ';

    public static String asCurrency(BigDecimal number) {
        String formated = Application.getInstance().getBaseContext().getString(R.string.no_value);
        if (number != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(SEPARATOR_DECIMAL);
            symbols.setGroupingSeparator(SEPARATOR_GROUPING);
            DecimalFormat currency = new DecimalFormat("#,##0.00", symbols);
            formated = SYMBOL_CURRENCY + currency.format(number);
        }
        return formated;
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
