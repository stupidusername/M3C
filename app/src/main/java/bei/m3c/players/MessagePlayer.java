package bei.m3c.players;

import android.media.AudioManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayDeque;

import bei.m3c.activities.MainActivity;
import bei.m3c.commands.TRCStartAudioMessageCommand;
import bei.m3c.events.MessagePlayerPlayEvent;
import bei.m3c.events.MessagePlayerStopEvent;
import bei.m3c.helpers.PICConnectionHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.VolumeHelper;
import bei.m3c.models.AudioMessage;

public class MessagePlayer extends android.media.MediaPlayer {

    public static final String TAG = "MessagePlayer";

    private AudioMessage currentMessage = null;
    private ArrayDeque<AudioMessage> messageQueue = new ArrayDeque<>();
    private boolean ready = false;
    private int previousVolume;

    public MessagePlayer() {
        super();
        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
                ready = true;
                play();
            }
        });
        this.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(android.media.MediaPlayer mp) {
                messagePlaybackFinished();
            }
        });
        this.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "Playback error. Playing next message.");
                messagePlaybackFinished();
                return true;
            }
        });
    }

    public static MessagePlayer getInstance() {
        return MainActivity.getInstance().getMessagePlayer();
    }

    public void addAudioMessage(AudioMessage message) {
        messageQueue.add(message);
        if (currentMessage == null) {
            // Message queue is stared
            EventBus.getDefault().post(new MessagePlayerPlayEvent());
            playNext();
        }
    }

    private void play() {
        if (!ready) {
            if (currentMessage != null) {
                try {
                    setAudioStreamType(AudioManager.STREAM_MUSIC);
                    setDataSource(currentMessage.audioMessageUrl);
                    prepareAsync();
                } catch (Exception e) {
                    Log.e(TAG, "Error setting data source. Playing next message.", e);
                    playNext();
                }
            }
        } else {
            PICConnectionHelper.sendCommand(new TRCStartAudioMessageCommand(true, currentMessage.key));
            previousVolume = VolumeHelper.getVolume();
            int newVolume = Math.round((float) (PreferencesHelper.getMessageVolumePercentage() * VolumeHelper.getMaxVolume()) / 100);
            VolumeHelper.setVolume(newVolume);
            start();
        }
    }

    private void playNext() {
        getNextMessage();
        if (currentMessage != null) {
            play();
        } else {
            // All messages are finished
            EventBus.getDefault().post(new MessagePlayerStopEvent());
        }
    }

    private void getNextMessage() {
        currentMessage = messageQueue.poll();
    }

    private void messagePlaybackFinished() {
        ready = false;
        reset();
        VolumeHelper.setVolume(previousVolume);
        PICConnectionHelper.sendCommand(new TRCStartAudioMessageCommand(false, currentMessage.key));
        currentMessage = null;
        playNext();
    }
}
