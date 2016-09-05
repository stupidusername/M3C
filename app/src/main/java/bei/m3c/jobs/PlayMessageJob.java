package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    private TPCStartAudioMessageCommand command;

    public PlayMessageJob(TPCStartAudioMessageCommand command) {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(command.getTidName()).addTags(command.getTidName()));
        this.command = command;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        AudioMessage audioMessage = M3SHelper.getAudioMessage(command);
        MessagePlayer.getInstance().addAudioMessage(audioMessage);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Subscribe
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
