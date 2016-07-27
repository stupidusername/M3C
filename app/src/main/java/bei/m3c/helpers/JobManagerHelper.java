package bei.m3c.helpers;

import com.birbit.android.jobqueue.JobManager;

import bei.m3c.Application;

public final class JobManagerHelper {

    public static JobManager getJobManager() {
        return Application.getInstance().getJobManager();
    }
}
