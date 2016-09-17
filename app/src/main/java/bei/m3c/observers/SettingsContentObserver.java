package bei.m3c.observers;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

import bei.m3c.events.VolumeChangedEvent;

public class SettingsContentObserver extends ContentObserver {

    Context context;

    public SettingsContentObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        EventBus.getDefault().post(new VolumeChangedEvent(currentVolume));
    }
}