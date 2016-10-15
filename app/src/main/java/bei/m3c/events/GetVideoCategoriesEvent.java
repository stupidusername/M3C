package bei.m3c.events;

import java.util.List;

import bei.m3c.models.VideoCategory;

public class GetVideoCategoriesEvent {
    public List<VideoCategory> videoCategories;

    public GetVideoCategoriesEvent(List<VideoCategory> videoCategories) {
        this.videoCategories = videoCategories;
    }
}
