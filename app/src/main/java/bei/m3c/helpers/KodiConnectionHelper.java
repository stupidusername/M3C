package bei.m3c.helpers;

import android.util.Log;

import bei.m3c.activities.MainActivity;
import bei.m3c.jobs.SendKodiMethodJob;
import bei.m3c.kodiMethods.BaseMethod;

public final class KodiConnectionHelper {

    public static final String TAG = "KodiConnectionHelper";

    public static void sendMethod(BaseMethod method) {
        sendMethod(method, SendKodiMethodJob.DEFAULT_INTERVAL);
    }

    public static void sendMethod(BaseMethod method, int interval) {
        sendMethod(method, interval, SendKodiMethodJob.DEFAULT_RETRY);
    }

    public static void sendMethod(BaseMethod method, int interval, boolean retry) {
        try {
            MainActivity.getInstance().getKodiConnection().addMethodJob(method, interval, retry);
        } catch (Exception e) {
            Log.e(TAG, "Error adding " + method.method + ".");
        }
    }
}