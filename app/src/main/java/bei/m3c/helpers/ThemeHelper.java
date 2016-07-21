package bei.m3c.helpers;

import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.design.widget.TabLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import bei.m3c.R;

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

    /**
     * Sets the color scheme for the given ProgressBar
     */
    public static void setProgressBarTheme(ProgressBar progressBar) {
        progressBar.setProgressTintList(ColorStateList.valueOf(getAccentColor()));
    }

    /**
     * Sets the color scheme for the given SeekBar
     */
    public static void setSeekBarTheme(SeekBar seekBar) {
        setProgressBarTheme(seekBar);
        seekBar.setThumbTintList(ColorStateList.valueOf(getAccentColor()));
    }

    /**
     * Sets the color scheme for the given TabLayout
     */
    public static void setTabLayoutTheme(TabLayout tabLayout) {
        tabLayout.setSelectedTabIndicatorColor(getAccentColor());
        LayerDrawable layerDrawable = (LayerDrawable) tabLayout.getContext().getDrawable(R.drawable.tablayout_background);
        Drawable tabLayoutUnderline = layerDrawable.getDrawable(0);
        tabLayoutUnderline.setColorFilter(getDarkAccentColor(), PorterDuff.Mode.SRC);
        tabLayout.setBackground(layerDrawable);
    }
}
