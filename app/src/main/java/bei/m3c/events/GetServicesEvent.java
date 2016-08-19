package bei.m3c.events;

import java.util.List;

import bei.m3c.models.Service;

public class GetServicesEvent {
    public List<Service> services;

    public GetServicesEvent(List<Service> services) {
        this.services = services;
    }
}
