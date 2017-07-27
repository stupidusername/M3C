package bei.m3c.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bei.m3c.activities.MainActivity;

public class MonitorService extends Service {

    public static final int CHECK_INTERVAL_MILLIS = 1000;
    public static final int SLEEP_UNTIL_RELAUNCH_MILLIS = 5000;

    public static Timer timer = null;
    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isRunning()) {
                        try {
                            Thread.sleep(SLEEP_UNTIL_RELAUNCH_MILLIS);
                        } catch (Exception e) {
                            // Go ahead and relaunch the app
                        }
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }, CHECK_INTERVAL_MILLIS, CHECK_INTERVAL_MILLIS);
        }

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private boolean isRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (context.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
