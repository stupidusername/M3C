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
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Timestamp;

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
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.KodiConnectionHelper;
import bei.m3c.helpers.PICConnectionHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.jobs.GetKodiActivePlayersJob;
import bei.m3c.jobs.GetVideoCategoriesJob;
import bei.m3c.jobs.GetVideosJob;
import bei.m3c.kodiMethods.PlayerOpenKodiMethod;
import bei.m3c.kodiMethods.PlayerPlayPauseKodiMethod;
import bei.m3c.kodiMethods.PlayerSetSpeedKodiMethod;
import bei.m3c.kodiMethods.PlayerStopKodiMethod;
import bei.m3c.models.Player;
import bei.m3c.models.Video;
import bei.m3c.models.VideoCategory;

/**
 * Video fragment
 */
public class VideoFragment extends Fragment {

    // views
    private LinearLayout selectionLayout;
    private LinearLayout playerLayout;
    private ListView categoriesListView;
    private GridView videosGridView;
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
    private ImageButton tvVolumeDownButton;
    private ImageButton tvVolumeUpButton;

    // adapters
    private VideoCategoryAdapter videoCategoryAdapter;
    private VideoAdapter videoAdapter;

    private Player player; // Kodi video player
    private Video selectedVideo;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        selectionLayout = (LinearLayout) view.findViewById(R.id.video_selection_layout);
        playerLayout = (LinearLayout) view.findViewById(R.id.video_player_layout);
        categoriesListView = (ListView) view.findViewById(R.id.video_categories_listview);
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
        tvVolumeDownButton = (ImageButton) view.findViewById(R.id.video_tv_volume_down_button);
        tvVolumeUpButton = (ImageButton) view.findViewById(R.id.video_tv_volume_up_button);

        // Set theme
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
                startVideo(video);
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerPlayPauseKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid));
            }
        });
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerSetSpeedKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid, -2));
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerStopKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid));
            }
        });
        fastForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerSetSpeedKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), player.playerid, 2));
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
        tvVolumeDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVolumeDownCommand());
            }
        });
        tvVolumeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICConnectionHelper.sendCommand(new TRCVolumeUpCommand());
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
        if (categoryPosition != AdapterView.INVALID_POSITION) {
            VideoCategory videoCategory = videoCategoryAdapter.getItem(categoryPosition);
            JobManagerHelper.getJobManager().addJobInBackground(new GetVideosJob(videoCategory));
        }
    }

    private void startVideo(Video video) {
        KodiConnectionHelper.sendMethod(new PlayerOpenKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), video.videoUrl));
        selectedVideo = video;
    }

    private void showSelectionLayout() {
        playerLayout.setVisibility(View.GONE);
        selectionLayout.setVisibility(View.VISIBLE);
    }

    private void showPlayerLayout(Video video) {
        if (selectedVideo != null) {
            Glide.with(this).load(video.coverUrl).centerCrop().placeholder(R.drawable.albumart_placeholder).crossFade().into(coverImageView);
            titleTextView.setText(video.title);
        }
        selectionLayout.setVisibility(View.GONE);
        playerLayout.setVisibility(View.VISIBLE);
    }

    private void showPlayButton() {
        playPauseButton.setImageResource(R.drawable.play);
        ThemeHelper.setImageButtonTheme(playPauseButton);
    }

    private void showPauseButton() {
        playPauseButton.setImageResource(R.drawable.pause);
        ThemeHelper.setImageButtonTheme(playPauseButton);
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
            showPlayerLayout(selectedVideo);
        } else {
            selectedVideo = null;
            showSelectionLayout();
        }
    }
}