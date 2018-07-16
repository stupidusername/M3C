package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import bei.m3c.connections.KodiConnection;
import bei.m3c.helpers.JobManagerHelper;

public class ConnectKodiJob extends Job {

    public static final String TAG = "ConnectKodiJob";
    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int INTERVAL = 5000; // interval in millis

    public KodiConnection connection;

    public ConnectKodiJob(KodiConnection connection) {
        this(connection, DELAY);
    }

    public ConnectKodiJob(KodiConnection connection, int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(TAG).addTags(TAG));
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
            JobManagerHelper.getJobManager().addJobInBackground(new ConnectKodiJob(connection, INTERVAL));
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
