package bei.m3c.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import bei.m3c.R;
import bei.m3c.adapters.ViewPagerAdapter;
import bei.m3c.connections.KodiConnection;
import bei.m3c.connections.PICConnection;
import bei.m3c.connections.SGHConnection;
import bei.m3c.events.IntroEvent;
import bei.m3c.fragments.BarFragment;
import bei.m3c.fragments.InfoFragment;
import bei.m3c.fragments.LightsACFragment;
import bei.m3c.fragments.MusicFragment;
import bei.m3c.fragments.TVFragment;
import bei.m3c.fragments.VideoFragment;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.KioskModeHelper;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.interfaces.FragmentInterface;
import bei.m3c.jobs.ConnectKodiJob;
import bei.m3c.observers.SettingsContentObserver;
import bei.m3c.players.MessagePlayer;
import bei.m3c.players.MusicPlayer;
import bei.m3c.services.M3SService;
import bei.m3c.services.MonitorService;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.bumptech.glide.Glide;
import com.github.pwittchen.reactivewifi.ReactiveWifi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int CONSUMER_MIN_COUNT = 1; //always keep at least one consumer alive
    public static final int CONSUMER_MAX_COUNT = 50; //up to 50 consumers at a time
    public static final int CONSUMER_LOAD_FACTOR = 1; //1 jobs per consumer
    public static final int CONSUMER_KEEP_ALIVE = 120; //wait 2 minutes
    public static final int POSITION_MUSIC_TAB = 0;
    public static final int SHOW_INFO_TAB_DELAY= 30000; //time of inactivity until the info tab is shown
    public static final int MAX_DELAY_TO_PLAY_INTRO = 10000;

    public static MainActivity instance;
    private static JobManager jobManager = null;
    private static Retrofit retrofit = null;
    private static Gson gson = null;
    private static M3SService m3sService = null;
    private static MessagePlayer messagePlayer = null;
    private static MusicPlayer musicPlayer = null;
    private static PICConnection picConnection;
    private static SGHConnection sghConnection;
    private static KodiConnection kodiConnection;
    private static boolean wifiWasConnected = true;
    private static PowerManager.WakeLock wakeLock;
    private static SettingsContentObserver settingsContentObserver;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    // Views
    private LinearLayout touchDetectLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button preferencesButton;

    private AlertDialog preferencesAlertDialog;

    // Info tab timer
    private Timer infoTabTimer = null;

    // Integer to boolean adapter for Gson
    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt() != 0;
                case STRING:
                    return Boolean.parseBoolean(in.nextString());
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };

    public MainActivity() {
        instance = this;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Clear glide cache
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(getBaseContext()).clearDiskCache();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getBaseContext()).clearMemory();
                    }
                });
            }
        });

        startService(new Intent(getBaseContext(), MonitorService.class));
        getJobManager();
        getM3SService();
        getPICConnection();
        getSGHConnection();
        getKodiConnection();

        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        preferencesButton = (Button) findViewById(R.id.preferences_button);

        setupViewPager(viewPager);

        // Set up theme
        ThemeHelper.setTabLayoutTheme(tabLayout);

        // Add tabs
        tabLayout.setupWithViewPager(viewPager);
        startInfoTabTimer();

        // Set up preferences alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.version) + " " + PreferencesHelper.getAppVersion();
        builder.setTitle(title);
        builder.setView(R.layout.dialog_preferences);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Launch preferences activity if password is correct
                EditText passwordEditText = (EditText) ((AlertDialog) dialog).findViewById(R.id.preferences_dialog_password_edittext);
                if (passwordEditText.getText().toString().equals(PreferencesHelper.PASSWORD)) {
                    Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong_password),
                            Toast.LENGTH_SHORT).show();
                }
                passwordEditText.getText().clear();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        preferencesAlertDialog = builder.create();

        // Set up preferences button
        preferencesButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                preferencesAlertDialog.show();
                return false;
            }
        });

        // Enable kiosk mode
        KioskModeHelper.initialize(this);
        KioskModeHelper.enterKioskMode();

        // Wake up and keep screen on
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Register events and jobs
        EventBus.getDefault().register(this);

        // Monitor wifi state
        (new ReactiveWifi()).observeSupplicantState(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SupplicantState>() {
                    @Override
                    public void call(SupplicantState supplicantState) {
                        if (supplicantState == SupplicantState.DISCONNECTED && wifiWasConnected) {
                            Log.w(TAG, "Wi-Fi disconnected. Restarting adapter.");
                            wifiWasConnected = false;
                            WifiManager wifiManager = (WifiManager) getBaseContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            wifiManager.setWifiEnabled(false);
                            wifiManager.setWifiEnabled(true);
                        } else if (supplicantState == SupplicantState.COMPLETED) {
                            Log.w(TAG, "Wi-Fi connected.");
                            wifiWasConnected = true;
                        }
                    }
                });

        // Listen to preference changes
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(PreferencesHelper.KEY_SGH_ADDRESS) || key.equals(PreferencesHelper.KEY_SGH_ADDRESS)) {
                    if (sghConnection != null) {
                        sghConnection.disconnect(false);
                        sghConnection = null;
                        getSGHConnection();
                    }
                } else if (key.equals(PreferencesHelper.KEY_PIC_ADDRESS) || key.equals(PreferencesHelper.KEY_PIC_ADDRESS)) {
                    if (picConnection != null) {
                        picConnection.disconnect(false);
                        picConnection = null;
                        getPICConnection();
                    }
                }
                if (key.equals(PreferencesHelper.KEY_KODI_ADDRESS) || key.equals(PreferencesHelper.KEY_KODI_PORT)) {
                    if (kodiConnection != null) {
                        JobManagerHelper.cancelJobsInBackground(ConnectKodiJob.TAG);
                        kodiConnection.disconnect(false);
                        kodiConnection = null;
                        getKodiConnection();
                    }
                }
            }
        };
        PreferencesHelper.getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListener);
    }

    // Show the info tab when the device was not used for a while
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (infoTabTimer != null) {
                    infoTabTimer.cancel();
                    infoTabTimer = null;
                }
                break;
            case MotionEvent.ACTION_UP:
                startInfoTabTimer();
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    // Use immersive mode
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireWakeLock();
        if (settingsContentObserver == null) {
            settingsContentObserver = new SettingsContentObserver(this, new Handler());
        }
        getApplicationContext().getContentResolver()
                .registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, settingsContentObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
        getApplicationContext().getContentResolver().unregisterContentObserver(settingsContentObserver);
    }

    private void startInfoTabTimer() {
        if (infoTabTimer == null) {
            infoTabTimer = new Timer();
            infoTabTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getAdapter().getCount());
                        }
                    });
                }
            }, SHOW_INFO_TAB_DELAY);
        }
    }

    private void setupViewPager(final ViewPager viewPager) {
        // Set lights/ac tab title according to preferences
        String lightsACTitle = getString(R.string.lights_title);
        if (PreferencesHelper.showACControls()) {
            lightsACTitle += getString(R.string.tab_title_separator) + getString(R.string.ac_title);
        }

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MusicFragment(), getString(R.string.music_title));
        adapter.addFragment(new TVFragment(), getString(R.string.tv_title));
        if (PreferencesHelper.showVideoControls()) {
            adapter.addFragment(new VideoFragment(), getString(R.string.video_title));
        }
        adapter.addFragment(new LightsACFragment(), lightsACTitle);
        adapter.addFragment(new BarFragment(), getString(R.string.bar_title));
        adapter.addFragment(new InfoFragment(), getString(R.string.info_title));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int lastPosition = position != viewPager.getCurrentItem()  || position == adapter.getCount() - 1 ? position : position + 1;
                FragmentInterface fragment = (FragmentInterface) adapter.getItem(lastPosition);
                if (fragment != null) {
                    fragment.fragmentBecameInvisible();
                }
            }

            @Override
            public void onPageSelected(int position) {
                FragmentInterface fragment = (FragmentInterface) adapter.getItem(position);
                if (fragment != null) {
                    fragment.fragmentBecameVisible();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public synchronized JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }

    public synchronized Gson getGson() {
        if (gson == null) {
            gson = (new GsonBuilder()).create();
        }
        return gson;
    }

    public synchronized M3SService getM3SService() {
        // Create service if it was not created or if the server address was changed
        try {
            String baseUrl = M3SHelper.getM3SUrl();
            if (retrofit == null || !baseUrl.equals(retrofit.baseUrl())) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                                .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                                .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                                .create()))
                        .build();
                m3sService = retrofit.create(M3SService.class);
            }
        } catch (Exception e) {
            m3sService = null;
            Log.e(TAG, "Error creating M3S service.", e);
        }
        return m3sService;
    }

    public synchronized MessagePlayer getMessagePlayer() {
        if (messagePlayer == null) {
            messagePlayer = new MessagePlayer();
        }
        return messagePlayer;
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

    public synchronized KodiConnection getKodiConnection() {
        if (kodiConnection == null) {
            try {
                String address = PreferencesHelper.getKodiAddress();
                int port = PreferencesHelper.getKodiPort();
                kodiConnection = new KodiConnection(address, port);
            } catch (Exception e) {
                Log.e(TAG, "Error creating Kodi connection.", e);
            }
        }
        return kodiConnection;
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .minConsumerCount(CONSUMER_MIN_COUNT)
                .maxConsumerCount(CONSUMER_MAX_COUNT)
                .loadFactor(CONSUMER_LOAD_FACTOR)
                .consumerKeepAlive(CONSUMER_KEEP_ALIVE);
        jobManager = new JobManager(builder.build());
    }

    @SuppressWarnings("deprecation")
    public void acquireWakeLock() {
        if (wakeLock != null) {
            if (wakeLock.isHeld() == false) {
                wakeLock.acquire();
            }
        } else {
            PowerManager pm = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    TAG);
            wakeLock.setReferenceCounted(false);
            if (wakeLock.isHeld() == false) {
                wakeLock.acquire();
            }
        }
    }

    public void releaseWakeLock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IntroEvent event) {
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
        final MusicFragment musicFragment = (MusicFragment) viewPagerAdapter.getItem(POSITION_MUSIC_TAB);
        musicFragment.setPlayOnCreate(true);
        viewPager.setCurrentItem(POSITION_MUSIC_TAB);
        musicFragment.playIntro();
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                musicFragment.setPlayOnCreate(false);
            }
        }, MAX_DELAY_TO_PLAY_INTRO);
    }
}
