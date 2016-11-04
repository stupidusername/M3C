package bei.m3c.events;

import bei.m3c.models.Player;

public class ActiveVideoPlayerEvent {

    public Player player;

    public ActiveVideoPlayerEvent(Player player) {
        this.player = player;
    }
}
