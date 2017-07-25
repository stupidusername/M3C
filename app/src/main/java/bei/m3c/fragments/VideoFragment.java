package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bei.m3c.R;
import bei.m3c.adapters.VideoAdapter;
import bei.m3c.adapters.VideoCategoryAdapter;
import bei.m3c.commands.TRCVideoOnOffCommand;
import bei.m3c.commands.TRCVideoSourceCommand;
import bei.m3c.commands.TRCVolumeDownCommand;
import bei.m3c.commands.TRCVolumeUpCommand;
import bei.m3c.events.ActiveVideoPlayersEvent;
import bei.m3c.events.GetVideoCategoriesEvent;
import bei.m3c.events.GetVideosEvent;
import bei.m3c.events.PlayerPropertiesEvent;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.KodiConnectionHelper;
import bei.m3c.helpers.PICConnectionHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.interfaces.FragmentInterface;
import bei.m3c.jobs.GetKodiActivePlayersJob;
import bei.m3c.jobs.GetVideoCategoriesJob;
import bei.m3c.jobs.GetVideosJob;
import bei.m3c.kodiMethods.PlayerGetPropertiesKodiMethod;
import bei.m3c.kodiMethods.PlayerOpenKodiMethod;
import bei.m3c.kodiMethods.PlayerPlayPauseKodiMethod;
import bei.m3c.kodiMethods.PlayerSeekKodiMethod;
import bei.m3c.kodiMethods.PlayerSetSpeedKodiMethod;
import bei.m3c.kodiMethods.PlayerSetSubtitleKodiMethod;
import bei.m3c.kodiMethods.PlayerStopKodiMethod;
import bei.m3c.models.GlobalTime;
import bei.m3c.models.Player;
import bei.m3c.models.PlayerProperties;
import bei.m3c.models.Video;
import bei.m3c.models.VideoCategory;
import bei.m3c.widgets.HoldAndRepeatImageButton;
import bei.m3c.widgets.ToastWidget;

/**
 * Video fragment
 */
public class VideoFragment extends Fragment implements FragmentInterface {

    public static final int SET_SUBTITLE_RETRY_MILLIS = 1000;
    public static final String DEFAULT_VIDEO_TITLE = "";
    public static final int ENABLE_UPDATE_PLAYER_DELAY_MILLIS = 1500;
    public static final int RESUME_PLAYER_DELAY_MILLIS = 500;

    // views
    private LinearLayout selectionLayout;
    private LinearLayout playerLayout;
    private ListView categoriesListView;
    private ProgressBar categoriesListViewLoadingProgressBar;
    private GridView videosGridView;
    private ProgressBar videosGridViewLoadingProgressBar;
    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView timeElapsedTextView;
    private SeekBar timeSeekbar;
    private TextView timeRemainingTextView;
    private ImageButton playPauseButton;
    private ImageButton rewindButton;
    private ImageButton stopButton;
    private ImageButton fastForwardButton;
    private LinearLayout tvControlsLayout;
    private ImageButton tvPowerButton;
    private ImageButton tvSrcButton;
    private HoldAndRepeatImageButton tvVolumeDownButton;
    private HoldAndRepeatImageButton tvVolumeUpButton;

    // adapters
    private VideoCategoryAdapter videoCategoryAdapter;
    private VideoAdapter videoAdapter;

    private Player player; // Kodi video player
    private PlayerProperties properties;
    private Video selectedVideo;
    private boolean playerShowed = false;
    private boolean updatePlayer = true;
    private boolean displayWarning = false;
    private boolean videoLoaded = false;
    private boolean subtitlesLoaded = false;
    // Toast widget
    private ToastWidget toastWidget;
    // Timers
    private Timer enableUpdatePlayerTimer = null;
    private Timer resumePlayerTimer = null;

    @Override
    public void onDestroyView() {
        JobManagerHelper.cancelJobsInBackground(GetVideoCategoriesJob.TAG);
        JobManagerHelper.cancelJobsInBackground(GetKodiActivePlayersJob.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Player is not loaded yet
        playerShowed = false;

        // Find views
        selectionLayout = (LinearLayout) view.findViewById(R.id.video_selection_layout);
        playerLayout = (LinearLayout) view.findViewById(R.id.video_player_layout);
        categoriesListView = (ListView) view.findViewById(R.id.video_categories_listview);
        categoriesListViewLoadingProgressBar = (ProgressBar) view.findViewById(R.id.video_categories_listview_loading_progress_bar);
        videosGridViewLoadingProgressBar = (ProgressBar) view.findViewById(R.id.videos_gridview_loading_progress_bar);
        videosGridView = (GridView) view.findViewById(R.id.videos_gridview);
        coverImageView = (ImageView) view.findViewById(R.id.video_cover_imageview);
        titleTextView = (TextView) view.findViewById(R.id.video_title_textview);
        timeElapsedTextView = (TextView) view.findViewById(R.id.video_time_elapsed_textview);
        timeSeekbar = (SeekBar) view.findViewById(R.id.video_time_seekbar);
        timeRemainingTextView = (TextView) view.findViewById(R.id.video_time_remaining_textview);
        playPauseButton = (ImageButton) view.findViewById(R.id.video_play_pause_button);
        rewindButton = (ImageButton) view.findViewById(R.id.video_rewind_button);
        stopButton = (ImageButton) view.findViewById(R.id.video_stop_button);
        fastForwardButton = (ImageButton) view.findViewById(R.id.video_fast_forward_button);
        tvControlsLayout = (LinearLayout) view.findViewById(R.id.video_tv_controls_layout);
        tvPowerButton = (ImageButton) view.findViewById(R.id.video_tv_power_button);
        tvSrcButton = (ImageButton) view.findViewById(R.id.video_tv_src_button);
        tvVolumeDownButton = (HoldAndRepeatImageButton) view.findViewById(R.id.video_tv_volume_down_button);
        tvVolumeUpButton = (HoldAndRepeatImageButton) view.findViewById(R.id.video_tv_volume_up_button);

        // Set theme
        ThemeHelper.setProgressBarTheme(categoriesListViewLoadingProgressBar);
        ThemeHelper.setProgressBarTheme(videosGridViewLoadingProgressBar);
        ThemeHelper.setSeekBarTheme(timeSeekbar);
        ThemeHelper.setImageButtonTheme(playPauseButton);
        ThemeHelper.setImageButtonTheme(rewindButton);
        ThemeHelper.setImageButtonTheme(stopButton);
        ThemeHelper.setImageButtonTheme(fastForwardButton);
        ThemeHelper.setColorStateListTheme(tvControlsLayout);
        ThemeHelper.setImageButtonTheme(tvPowerButton);
        ThemeHelper.setImageButtonTheme(tvSrcButton);
        ThemeHelper.setImageButtonTheme(tvVolumeDownButton);
        ThemeHelper.setImageButtonTheme(tvVolumeUpButton);

        categoriesListView.setEmptyView(categoriesListViewLoadingProgressBar);
        videosGridView.setEmptyView(videosGridViewLoadingProgressBar);

        videoCategoryAdapter = new VideoCategoryAdapter(getLayoutInflater(savedInstanceState));
        categoriesListView.setAdapter(videoCategoryAdapter);
        categoriesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        videoAdapter = new VideoAdapter(getLayoutInflater(savedInstanceState));
        videosGridView.setAdapter(videoAdapter);

        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetVideoCategoriesJob());
        JobManagerHelper.getJobManager().addJobInBackground(new GetKodiActivePlayersJob());

        // Set UI click listeners
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadSelectedCategoryVideos();
            }
        });
        videosGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = (Video) parent.getItemAtPosition(position);
                displayWarning = true;
                startVideo(video);
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerPlayPauseKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid));
                updatePlayer = true;
            }
        });
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speed = -2;
                if (properties.speed < 0) {
                    if (properties.speed > -32) {
                        speed = properties.speed * 2;
                    } else {
                        speed = properties.speed;
                    }
                }
                KodiConnectionHelper.sendMethod(new PlayerSetSpeedKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid, speed));
                updatePlayer = true;
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerStopKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid));
                updatePlayer = true;
            }
        });
        fastForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speed = 2;
                if (properties.speed > 0) {
                    if (properties.speed < 32) {
                        speed = properties.speed * 2;
                    } else {
                        speed = properties.speed;
                    }
                }
                KodiConnectionHelper.sendMethod(new PlayerSetSpeedKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid, speed));
                updatePlayer = true;
            }
        });
        tvPowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVideoOnOffCommand());
            }
        });
        tvSrcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVideoSourceCommand());
            }
        });
        tvVolumeDownButton.setRepeatableAction(new Runnable() {
            @Override
            public void run() {
                PICConnectionHelper.sendCommand(new TRCVolumeDownCommand());
            }
        });
        tvVolumeUpButton.setRepeatableAction(new Runnable() {
            @Override
            public void run() {
                PICConnectionHelper.sendCommand(new TRCVolumeUpCommand());
            }
        });
        timeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (progress != seekbar.getMax()) {
                    progress = progress - progress % 1000;
                }
                timeRemainingTextView.setText(FormatHelper.asTimer(seekbar.getMax() - progress));
                timeElapsedTextView.setText(FormatHelper.asTimer(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
                updatePlayer = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                KodiConnectionHelper.sendMethod(new PlayerSeekKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid, new GlobalTime(seekbar.getProgress())));
                // Avoid Kodi bug when seeking on a paused video
                if (properties.speed == 0) {
                    KodiConnectionHelper.sendMethod(new PlayerPlayPauseKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid));
                    if (resumePlayerTimer != null) {
                        resumePlayerTimer.cancel();
                    }
                    resumePlayerTimer = new Timer();
                    resumePlayerTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Avoid job tag duplication
                            KodiConnectionHelper.sendMethod(new PlayerSetSpeedKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid, 0));
                            resumePlayerTimer = null;
                        }
                    }, RESUME_PLAYER_DELAY_MILLIS);
                }
                if (enableUpdatePlayerTimer != null) {
                    enableUpdatePlayerTimer.cancel();
                }
                enableUpdatePlayerTimer = new Timer();
                enableUpdatePlayerTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        updatePlayer = true;
                        enableUpdatePlayerTimer = null;
                    }
                }, ENABLE_UPDATE_PLAYER_DELAY_MILLIS);

            }
        });
    }

    private void selectDefaultCategory() {
        // Select default category if no one is selected and the list view is not empty
        int categoryPosition = categoriesListView.getCheckedItemPosition();
        if (categoryPosition == AdapterView.INVALID_POSITION && categoriesListView.getCount() > 0) {
            categoriesListView.setItemChecked(0, true);
        }
    }

    private void loadSelectedCategoryVideos() {
        int categoryPosition = categoriesListView.getCheckedItemPosition();
        if (!videoCategoryAdapter.isEmpty() && categoryPosition != AdapterView.INVALID_POSITION) {
            VideoCategory videoCategory = videoCategoryAdapter.getItem(categoryPosition);
            JobManagerHelper.getJobManager().addJobInBackground(new GetVideosJob(videoCategory));
        }
        // Clear selection if apadater is empty
        if (videoCategoryAdapter.isEmpty()) {
            videoAdapter.replaceList(new ArrayList<Video>());
        }
    }

    private void startVideo(Video video) {
        selectedVideo = video;
        videoLoaded = false;
        subtitlesLoaded = false;
        JobManagerHelper.cancelJobsInBackground(PlayerSetSubtitleKodiMethod.METHOD);
        KodiConnectionHelper.sendMethod(new PlayerOpenKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), video.videoUrl));
    }

    private void showSelectionLayout() {
        playerShowed = false;
        // Clear player layout
        Glide.with(this).load(R.drawable.video_cover_placeholder).centerCrop().dontAnimate().into(coverImageView);
        titleTextView.setText(DEFAULT_VIDEO_TITLE);
        timeElapsedTextView.setText(getString(R.string.time_default_with_hours));
        timeSeekbar.setProgress(0);
        timeRemainingTextView.setText(getString(R.string.time_default_with_hours));
        showPlayButton();
        // Show selection layout
        playerLayout.setVisibility(View.GONE);
        selectionLayout.setVisibility(View.VISIBLE);
    }

    private void showPlayerLayout(Video video) {
        playerShowed = true;
        if (selectedVideo != null) {
            Glide.with(this).load(video.coverUrl).centerCrop().placeholder(R.drawable.video_cover_placeholder).dontAnimate().into(coverImageView);
            if (titleTextView.getText() == null || !titleTextView.getText().equals(video.title)) {
                titleTextView.setText(video.title);
            }
        }
        if (playerLayout.getVisibility() != View.VISIBLE) {
            selectionLayout.setVisibility(View.GONE);
            playerLayout.setVisibility(View.VISIBLE);
        }
        if (displayWarning) {
            showWarning();
            displayWarning = false;
        }
    }

    private void showPlayButton() {
        playPauseButton.setImageResource(R.drawable.play);
        ThemeHelper.setImageButtonTheme(playPauseButton);
    }

    private void showPauseButton() {
        playPauseButton.setImageResource(R.drawable.pause);
        ThemeHelper.setImageButtonTheme(playPauseButton);
    }

    private void updatePlayer() {
        if (updatePlayer) {
            if (properties.speed == 1) {
                showPauseButton();
            } else {
                showPlayButton();
            }
            timeSeekbar.setMax(properties.totalTime.toMilliseconds());
            timeSeekbar.setProgress(properties.time.toMilliseconds());
        }
    }

    private void showWarning() {
        if (toastWidget == null && getContext() != null) {
            toastWidget = new ToastWidget(getContext(), getContext().getString(R.string.video_warning), getActivity().findViewById(android.R.id.content));
        }
        if (toastWidget != null) {
            toastWidget.flash();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetVideoCategoriesEvent event) {
        videoCategoryAdapter.replaceList(event.videoCategories);
        selectDefaultCategory();
        loadSelectedCategoryVideos();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetVideosEvent event) {
        videoAdapter.replaceList(event.videos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ActiveVideoPlayersEvent event) {
        Player foundPlayer = null;
        for (Player eventPlayer : event.players) {
            if (eventPlayer.type.equals(Player.TYPE_VIDEO)) {
                foundPlayer = eventPlayer;
                break;
            }
        }
        player = foundPlayer;
        if (player != null) {
            if (!playerShowed) {
                showPlayerLayout(selectedVideo);
            }
            KodiConnectionHelper.sendMethod(new PlayerGetPropertiesKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid));
            // add subtitles if needed
            if (!subtitlesLoaded && selectedVideo != null && selectedVideo.subtitleUrl != null) {
                KodiConnectionHelper.sendMethod(
                        new PlayerSetSubtitleKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid, selectedVideo.subtitleUrl),
                        SET_SUBTITLE_RETRY_MILLIS, true);
                subtitlesLoaded = true;
            }
            videoLoaded = true;
        } else if (playerShowed){
            if (videoLoaded) {
                selectedVideo = null;
                videoLoaded = false;
            }
            showSelectionLayout();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerPropertiesEvent event) {
        properties = event.properties;
        updatePlayer();
    }

    @Override
    public void fragmentBecameVisible() {
    }

    @Override
    public void fragmentBecameInvisible() {
        if (toastWidget != null) {
            toastWidget.dismiss();
        }
    }
}