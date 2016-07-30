package bei.m3c.events;

import java.util.List;

import bei.m3c.models.Song;

public class GetRadioSongsEvent {

    public List<Song> songs;

    public GetRadioSongsEvent(List<Song> songs) {
        this.songs = songs;
    }
}
