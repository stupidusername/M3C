package bei.m3c.jobs;

import android.content.ComponentName;
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
import bei.m3c.commands.TPCTabStatusCommand;
import bei.m3c.helpers.DownloadHelper;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.helpers.MD5Helper;
import bei.m3c.helpers.PowerHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.RootHelper;
import bei.m3c.helpers.SGHConnectionHelper;
import bei.m3c.models.AppVersion;

public class UpdateRebootJob extends Job {

    public static final String TAG = "UpdateRebootJob";
    public static final int PRIORITY = 1;
    public static final String PACKAGE_NAME = "M3C.apk";
    public static final String REBOOT_COMMAND = "reboot";
    public static final String PLACEHOLDER_PATH = "{path_placeholder}";
    public static final String PLACEHOLDER_PACKAGE = "{package_placeholder}";
    public static final String PLACEHOLDER_CLASS = "{class_placeholder}";
    public static final String PACKAGE_INSTALL_PATH = "/sdcard/" + PACKAGE_NAME;
    public static final String UPDATE_COMMAND = "mv " + PLACEHOLDER_PATH + " " + PACKAGE_INSTALL_PATH + "; pm install -r " + PACKAGE_INSTALL_PATH;
    public static final String UPDATE_REBOOT_COMMAND = "(" + UPDATE_COMMAND + "; " + REBOOT_COMMAND + ") &";
    // Sleep 3 secs before launching app (android sucks)
    public static final String LAUNCH_APP_COMMAND = "sleep 3; am start -n \"" + PLACEHOLDER_PACKAGE + "/" + PLACEHOLDER_CLASS + "\" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER";
    public static final String UPDATE_RELAUNCH_COMMAND = "(" + UPDATE_COMMAND + "; " + LAUNCH_APP_COMMAND + ") &";

    public UpdateRebootJob() {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(TAG).addTags(TAG));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        if (RootHelper.canRunRootCommands()) {
            AppVersion appVersion = M3SHelper.getUpdate();
            boolean update = false;
            if (appVersion != null) {
                if (appVersion.version > PreferencesHelper.getAppVersion()) {
                    update = true;
                } else if (appVersion.version != PreferencesHelper.getAppVersion() && appVersion.forceUpdate) {
                    update = true;
                }
                String path = MainActivity.getInstance().getFilesDir() + "/" + PACKAGE_NAME;
                if (update) {
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
                    command = UPDATE_RELAUNCH_COMMAND;
                } else if (rebootAllowed) {
                    command = REBOOT_COMMAND;
                }

                if (rebootAllowed) {
                    SGHConnectionHelper.sendCommand(new TPCTabStatusCommand(PowerHelper.isConnected(), true));
                }

                if (command != null) {
                    ComponentName componentName = MainActivity.getInstance().getComponentName();
                    command = command.replace(PLACEHOLDER_PATH, path)
                            .replace(PLACEHOLDER_PACKAGE, componentName.getPackageName())
                            .replace(PLACEHOLDER_CLASS, componentName.getShortClassName());
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
