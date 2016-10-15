package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import bei.m3c.events.GetVideosEvent;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.Video;
import bei.m3c.models.VideoCategory;

public class GetVideosJob extends Job {

    public static final String TAG = "GetVideosJob";
    public static final int PRIORITY = 1;

    private VideoCategory videoCategory;

    public GetVideosJob(VideoCategory videoCategory) {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(TAG).addTags(TAG));
        this.videoCategory = videoCategory;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        List<Video> videos = M3SHelper.getVideos(videoCategory.id);
        EventBus.getDefault().post(new GetVideosEvent(videos));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Subscribe
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
