package bei.m3c.events;

import android.support.annotation.Nullable;

import bei.m3c.models.Song;

public class MusicPlayerUpdateEvent{

    public Song currentSong;

    public MusicPlayerUpdateEvent(Song song) {
        currentSong = song;
    }
}
