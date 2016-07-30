package bei.m3c.events;

import bei.m3c.models.Song;

public class MusicPlayerPauseEvent {

    public Song currentSong;

    public MusicPlayerPauseEvent(Song song) {
        currentSong = song;
    }
}
