package bei.m3c;

import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import java.io.IOException;

import bei.m3c.connections.PICConnection;
import bei.m3c.connections.SGHConnection;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.players.MusicPlayer;
import bei.m3c.services.M3SService;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class Application extends android.app.Application {

    public static final String TAG = "Application";

    public static final int CONSUMER_MIN_COUNT = 1; //always keep at least one consumer alive
    public static final int CONSUMER_MAX_COUNT = 3; //up to 3 consumers at a time
    public static final int CONSUMER_LOAD_FACTOR = 5; //5 jobs per consumer
    public static final int CONSUMER_KEEP_ALIVE = 120; //wait 2 minute

    private static Application instance;

    private JobManager jobManager = null;
    private Retrofit retrofit = null;
    private M3SService m3sService = null;
    private MusicPlayer musicPlayer = null;
    private PICConnection picConnection;
    private SGHConnection sghConnection;

    public Application() {
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesHelper.initialize(this);
        getJobManager();
        getM3SService();
        getPICConnection();
        getSGHConnection();
    }

    public synchronized JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }

    public synchronized M3SService getM3SService() {
        // Create service if it was not created or if the server address was changed
        try {
            String baseUrl = M3SHelper.getM3SUrl();
            if (retrofit == null || !baseUrl.equals(retrofit.baseUrl())) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                m3sService = retrofit.create(M3SService.class);
            }
        } catch (Exception e) {
            m3sService = null;
            Log.e(TAG, "Error creating M3S service.", e);
        }
        return m3sService;
    }

    public synchronized MusicPlayer getMusicPlayer() {
        if (musicPlayer == null) {
            musicPlayer = new MusicPlayer();
        }
        return musicPlayer;
    }

    public synchronized PICConnection getPICConnection() {
        if (picConnection == null) {
            try {
                String address = PreferencesHelper.getPICAddress();
                int port = PreferencesHelper.getPICPort();
                picConnection = new PICConnection(address, port);
            } catch (Exception e) {
                Log.e(TAG, "Error creating PIC connection.", e);
            }
        }
        return picConnection;
    }

    public synchronized SGHConnection getSGHConnection() {
        if (sghConnection == null) {
            try {
                String address = PreferencesHelper.getSGHAddress();
                int port = PreferencesHelper.getSGHPort();
                sghConnection = new SGHConnection(address, port);
            } catch (Exception e) {
                Log.e(TAG, "Error creating SGH connection.", e);
            }
        }
        return sghConnection;
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .minConsumerCount(CONSUMER_MIN_COUNT)
                .maxConsumerCount(CONSUMER_MAX_COUNT)
                .loadFactor(CONSUMER_LOAD_FACTOR)
                .consumerKeepAlive(CONSUMER_KEEP_ALIVE);
        jobManager = new JobManager(builder.build());
    }
}
