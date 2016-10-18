package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Timestamp;
import java.util.ArrayList;

import bei.m3c.R;
import bei.m3c.adapters.VideoAdapter;
import bei.m3c.adapters.VideoCategoryAdapter;
import bei.m3c.events.GetVideoCategoriesEvent;
import bei.m3c.events.GetVideosEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.KodiConnectionHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.jobs.GetVideoCategoriesJob;
import bei.m3c.jobs.GetVideosJob;
import bei.m3c.kodiMethods.PlayerOpenKodiMethod;
import bei.m3c.kodiMethods.PlayerPlayPauseKodiMethod;
import bei.m3c.kodiMethods.PlayerSetSpeedKodiMethod;
import bei.m3c.kodiMethods.PlayerStopKodiMethod;
import bei.m3c.models.Video;
import bei.m3c.models.VideoCategory;

/**
 * Video fragment
 */
public class VideoFragment extends Fragment {

    // Views
    private LinearLayout listViewHeaderLayout;
    private ListView listView;
    private ImageButton playPauseButton;
    private ImageButton rewindButton;
    private ImageButton stopButton;
    private ImageButton fastForwardButton;
    // Adapters
    private VideoCategoryAdapter videoCategoryAdapter;
    private VideoAdapter videoAdapter;
    // Variables
    private VideoCategory selectedVideoCategory;

    @Override
    public void onDestroyView() {
        JobManagerHelper.cancelJobsInBackground(GetVideoCategoriesJob.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewHeaderLayout = (LinearLayout) view.findViewById(R.id.video_listview_header_layout);
        listView = (ListView) view.findViewById(R.id.video_listview);
        playPauseButton = (ImageButton) view.findViewById(R.id.video_play_pause_button);
        rewindButton = (ImageButton) view.findViewById(R.id.video_rewind_button);
        stopButton = (ImageButton) view.findViewById(R.id.video_stop_button);
        fastForwardButton = (ImageButton) view.findViewById(R.id.video_fast_forward_button);

        videoCategoryAdapter = new VideoCategoryAdapter(getLayoutInflater(savedInstanceState));
        videoAdapter = new VideoAdapter(getLayoutInflater(savedInstanceState));

        ThemeHelper.setColorStateListTheme(listViewHeaderLayout);
        ThemeHelper.setImageButtonTheme(playPauseButton);
        ThemeHelper.setImageButtonTheme(rewindButton);
        ThemeHelper.setImageButtonTheme(stopButton);
        ThemeHelper.setImageButtonTheme(fastForwardButton);

        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetVideoCategoriesJob());

        // Register UI listeners
        listViewHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoCategories();
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerPlayPauseKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), 1));
            }
        });
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerSetSpeedKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), 1, -2));
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerStopKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), 1));
            }
        });
        fastForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KodiConnectionHelper.sendMethod(new PlayerSetSpeedKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), 1, 2));
            }
        });

        // Show videos or categories depending on saved video category
        if (selectedVideoCategory == null) {
            showVideoCategories();
        } else {
            showVideos();
        }
    }

    public void showVideoCategories() {
        // Clear selected video
        selectedVideoCategory = null;
        // Hide category header
        listViewHeaderLayout.setVisibility(View.GONE);
        // Populate listview with video categories
        listView.setAdapter(videoCategoryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Clear old listview and save selected category
                videoAdapter.replaceList(new ArrayList<Video>());
                selectedVideoCategory = videoCategoryAdapter.getItem(position);
                showVideos();
            }
        });
    }

    public void showVideos() {
        // Show category header
        listViewHeaderLayout.setVisibility(View.VISIBLE);
        // Populate listview with videos
        JobManagerHelper.getJobManager().addJobInBackground(new GetVideosJob(selectedVideoCategory));
        listView.setAdapter(videoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = videoAdapter.getItem(position);
                KodiConnectionHelper.sendMethod(new PlayerOpenKodiMethod(new Timestamp(System.currentTimeMillis()).toString(), video.videoUrl));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetVideoCategoriesEvent event) {
        videoCategoryAdapter.replaceList(event.videoCategories);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetVideosEvent event) {
        videoAdapter.replaceList(event.videos);
    }
}
