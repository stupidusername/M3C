package bei.m3c.helpers;

import android.util.Log;

import bei.m3c.Application;
import bei.m3c.commands.BaseCommand;
import bei.m3c.jobs.SendCommandJob;

public final class PICConnectionHelper {

    public static final String TAG = "PICConnectionHelper";

    public static void sendCommand(BaseCommand command) {
        sendCommand(command, SendCommandJob.DEFAULT_INTERVAL);
    }

    public static void sendCommand(BaseCommand command, int interval) {
        sendCommand(command, interval, SendCommandJob.DEFAULT_RETRY);
    }

    public static void sendCommand(BaseCommand command, int interval, boolean retry) {
        try {
            Application.getInstance().getPICConnection().addCommandJob(command, interval, retry);
        } catch (Exception e) {
            Log.e(TAG, "Error adding " + command.tag + ".");
        }
    }
}