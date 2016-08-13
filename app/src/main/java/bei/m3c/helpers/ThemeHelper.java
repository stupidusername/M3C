package bei.m3c.helpers;

import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import bei.m3c.R;

/**
 * Contains the methods to obtain color values based on the chosen theme color
 */
public final class ThemeHelper {

    /**
     * the fraction from the accent color to black
     */
    public static final float DARK_ACCENT_FACTOR = 0.625f;

    // Used for view color state list background shape
    public static final int ROW_CORNER_RADIUS_DIP = 5;
    public static final int ROW_STROKE_WIDTH_DIP = 2;

    /**
     * @return main accent color
     */
    public static int getAccentColor() {
        return PreferencesHelper.getThemeColor();
    }

    /**
     * Calculates and returns a color that sits between the accent color and black
     *
     * @return dark accent color
     */
    public static int getDarkAccentColor() {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int darkAccentColor = (int) evaluator.evaluate(DARK_ACCENT_FACTOR, getAccentColor(), Color.BLACK);
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

    /**
     * Sets the color state list for the given Button according to the color scheme
     */
    public static void setButtonTheme(Button button) {
        int[][] stateList = {new int[]{android.R.attr.state_pressed}, new int[]{}};
        int[] colorList = {getAccentColor(), ContextCompat.getColor(button.getContext(), R.color.gray_light)};
        ColorStateList colorStateList = new ColorStateList(stateList, colorList);
        button.setTextColor(colorStateList);
    }

    /**
     * Sets the color state list for the given ImageButton according to the color scheme
     */
    public static void setImageButtonTheme(ImageButton imageButton) {
        StateListDrawable stateList = new StateListDrawable();
        Drawable drawableOriginal = imageButton.getDrawable();
        Drawable drawableHighlighted = imageButton.getDrawable().getConstantState().newDrawable().mutate();
        drawableHighlighted.setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN);
        stateList.addState(new int[]{android.R.attr.state_activated}, drawableHighlighted);
        stateList.addState(new int[]{android.R.attr.state_pressed}, drawableHighlighted);
        stateList.addState(new int[]{}, drawableOriginal);
        imageButton.setImageDrawable(stateList);
    }

    /**
     * Sets the color state list for the given View according to the color scheme
     */
    public static void setColorStateListTheme(View view) {
        Resources resources = view.getContext().getResources();
        float cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_CORNER_RADIUS_DIP, resources.getDisplayMetrics());
        int strokeWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_STROKE_WIDTH_DIP, resources.getDisplayMetrics()));
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(cornerRadius);
        shape.setStroke(strokeWidth, getDarkAccentColor());
        GradientDrawable activatedShape = new GradientDrawable();
        activatedShape.setCornerRadius(cornerRadius);
        activatedShape.setColor(getDarkAccentColor());
        activatedShape.setStroke(strokeWidth, getAccentColor());
        StateListDrawable stateList = new StateListDrawable();
        stateList.addState(new int[]{android.R.attr.state_pressed}, activatedShape);
        stateList.addState(new int[]{android.R.attr.state_activated}, activatedShape);
        stateList.addState(new int[]{}, shape);
        view.setBackground(stateList);
    }
}
