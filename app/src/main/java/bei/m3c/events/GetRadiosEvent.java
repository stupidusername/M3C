package bei.m3c.events;

import java.util.List;

import bei.m3c.models.Radio;

public class GetRadiosEvent {
    public List<Radio> radios;

    public GetRadiosEvent(List<Radio> radios) {
        this.radios = radios;
    }
}
