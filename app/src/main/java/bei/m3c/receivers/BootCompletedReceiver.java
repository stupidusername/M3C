package bei.m3c.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bei.m3c.activities.MainActivity;
import bei.m3c.helpers.PreferencesHelper;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PreferencesHelper.startOnBoot()) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }

}
