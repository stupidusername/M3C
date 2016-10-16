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
import java.util.regex.Pattern;

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
    public static final String KEY_ROOM_NUMBER = "room_number";
    public static final String KEY_SGH_ADDRESS = "sgh_address";
    public static final String KEY_SGH_PORT = "sgh_port";
    public static final String KEY_PIC_ADDRESS = "pic_address";
    public static final String KEY_M3S_ADDRESS = "m3s_address";
    public static final String KEY_START_ON_BOOT = "start_on_boot";
    public static final String KEY_REBOOT_ALLOWED = "reboot_allowed";
    public static final String KEY_SHOW_RECORD_SCENE_CONTROLS = "show_record_scene_controls";
    public static final String KEY_SHOW_AC_CONTROLS = "show_ac_controls";
    public static final String KEY_MESSAGE_VOLUME_PERCENTAGE = "message_volume_percentage";
    public static final String KEY_INTRO_VOLUME_PERCENTAGE = "intro_volume_percentage";
    public static final String KEY_TV_REMOTE_CODE = "tv_remote_code";
    public static final String KEY_SHOW_VIDEO_CONTROLS = "show_video_controls";
    public static final String KEY_KODI_ADDRESS = "kodi_address";
    public static final String KEY_KODI_PORT = "kodi_port";
    public static final String KEY_THEME_COLOR = "theme_color";

    // Default values
    public static final String DEFAULT_ROOM_NUMBER = null;
    public static final String DEFAULT_ADDRESS = "";
    public static final boolean DEFAULT_START_ON_BOOT = false;
    public static final boolean DEFAULT_REBOOT_ALLOWED = false;
    public static final boolean DEFAULT_SHOW_RECORD_SCENE_CONTROLS = false;
    public static final boolean DEFAULT_SHOW_AC_CONTROLS = true;
    public static final String DEFAULT_VOLUME_PERCENTAGE = "50";
    public static final int DEFAULT_TV_CODE = -1;
    public static final boolean DEFAULT_SHOW_VIDEO_CONTROLS = false;
    public static final int DEFAULT_KODI_PORT = 9090;

    public static final String PORT_UNSET = "";

    public static final String ADDRESS_SEPARATOR = "."; // ip address separator
    public static final int PIC_ADDRESS_BASE_ADDRESS = 200;
    public static final int PIC_PORT = 9761;
    public static final int SGH_BASE_PORT = 3000;

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

    public static int getRoomNumber() throws RuntimeException {
        String roomNumberString = getSharedPreferences().getString(KEY_ROOM_NUMBER, DEFAULT_ROOM_NUMBER).trim();
        if (roomNumberString == null) {
            throw new RuntimeException("Room number is not set.");
        }
        int roomNumber = Integer.parseInt(roomNumberString);
        return roomNumber;
    }

    public static String getPICAddress() {
        String address = getSharedPreferences().getString(KEY_PIC_ADDRESS, DEFAULT_ADDRESS).trim();
        if (address.equals(DEFAULT_ADDRESS)) {
            address = getDefaultPICAddress();
        }
        return address;
    }

    private static String getDefaultPICAddress() throws RuntimeException {
        try {
            String sghAddress = getSGHAddress();
            String[] bits = sghAddress.split(Pattern.quote(ADDRESS_SEPARATOR));
            String picAddress = bits[0] + ADDRESS_SEPARATOR + bits[1] + ADDRESS_SEPARATOR + bits[2] + ADDRESS_SEPARATOR + (PIC_ADDRESS_BASE_ADDRESS + getRoomNumber());
            return picAddress;
        } catch (Exception e) {
            throw new RuntimeException("Error getting PIC address.", e);
        }
    }

    public static int getPICPort() {
        return PIC_PORT;
    }

    public static String getSGHAddress() throws RuntimeException {
        String sghAdress = getSharedPreferences().getString(KEY_SGH_ADDRESS, DEFAULT_ADDRESS).trim();
        if (sghAdress.equals(DEFAULT_ADDRESS)) {
            throw new RuntimeException("SGH address is not set.");
        }
        return sghAdress;
    }

    public static int getSGHPort() {
        String portString = getSharedPreferences().getString(KEY_SGH_PORT, PORT_UNSET).trim();
        int port = portString.equals(PORT_UNSET) ? getDefaultSGHPort() : Integer.parseInt(portString);
        return port;
    }

    private static int getDefaultSGHPort() throws RuntimeException {
        try {
            return SGH_BASE_PORT + getRoomNumber();
        } catch (Exception e) {
            throw new RuntimeException("Error getting SGH port.", e);
        }
    }

    public static String getM3SAddress() {
        return getSharedPreferences().getString(KEY_M3S_ADDRESS, DEFAULT_ADDRESS).trim();
    }

    public static boolean startOnBoot() {
        return getSharedPreferences().getBoolean(KEY_START_ON_BOOT, DEFAULT_START_ON_BOOT);
    }

    public static boolean rebootAllowed() {
        return getSharedPreferences().getBoolean(KEY_REBOOT_ALLOWED, DEFAULT_REBOOT_ALLOWED);
    }

    public static boolean showRecordSceneControls() {
        return getSharedPreferences().getBoolean(KEY_SHOW_RECORD_SCENE_CONTROLS, DEFAULT_SHOW_RECORD_SCENE_CONTROLS);
    }

    public static boolean showACControls() {
        return getSharedPreferences().getBoolean(KEY_SHOW_AC_CONTROLS, DEFAULT_SHOW_AC_CONTROLS);
    }

    public static int getMessageVolumePercentage() {
        String string = getSharedPreferences().getString(KEY_MESSAGE_VOLUME_PERCENTAGE, DEFAULT_VOLUME_PERCENTAGE).trim();
        return Integer.parseInt(string);
    }

    public static int getIntroVolumePercentage() {
        String string = getSharedPreferences().getString(KEY_INTRO_VOLUME_PERCENTAGE, DEFAULT_VOLUME_PERCENTAGE).trim();
        return Integer.parseInt(string);
    }

    public static int getTVRemoteCode() {
        String string = getSharedPreferences().getString(KEY_TV_REMOTE_CODE, Integer.toString(DEFAULT_TV_CODE)).trim();
        return Integer.parseInt(string);
    }

    public static boolean showVideoControls() {
        return getSharedPreferences().getBoolean(KEY_SHOW_VIDEO_CONTROLS, DEFAULT_SHOW_VIDEO_CONTROLS);
    }

    public static String getKodiAddress() throws RuntimeException {
        String kodiAdress = getSharedPreferences().getString(KEY_KODI_ADDRESS, DEFAULT_ADDRESS).trim();
        if (kodiAdress.equals(DEFAULT_ADDRESS)) {
            throw new RuntimeException("Kodi address is not set.");
        }
        return kodiAdress;
    }

    public static int getKodiPort() {
        String string = getSharedPreferences().getString(KEY_KODI_PORT, Integer.toString(DEFAULT_KODI_PORT)).trim();
        return Integer.parseInt(string);
    }

    public static int getThemeColor() {
        return getSharedPreferences().getInt(KEY_THEME_COLOR, ContextCompat.getColor(context, R.color.default_accent_color));
    }

    public static List<Light> getLights() {
        List<Light> lights = new ArrayList<Light>();
        for (int i = 0; i < Light.MAX_LIGHTS; i++) {
            String name = getSharedPreferences().getString(LightPreference.getNameKey(i), null).trim();
            if (name != null) {
                int type = getSharedPreferences().getInt(LightPreference.getTypeKey(i), LightPreference.getDefaultType());
                lights.add(new Light(name, type));
            }
        }
        return lights;
    }
}
