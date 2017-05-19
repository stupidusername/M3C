package bei.m3c.players;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bei.m3c.activities.MainActivity;
import bei.m3c.commands.TRCStartAudioCommand;
import bei.m3c.events.GetRadioSongsEvent;
import bei.m3c.events.MessagePlayerPlayEvent;
import bei.m3c.events.MessagePlayerStopEvent;
import bei.m3c.events.MusicPlayerPauseEvent;
import bei.m3c.events.MusicPlayerPlayEvent;
import bei.m3c.events.MusicPlayerSongChangedEvent;
import bei.m3c.events.MusicPlayerStopEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.PICConnectionHelper;
import bei.m3c.jobs.GetRadioSongsJob;
import bei.m3c.jobs.UpdateMusicPlayerJob;
import bei.m3c.models.Radio;
import bei.m3c.models.Song;

public class MusicPlayer extends MediaPlayer {

    public static final String TAG = "MusicPlayer";

    private Radio radio = null;
    private List<Song> songs = new ArrayList<>();
    private int songPosition = 0;
    private boolean ready = false;
    private boolean preparing = false;
    private boolean songChanged = false;
    private boolean resumeAfterMessagePlayed;

    public MusicPlayer() {
        super();
        EventBus.getDefault().register(this);
        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ready = true;
                preparing = false;
                if (songChanged) {
                    changeSong();
                    songChanged = false;
                    return;
                }
                EventBus.getDefault().post(new MusicPlayerSongChangedEvent(getCurrentSong()));
                play();
            }
        });
        this.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        });
        this.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "Playback error (what: " + what + ", extra: " + extra + "). Playing next song.");
                preparing = false;
                playNext();
                return true;
            }
        });
    }

    public static MusicPlayer getInstance() {
        return MainActivity.getInstance().getMusicPlayer();
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
        if (!preparing) {
            stop();
        }
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

    public int getRemainingTime() {
        return getDuration() - getCurrentPosition();
    }

    public boolean isReady() {
        return ready;
    }

    public void play() {
        if (!ready) {
            if (songs.size() > 0) {
                if (!preparing) {
                    try {
                        String urlStr = getCurrentSong().songUrl;
                        URL url = new URL(urlStr);
                        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                        url = uri.toURL();
                        setAudioStreamType(AudioManager.STREAM_MUSIC);
                        setDataSource(url.toString());
                        if (!preparing) {
                            preparing = true;
                            prepareAsync();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting data source. Playing next song.", e);
                        preparing = false;
                        playNext();
                    }
                }
            } else {
                Log.i(TAG, "Song list is empty.");
            }
        } else {
            JobManagerHelper.getJobManager().addJobInBackground(new UpdateMusicPlayerJob());
            EventBus.getDefault().post(new MusicPlayerPlayEvent(getCurrentSong()));
            // Send start audio command to pic
            PICConnectionHelper.sendCommand(new TRCStartAudioCommand(true));
            start();
        }
    }

    public void playNext() {
        if (songPosition < songs.size() - 1) {
            songPosition++;
        } else {
            songPosition = 0;
        }
        changeSong();
    }

    public void playPrevious() {
        if (songPosition > 0) {
            songPosition--;
        } else {
            songPosition = songs.size() - 1;
        }
        changeSong();
    }

    public void changeSong() {
        if (!preparing) {
            stop();
            play();
        } else {
            songChanged = true;
        }
    }

    @Override
    public void stop() {
        ready = false;
        JobManagerHelper.cancelJobsInBackground(UpdateMusicPlayerJob.TAG);
        EventBus.getDefault().post(new MusicPlayerStopEvent());
        // Send stop audio command only if the player is playing. Otherwise it would be sent already.
        if (isPlaying()) {
            PICConnectionHelper.sendCommand(new TRCStartAudioCommand(false));
        }
        super.stop();
        reset();
    }

    @Override
    public void pause() {
        JobManagerHelper.cancelJobsInBackground(UpdateMusicPlayerJob.TAG);
        EventBus.getDefault().post(new MusicPlayerPauseEvent(getCurrentSong()));
        PICConnectionHelper.sendCommand(new TRCStartAudioCommand(false));
        super.pause();
    }

    @Subscribe
    public void onEvent(GetRadioSongsEvent event) {
        songs = event.songs;
        Collections.shuffle(songs);
        if (preparing) {
            songChanged = true;
        } else {
            play();
        }
    }

    @Subscribe
    public void onEvent(MessagePlayerPlayEvent event) {
        resumeAfterMessagePlayed = isPlaying();
        if (isPlaying()) {
            pause();
        }
    }

    @Subscribe
    public void onEvent(MessagePlayerStopEvent event) {
        if (resumeAfterMessagePlayed) {
            play();
        }
    }
}
