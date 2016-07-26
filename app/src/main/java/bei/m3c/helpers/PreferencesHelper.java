package bei.m3c.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * This class contains static methods to get the application preferences
 */
public final class PreferencesHelper {

    public static final String PASSWORD = "beisrl";
    public static final String TAG = PreferencesHelper.class.getSimpleName();

    public static int getAppVersion(Context context) {
        int flags = 0;
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), flags);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package not found.");
        }
        int version = 0;
        if (packageInfo != null) {
            version = packageInfo.versionCode;
        }
        return version;
    }
}
