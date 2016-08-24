package bei.m3c.helpers;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;

import bei.m3c.activities.MainActivity;

public final class JobManagerHelper {

    public static JobManager getJobManager() {
        return MainActivity.getInstance().getJobManager();
    }

    public static void cancelJobsInBackground(String... tags) {
        getJobManager().cancelJobsInBackground(null, TagConstraint.ANY, tags);
    }
}
