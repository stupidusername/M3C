package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import bei.m3c.connections.BaseConnection;
import bei.m3c.helpers.JobManagerHelper;

public class ConnectJob extends Job {

    public BaseConnection connection;
    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int INTERVAL = 5000; // interval in millis

    public ConnectJob(BaseConnection connection) {
        this(connection, DELAY);
    }

    public ConnectJob(BaseConnection connection, int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(connection.tag).addTags(connection.tag));
        this.connection = connection;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        try {
            connection.connect();
        } catch (Exception e) {
            JobManagerHelper.getJobManager().addJobInBackground(new ConnectJob(connection, INTERVAL));
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
