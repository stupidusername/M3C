package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import bei.m3c.helpers.JobManagerHelper;

public class GetRadiosJob extends Job {

    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in milis
    public static final int INTERVAL = 5000; // interval in milis

    public GetRadiosJob() {
        this(DELAY);
    }

    public GetRadiosJob(int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(GetRadiosJob.class.getSimpleName()));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
       JobManagerHelper.getJobManager().addJob(new GetRadiosJob(INTERVAL));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
