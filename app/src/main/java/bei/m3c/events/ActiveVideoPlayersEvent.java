package bei.m3c.events;

import bei.m3c.models.Player;

public class ActiveVideoPlayersEvent {

    public Player[] players;

    public ActiveVideoPlayersEvent(Player[] players) {
        this.players = players;
    }
}
