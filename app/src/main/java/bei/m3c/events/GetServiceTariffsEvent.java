package bei.m3c.events;

import java.util.List;

import bei.m3c.models.ServiceTariff;

public class GetServiceTariffsEvent {
    public List<ServiceTariff> serviceTariffs;

    public GetServiceTariffsEvent(List<ServiceTariff> serviceTariffs) {
        this.serviceTariffs = serviceTariffs;
    }
}
