package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.Subscribe;

import bei.m3c.commands.TPCStartAudioMessageCommand;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.AudioMessage;
import bei.m3c.players.MessagePlayer;

public class PlayMessageJob extends Job {

    public static final int PRIORITY = 1;
    public static final String TAG = "PlayMessageJob";

    private TPCStartAudioMessageCommand command;

    public PlayMessageJob(TPCStartAudioMessageCommand command) {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(Integer.toString(command.audioMessageKey)).addTags(Integer.toString(command.audioMessageKey)));
        this.command = command;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        AudioMessage audioMessage = M3SHelper.getAudioMessage(command);
        if (audioMessage != null) {
            MessagePlayer.getInstance().addAudioMessage(audioMessage);
        } else {
            Log.w(TAG, "Audio message not found.");
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
