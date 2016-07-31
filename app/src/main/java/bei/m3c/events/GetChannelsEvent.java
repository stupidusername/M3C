package bei.m3c.events;

import java.util.List;

import bei.m3c.models.Channel;

public class GetChannelsEvent {
    public List<Channel> channels;

    public GetChannelsEvent(List<Channel> channels) {
        this.channels = channels;
    }
}
