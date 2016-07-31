package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import bei.m3c.events.GetChannelsEvent;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.Channel;
import bei.m3c.models.ChannelCategory;

public class GetChannelsJob extends Job {

    public static final String TAG = "GetChannelsJob";
    public static final int PRIORITY = 1;

    private ChannelCategory channelCategory;

    public GetChannelsJob(ChannelCategory channelCategory) {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(TAG).addTags(TAG));
        this.channelCategory = channelCategory;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        List<Channel> channels = M3SHelper.getChannels(channelCategory.id);
        EventBus.getDefault().post(new GetChannelsEvent(channels));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Subscribe
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
