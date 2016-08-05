package bei.m3c.helpers;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;

import bei.m3c.Application;

public final class JobManagerHelper {

    public static JobManager getJobManager() {
        return Application.getInstance().getJobManager();
    }

    public static void cancelJobs(String... tags) {
        getJobManager().cancelJobs(TagConstraint.ANY, tags);
    }

    public static void cancelJobsInBackground(String... tags) {
        getJobManager().cancelJobsInBackground(null, TagConstraint.ANY, tags);
    }
}
