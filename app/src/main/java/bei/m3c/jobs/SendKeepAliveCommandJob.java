package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import bei.m3c.connections.BaseConnection;
import bei.m3c.helpers.JobManagerHelper;

public class SendKeepAliveCommandJob extends Job {

    public static final int PRIORITY = 1;
    public static final int DELAY_MILLIS = 0;
    public static final int INTERVAL_MILLIS = 10000;

    public BaseConnection connection;

    public SendKeepAliveCommandJob(BaseConnection connection) {
        this(connection, DELAY_MILLIS);
    }

    public SendKeepAliveCommandJob(BaseConnection connection, int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay)
                .singleInstanceBy(connection.getKeepAliveCommand().tag).addTags(connection.getKeepAliveCommand().tag));
        this.connection = connection;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        if (connection.isConnected) {
            connection.sendCommand(connection.getKeepAliveCommand());
        }
        JobManagerHelper.getJobManager().addJobInBackground(new SendKeepAliveCommandJob(connection, INTERVAL_MILLIS));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
