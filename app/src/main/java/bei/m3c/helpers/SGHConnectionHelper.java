package bei.m3c.helpers;

import android.util.Log;

import bei.m3c.activities.MainActivity;
import bei.m3c.commands.BaseCommand;
import bei.m3c.jobs.SendCommandJob;

public final class SGHConnectionHelper {

    public static final String TAG = "SGHConnectionHelper";

    public static void sendCommand(BaseCommand command) {
        sendCommand(command, SendCommandJob.DEFAULT_INTERVAL);
    }

    public static void sendCommand(BaseCommand command, int interval) {
        sendCommand(command, interval, SendCommandJob.DEFAULT_RETRY);
    }

    public static void sendCommand(BaseCommand command, int interval, boolean retry) {
        try {
            MainActivity.getInstance().getSGHConnection().addCommandJob(command, interval, retry);
        } catch (Exception e) {
            Log.e(TAG, "Error adding " + command.tag + ".");
        }
    }
}
