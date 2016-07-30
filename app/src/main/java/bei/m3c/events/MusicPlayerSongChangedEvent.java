package bei.m3c.events;

import bei.m3c.models.Song;

public class MusicPlayerSongChangedEvent {

    public Song currentSong;

    public MusicPlayerSongChangedEvent(Song song) {
        currentSong = song;
    }
}
