package bei.m3c.players;

import android.media.MediaPlayer;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bei.m3c.Application;
import bei.m3c.events.GetInfoEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.jobs.GetRadioSongsJob;
import bei.m3c.models.Radio;
import bei.m3c.models.Song;

public class MusicPlayer extends MediaPlayer {

    public static final String TAG = "MusicPlayer";

    private Radio radio = null;
    private List<Song> songs;
    private int songPosition = 0;
    private boolean ready = false;

    public MusicPlayer() {
        super();
        EventBus.getDefault().register(this);
        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ready = true;
                start();
            }
        });
    }

    public static MusicPlayer getInstance() {
        return Application.getInstance().getMusicPlayer();
    }

    public void selectRadio(Radio radio) {
        clearSelectedRadio();
        this.radio = radio;
        getSongs();
    }

    public void clearSelectedRadio() {
        radio = null;
        songs = new ArrayList<>();
        songPosition = 0;
        stop();
    }

    public Radio getRadio() {
        return radio;
    }

    private void getSongs() {
        JobManagerHelper.cancelJobsInBackground(GetRadioSongsJob.TAG);
        JobManagerHelper.getJobManager().addJobInBackground(new GetRadioSongsJob(radio));
    }

    public Song getCurrentSong() {
        return songs.get(songPosition);
    }

    public void play() {
        if (!ready) {
            if (songs.size() > 0) {
                try {
                    setDataSource(getCurrentSong().songUrl);
                    prepareAsync();
                } catch (Exception e) {
                    Log.e(TAG, "Error setting data source.", e);
                }
            } else {
                Log.i(TAG, "Song list is empty.");
            }
        } else {
            start();
        }
    }

    public void playNext() {
        stop();
        if (songPosition < songs.size() - 1) {
            songPosition++;
        } else {
            songPosition = 0;
        }
        play();
    }

    public void playPrevious() {
        stop();
        if (songPosition > 0) {
            songPosition--;
        } else {
            songPosition = songs.size() - 1;
        }
        play();
    }

    @Override
    public void stop() {
        ready = false;
        super.stop();
        reset();
    }

    @Subscribe
    public void onEvent(GetInfoEvent<List<Song>> event) {
        songs = event.info;
        Collections.shuffle(songs);
        play();
    }
}
