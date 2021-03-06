package bei.m3c.helpers;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import bei.m3c.Application;

public class PowerHelper {

    public static boolean isConnected() {
        Intent intent = Application.getInstance().registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }
}
