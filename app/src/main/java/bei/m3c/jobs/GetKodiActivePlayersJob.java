package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import java.sql.Timestamp;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.KodiConnectionHelper;
import bei.m3c.kodiMethods.PlayerGetActivePlayersKodiMethod;

public class GetKodiActivePlayersJob extends Job {

    public static final String TAG = "GetKodiActivePlayersJob";
    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int INTERVAL = 250; // interval in millis

    public GetKodiActivePlayersJob() {
        this(DELAY);
    }

    public GetKodiActivePlayersJob(int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(TAG).addTags(TAG));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        KodiConnectionHelper.sendMethod(new PlayerGetActivePlayersKodiMethod(new Timestamp(System.currentTimeMillis()).toString()));
        JobManagerHelper.getJobManager().addJobInBackground(new GetKodiActivePlayersJob(INTERVAL));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
