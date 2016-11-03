package bei.m3c.helpers;

import android.util.Log;

import bei.m3c.activities.MainActivity;
import bei.m3c.jobs.SendKodiMethodJob;
import bei.m3c.kodiMethods.BaseKodiMethod;

public final class KodiConnectionHelper {

    public static final String TAG = "KodiConnectionHelper";

    public static void sendMethod(BaseKodiMethod method) {
        sendMethod(method, SendKodiMethodJob.DEFAULT_INTERVAL);
    }

    public static void sendMethod(BaseKodiMethod method, int interval) {
        sendMethod(method, interval, SendKodiMethodJob.DEFAULT_RETRY);
    }

    public static void sendMethod(BaseKodiMethod method, int interval, boolean retry) {
        try {
            MainActivity.getInstance().getKodiConnection().addMethodJob(method, interval, retry);
        } catch (Exception e) {
            Log.e(TAG, "Error adding " + method.method + ".");
        }
    }
}