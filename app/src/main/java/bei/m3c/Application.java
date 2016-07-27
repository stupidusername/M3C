package bei.m3c;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import bei.m3c.helpers.PreferencesHelper;

public class Application extends android.app.Application {

    public static final int CONSUMER_MIN_COUNT = 1; //always keep at least one consumer alive
    public static final int CONSUMER_MAX_COUNT = 3; //up to 3 consumers at a time
    public static final int CONSUMER_LOAD_FACTOR = 5; //5 jobs per consumer
    public static final int CONSUMER_KEEP_ALIVE = 120; //wait 2 minute

    private static Application instance;
    private JobManager jobManager;

    public Application() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesHelper.initialize(this);
        getJobManager();
    }

    public synchronized JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .minConsumerCount(CONSUMER_MIN_COUNT)
                .maxConsumerCount(CONSUMER_MAX_COUNT)
                .loadFactor(CONSUMER_LOAD_FACTOR)
                .consumerKeepAlive(CONSUMER_KEEP_ALIVE);
        jobManager = new JobManager(builder.build());
    }

    public static Application getInstance() {
        return instance;
    }
}
