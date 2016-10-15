package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import bei.m3c.events.GetVideoCategoriesEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.VideoCategory;

public class GetVideoCategoriesJob extends Job {

    public static final String TAG = "GetVideoCategoriesJob";
    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int INTERVAL = 60000; // interval in millis

    public GetVideoCategoriesJob() {
        this(DELAY);
    }

    public GetVideoCategoriesJob(int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(TAG).addTags(TAG));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        List<VideoCategory> videoCategories = M3SHelper.getVideoCategories();
        EventBus.getDefault().post(new GetVideoCategoriesEvent(videoCategories));
        JobManagerHelper.getJobManager().addJobInBackground(new GetVideoCategoriesJob(INTERVAL));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
