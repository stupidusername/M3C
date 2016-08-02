package bei.m3c.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bei.m3c.R;
import bei.m3c.models.Light;
import bei.m3c.preferences.LightPreference;

/**
 * This class contains static methods to get the application preferences
 */
public final class PreferencesHelper {

    public static final String TAG = "PreferencesHelper";

    // Preferences dialog password
    public static final String PASSWORD = "beisrl";

    // Preferences keys
    public static final String KEY_M3S_ADDRESS = "m3s_address";
    public static final String KEY_SHOW_AC_CONTROLS = "show_ac_controls";
    public static final String KEY_THEME_COLOR = "theme_color";

    // Default values
    public static final boolean DEFAULT_SHOW_AC_CONTROLS = true;
    public static final String DEFAULT_ADDRESS = null;

    private static Context context = null;
    private static SharedPreferences sharedPreferences = null;

    public static void initialize(Context newContext) {
        context = newContext;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getAppVersion() {
        int flags = 0;
        PackageInfo packageInfo = null;
        try {
            packageInfo = getContext().getPackageManager().getPackageInfo(context.getPackageName(), flags);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package not found.", e);
        }
        int version = 0;
        if (packageInfo != null) {
            version = packageInfo.versionCode;
        }
        return version;
    }

    private static Context getContext() {
        if (context == null) {
            throw new RuntimeException("Context is null. Call initialize() before using this class.");
        }
        return context;
    }

    public static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            throw new RuntimeException("SharedPreferences is null. Call initialize() before using this class.");
        }
        return sharedPreferences;
    }

    public static String getM3SAddress() {
        return sharedPreferences.getString(KEY_M3S_ADDRESS, DEFAULT_ADDRESS);
    }

    public static boolean showACControls() {
        return sharedPreferences.getBoolean(KEY_SHOW_AC_CONTROLS, DEFAULT_SHOW_AC_CONTROLS);
    }

    public static int getThemeColor() {
        return sharedPreferences.getInt(KEY_THEME_COLOR, ContextCompat.getColor(context, R.color.default_accent_color));
    }

    public static List<Light> getLights() {
        List<Light> lights = new ArrayList<Light>();
        for (int i = 0; i < Light.MAX_LIGHTS; i++) {
            String name = getSharedPreferences().getString(LightPreference.getNameKey(i), null);
            if(name != null) {
                int type = getSharedPreferences().getInt(LightPreference.getTypeKey(i), LightPreference.getDefaultType());
                lights.add(new Light(name, type));
            }
        }
        return lights;
    }
}
