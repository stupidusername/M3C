package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import bei.m3c.events.GetServiceTariffsEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.ServiceTariff;

public class GetServiceTariffsJob extends Job {

    public static final String TAG = "GetServiceTariffsJob";
    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int INTERVAL = 60000; // interval in millis

    public GetServiceTariffsJob() {
        this(DELAY);
    }

    public GetServiceTariffsJob(int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(TAG).addTags(TAG));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        List<ServiceTariff> serviceTariffs = M3SHelper.getServiceTariffs();
        EventBus.getDefault().post(new GetServiceTariffsEvent(serviceTariffs));
        JobManagerHelper.getJobManager().addJobInBackground(new GetServiceTariffsJob(INTERVAL));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
