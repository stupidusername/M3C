package bei.m3c.helpers;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import bei.m3c.R;
import bei.m3c.receivers.DeviceAdminReceiver;

/**
 * Contains methods to enable/disable kiosk mode
 */
public final class KioskModeHelper {

    public static final String TAG = "KioskModeHelper";
    private static AppCompatActivity activity;

    public static void initialize(AppCompatActivity newActivity) {
        activity = newActivity;
    }

    public static void enterKioskMode() {
        ComponentName deviceAdmin = new ComponentName(activity, DeviceAdminReceiver.class);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        String packageName = activity.getPackageName();
        if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
            devicePolicyManager.setLockTaskPackages(deviceAdmin, new String[]{packageName});
            activity.startLockTask();
        } else {
            Toast.makeText(activity, activity.getString(R.string.device_owner_not_enabled),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void exitKioskMode() {
        try {
            activity.stopLockTask();
        } catch (Exception e) {
            Log.e(TAG, "Could not stop lock task. Maybe the task wasn't locked.", e);
        }
    }
}
