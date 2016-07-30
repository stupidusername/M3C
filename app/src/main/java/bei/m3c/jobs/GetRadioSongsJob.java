package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import bei.m3c.events.GetRadioSongsEvent;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.Radio;
import bei.m3c.models.Song;

public class GetRadioSongsJob extends Job {

    public static final String TAG = "GetRadioSongsJob";
    public static final int PRIORITY = 1;

    private Radio radio;

    public GetRadioSongsJob(Radio radio) {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(TAG).addTags(TAG));
        this.radio = radio;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        List<Song> songs = M3SHelper.getRadioSongs(radio.id);
        EventBus.getDefault().post(new GetRadioSongsEvent(songs));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Subscribe
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
