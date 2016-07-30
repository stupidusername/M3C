package bei.m3c.helpers;

import android.content.Context;
import android.media.AudioManager;

import bei.m3c.Application;

/**
 * Contains methods to manipulate sound volume
 */
public final class VolumeHelper {

    public static final int FLAGS_SET_VOLUME = 0;

    public static int getMaxVolume() {
        return getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getVolume() {
        return getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setVolume(int volume) {
        getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, volume, FLAGS_SET_VOLUME);
    }

    private static AudioManager getAudioManager() {
        return (AudioManager) Application.getInstance().getSystemService(Context.AUDIO_SERVICE);
    }
}
