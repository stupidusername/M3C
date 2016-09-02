package bei.m3c.jobs;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import bei.m3c.activities.MainActivity;
import bei.m3c.helpers.DownloadHelper;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.helpers.MD5Helper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.RootHelper;
import bei.m3c.models.AppVersion;

public class UpdateRebootJob extends Job {

    public static final String TAG = "UpdateRebootJob";
    public static final int PRIORITY = 1;
    public static final String PACKAGE_NAME = "M3C.apk";
    public static final String REBOOT_COMMAND = "reboot";
    public static final String UPDATE_COMMAND = "pm install -r /sdcard/" + PACKAGE_NAME;
    public static final String UPDATE_REBOOT_COMMAND = "(" + UPDATE_COMMAND + "; " + REBOOT_COMMAND + ") &";

    public UpdateRebootJob() {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(TAG).addTags(TAG));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        if (RootHelper.canRunRootCommands(getApplicationContext())) {
            AppVersion appVersion = M3SHelper.getUpdate();
            boolean update = false;
            if (appVersion != null) {
                if (appVersion.version > PreferencesHelper.getAppVersion()) {
                    update = true;
                } else if (appVersion.version != PreferencesHelper.getAppVersion() && appVersion.forceUpdate) {
                    update = true;
                }
                if (update) {
                    String path = Environment.getExternalStorageDirectory() + "/" + PACKAGE_NAME;
                    DownloadHelper.download(appVersion.apkUrl, path);
                    // Don't update if the MD5 check fails
                    if (!MD5Helper.checkMD5(appVersion.md5, new File(path))) {
                        update = false;
                        Log.w(TAG, "Update file MD5 check failed. Update aborted.");
                    }
                }

                String command = null;
                boolean rebootAllowed = PreferencesHelper.rebootAllowed();

                if (update && rebootAllowed) {
                    command = UPDATE_REBOOT_COMMAND;
                } else if (update) {
                    command = UPDATE_COMMAND;
                } else if (rebootAllowed) {
                    command = REBOOT_COMMAND;
                }

                if (command != null) {
                    RootHelper.execute(command);
                }
            }
        } else {
            Log.w(TAG, "Application does not have root access. Update not allowed.");
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Subscribe
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
