package bei.m3c.events;

import bei.m3c.models.PlayerProperties;

public class PlayerPropertiesEvent {

    public PlayerProperties properties;

    public PlayerPropertiesEvent(PlayerProperties properties) {
        this.properties = properties;
    }
}
