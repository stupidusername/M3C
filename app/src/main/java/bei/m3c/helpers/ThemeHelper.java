package bei.m3c.helpers;

import android.animation.ArgbEvaluator;
import android.graphics.Color;

/**
 * Contains the methods to obtain color values based on the chosen theme color
 */
public final class ThemeHelper {

    public static final int COLOR_ACCENT = 0xFF00FDFD;

    /**
     * the fraction from the accent color to black
     */
    public static final float DARK_ACCENT_FACTOR = 0.625f;

    /**
     * @return main accent color
     */
    public static int getAccentColor() {
        return COLOR_ACCENT;
    }

    /**
     * Calculates and returns a color that sits between the accent color and black
     *
     * @return dark accent color
     */
    public static int getDarkAccentColor() {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int darkAccentColor = (int) evaluator.evaluate(DARK_ACCENT_FACTOR, COLOR_ACCENT, Color.BLACK);
        return darkAccentColor;
    }

}
