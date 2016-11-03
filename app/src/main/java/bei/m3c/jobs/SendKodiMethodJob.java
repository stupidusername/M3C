package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import bei.m3c.kodiMethods.BaseKodiMethod;
import bei.m3c.connections.KodiConnection;
import bei.m3c.helpers.JobManagerHelper;

public class SendKodiMethodJob extends Job {

    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int DEFAULT_INTERVAL = 0;
    public static final boolean DEFAULT_RETRY = false;

    public KodiConnection connection;
    public BaseKodiMethod method;
    public int interval = 0;
    public boolean retry;

    public SendKodiMethodJob(KodiConnection connection, BaseKodiMethod method) {
        this(connection, method, DELAY);
    }

    public SendKodiMethodJob(KodiConnection connection, BaseKodiMethod method, int interval) {
        this(connection, method, interval, DELAY);
    }

    public SendKodiMethodJob(KodiConnection connection, BaseKodiMethod method, int interval, int delay) {
        this(connection, method, interval, delay, DEFAULT_RETRY);
    }

    /**
     * @param connection
     * @param method
     * @param interval
     * @param delay
     * @param retry      If true the command will retry to be sent if an error ocurred.
     *                   When sent operation succeeds the job will not be posted again.
     */
    public SendKodiMethodJob(KodiConnection connection, BaseKodiMethod method, int interval, int delay, boolean retry) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(method.method).addTags(method.method));
        this.connection = connection;
        this.method = method;
        this.interval = interval;
        this.retry = retry;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        boolean success = false;
        if (connection.isConnected) {
            success = connection.sendMethod(method);
        }
        if ((interval != DEFAULT_INTERVAL && !retry) || (!success && retry)) {
            JobManagerHelper.getJobManager().addJobInBackground(new SendKodiMethodJob(connection, method, interval, interval, retry));
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
