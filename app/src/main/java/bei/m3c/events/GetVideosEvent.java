package bei.m3c.events;

import java.util.List;

import bei.m3c.models.Video;

public class GetVideosEvent {
    public List<Video> videos;

    public GetVideosEvent(List<Video> videos) {
        this.videos = videos;
    }
}
