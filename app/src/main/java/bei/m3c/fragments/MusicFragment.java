package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import bei.m3c.R;
import bei.m3c.adapters.RadioAdapter;
import bei.m3c.events.GetRadiosEvent;
import bei.m3c.events.MusicPlayerPauseEvent;
import bei.m3c.events.MusicPlayerPlayEvent;
import bei.m3c.events.MusicPlayerSongChangedEvent;
import bei.m3c.events.MusicPlayerStopEvent;
import bei.m3c.events.MusicPlayerUpdateEvent;
import bei.m3c.events.IntroEvent;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.helpers.VolumeHelper;
import bei.m3c.jobs.GetRadiosJob;
import bei.m3c.jobs.UpdateMusicPlayerJob;
import bei.m3c.models.Radio;
import bei.m3c.models.Song;
import bei.m3c.players.MusicPlayer;

/**
 * Music fragment
 */
public class MusicFragment extends Fragment {

    public static final int DEFAULT_RADIO_POSITION = 0;

    // views
    private ListView radiosListView;
    private ImageButton playPauseButton;
    private ImageButton previousButton;
    private ImageButton stopButton;
    private ImageButton nextButton;
    private ImageButton volumeButton;
    private ImageView albumartImageView;
    private TextView titleTextView;
    private TextView artistTextView;
    private TextView albumTextView;
    private TextView timeElapsedTextView;
    private TextView timeRemainingTextView;
    private SeekBar timeSeekbar;
    private SeekBar volumeSeekbar;
    // adapters
    private RadioAdapter radioAdapter;
    // Save volume value before mute
    private int savedVolume;
    // play radio on view created
    private boolean playOnCreate = false;

    @Override
    public void onDestroyView() {
        JobManagerHelper.cancelJobsInBackground(GetRadiosJob.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radiosListView = (ListView) view.findViewById(R.id.music_listview);
        playPauseButton = (ImageButton) view.findViewById(R.id.music_play_pause_button);
        previousButton = (ImageButton) view.findViewById(R.id.music_previous_button);
        stopButton = (ImageButton) view.findViewById(R.id.music_stop_button);
        nextButton = (ImageButton) view.findViewById(R.id.music_next_button);
        volumeButton = (ImageButton) view.findViewById(R.id.music_volume_button);

        albumartImageView = (ImageView) view.findViewById(R.id.music_albumart_imageview);
        titleTextView = (TextView) view.findViewById(R.id.music_title_textview);
        artistTextView = (TextView) view.findViewById(R.id.music_artist_textview);
        albumTextView = (TextView) view.findViewById(R.id.music_album_textview);

        timeElapsedTextView = (TextView) view.findViewById(R.id.music_time_elapsed_textview);
        timeRemainingTextView = (TextView) view.findViewById(R.id.music_time_remaining_textview);
        timeSeekbar = (SeekBar) view.findViewById(R.id.music_time_seekbar);
        volumeSeekbar = (SeekBar) view.findViewById(R.id.music_volume_seekbar);

        // Set radio adapter
        radioAdapter = new RadioAdapter(getLayoutInflater(savedInstanceState));
        radiosListView.setAdapter(radioAdapter);
        radiosListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Set UI theme
        ThemeHelper.setImageButtonTheme(previousButton);
        ThemeHelper.setImageButtonTheme(stopButton);
        ThemeHelper.setImageButtonTheme(nextButton);
        ThemeHelper.setSeekBarTheme(timeSeekbar);
        ThemeHelper.setSeekBarTheme(volumeSeekbar);

        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetRadiosJob());

        // Set volume seekbar max value and update with current volume
        volumeSeekbar.setMax(VolumeHelper.getMaxVolume());
        updateVolumeSeekbar();
        saveVolume();
        updateVolumeButton();

        // Set UI listeners
        radiosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Change selected radio if no previous one was selected or if the radio id is different
                Radio selectedRadio = radioAdapter.getItem(position);
                Radio currentRadio = MusicPlayer.getInstance().getRadio();
                if (currentRadio == null || selectedRadio.id != currentRadio.id) {
                    MusicPlayer.getInstance().selectRadio(selectedRadio);
                }
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playPrevious();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().clearSelectedRadio();
                radiosListView.clearChoices();
                radioAdapter.notifyDataSetChanged();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playNext();
            }
        });
        timeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (MusicPlayer.getInstance().isReady()) {
                    timeRemainingTextView.setText(FormatHelper.asTimer(seekbar.getMax() - progress));
                    timeElapsedTextView.setText(FormatHelper.asTimer(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
                // Cancel timer update when the user is using the time seekbar
                if (MusicPlayer.getInstance().isPlaying()) {
                    JobManagerHelper.cancelJobsInBackground(UpdateMusicPlayerJob.TAG);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                // Change music player playback position and re enable the music player update job
                if (MusicPlayer.getInstance().isReady()) {
                    MusicPlayer.getInstance().seekTo(seekbar.getProgress());
                    if (MusicPlayer.getInstance().isPlaying()) {
                        JobManagerHelper.getJobManager().addJobInBackground(new UpdateMusicPlayerJob());
                    }
                }
            }
        });
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                VolumeHelper.setVolume(progress);
                updateVolumeButton();
                if (!fromUser) {
                    saveVolume();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
                saveVolume();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                saveVolume();
            }
        });

        // Set play pause button according music player
        if (MusicPlayer.getInstance().isPlaying()) {
            setPauseButton();
        } else {
            setPlayButton();
        }
        // Update playback information if needed
        if (MusicPlayer.getInstance().isReady()) {
            updateSong(MusicPlayer.getInstance().getCurrentSong());
            updatePlaybackTime();
        }
    }

    private void setPlayButton() {
        playPauseButton.setImageResource(R.drawable.play);
        ThemeHelper.setImageButtonTheme(playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
    }

    private void setPauseButton() {
        playPauseButton.setImageResource(R.drawable.pause);
        ThemeHelper.setImageButtonTheme(playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().pause();
            }
        });
    }

    /**
     * Plays current selected radio or chooses the first one if none of them is selected.
     */
    private void play() {
        Radio currentRadio = MusicPlayer.getInstance().getRadio();
        if (currentRadio == null) {
            if (!radioAdapter.isEmpty()) {
                Radio newRadio = radioAdapter.getItem(DEFAULT_RADIO_POSITION);
                radiosListView.setItemChecked(DEFAULT_RADIO_POSITION, true);
                MusicPlayer.getInstance().selectRadio(newRadio);
            }
        } else {
            MusicPlayer.getInstance().play();
        }
    }

    public void updateSong(Song currentSong) {
        // Set text views
        titleTextView.setText(currentSong.title);
        artistTextView.setText(currentSong.author);
        albumTextView.setText(currentSong.album);
        timeRemainingTextView.setText(FormatHelper.asTimer(MusicPlayer.getInstance().getRemainingTime()));
        // Set time seekbar max value
        timeSeekbar.setMax(MusicPlayer.getInstance().getDuration());
        // Set albumart image
        Glide.with(this).load(currentSong.albumartUrl).centerCrop().placeholder(R.drawable.albumart_placeholder).crossFade().into(albumartImageView);
    }

    public void updatePlaybackTime() {
        timeSeekbar.setProgress(MusicPlayer.getInstance().getCurrentPosition());
    }

    public void updateVolumeSeekbar() {
        volumeSeekbar.setProgress(VolumeHelper.getVolume());
    }

    public void updateVolumeButton() {
        int volume = VolumeHelper.getVolume();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolumeHelper.setVolume(0);
                updateVolumeSeekbar();
            }
        };
        if (volume == 0) {
            volumeButton.setImageResource(R.drawable.volume_muted);
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VolumeHelper.setVolume(savedVolume);
                    updateVolumeSeekbar();
                }
            };
        } else if (volume <= VolumeHelper.getMaxVolume() / 2) {
            volumeButton.setImageResource(R.drawable.volume_low);
        } else {
            volumeButton.setImageResource(R.drawable.volume_high);
        }
        volumeButton.setOnClickListener(onClickListener);
        ThemeHelper.setImageButtonTheme(volumeButton);
    }

    public void saveVolume() {
        int volume = VolumeHelper.getVolume();
        if (volume > 0) {
            savedVolume = volume;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetRadiosEvent event) {
        radioAdapter.replaceList(event.radios);
        // play radio if needed
        if (playOnCreate) {
            playIntro();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicPlayerPlayEvent event) {
        setPauseButton();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicPlayerPauseEvent event) {
        setPlayButton();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicPlayerStopEvent event) {
        setPlayButton();
        // Set default texts
        titleTextView.setText(getString(R.string.song));
        artistTextView.setText(getString(R.string.artist));
        albumTextView.setText(getString(R.string.album));
        timeElapsedTextView.setText(getString(R.string.time_default));
        timeRemainingTextView.setText(getString(R.string.time_default));
        // Reset time seekbar progress
        timeSeekbar.setProgress(0);
        // Set default albumart image
        albumartImageView.setImageResource(R.drawable.albumart_placeholder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicPlayerSongChangedEvent event) {
        updateSong(event.currentSong);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicPlayerUpdateEvent event) {
        updatePlaybackTime();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IntroEvent event) {
        playIntro();
    }

    public void setPlayOnCreate(boolean playOnCreate) {
        this.playOnCreate = playOnCreate;
    }

    public void playIntro() {
        playOnCreate = false;
        int volume = Math.round((float) (PreferencesHelper.getIntroVolumePercentage() * VolumeHelper.getMaxVolume()) / 100);
        VolumeHelper.setVolume(volume);
        updateVolumeButton();
        updateVolumeSeekbar();
        play();
    }
}
