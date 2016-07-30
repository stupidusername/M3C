package bei.m3c.events;

import bei.m3c.models.Song;

public class MusicPlayerPlayEvent {

    public Song currentSong;

    public MusicPlayerPlayEvent(Song song) {
        currentSong = song;
    }
}
