package bei.m3c.helpers;

import android.content.Context;
import android.media.AudioManager;

import bei.m3c.Application;

/**
 * Contains methods to manipulate sound volume
 */
public final class VolumeHelper {

    public static final int FLAGS_SET_VOLUME = 0;
    public static final int SCALE_FACTOR = 5; // This scale factor is used so seekbar changes look smoother

    public static int getMaxVolume() {
        return getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC) * SCALE_FACTOR;
    }

    public static int getVolume() {
        return getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC) * SCALE_FACTOR;
    }

    public static void setVolume(int volume) {
        getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, Math.round((float) volume / SCALE_FACTOR), FLAGS_SET_VOLUME);
    }

    private static AudioManager getAudioManager() {
        return (AudioManager) Application.getInstance().getSystemService(Context.AUDIO_SERVICE);
    }
}
