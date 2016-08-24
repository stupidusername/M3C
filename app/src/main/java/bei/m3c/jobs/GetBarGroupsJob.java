package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import bei.m3c.events.GetBarGroupsEvent;
import bei.m3c.events.GetRadiosEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.BarGroup;
import bei.m3c.models.Radio;

public class GetBarGroupsJob extends Job {

    public static final String TAG = "GetBarGroupsJob";
    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int INTERVAL = 60000; // interval in millis

    public GetBarGroupsJob() {
        this(DELAY);
    }

    public GetBarGroupsJob(int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(TAG).addTags(TAG));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        List<BarGroup> barGroups = M3SHelper.getBarGroups();
        EventBus.getDefault().post(new GetBarGroupsEvent(barGroups));
        JobManagerHelper.getJobManager().addJobInBackground(new GetBarGroupsJob(INTERVAL));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
