package bei.m3c.events;

import java.util.List;

import bei.m3c.models.ChannelCategory;

public class GetChannelCategoriesEvent {
    public List<ChannelCategory> channelCategories;

    public GetChannelCategoriesEvent(List<ChannelCategory> channelCategories) {
        this.channelCategories = channelCategories;
    }
}
