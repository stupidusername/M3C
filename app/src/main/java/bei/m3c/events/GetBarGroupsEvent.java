package bei.m3c.events;

import java.util.List;

import bei.m3c.models.BarGroup;
import bei.m3c.models.Radio;

public class GetBarGroupsEvent {
    public List<BarGroup> barGroups;

    public GetBarGroupsEvent(List<BarGroup> barGroups) {
        this.barGroups = barGroups;
    }
}
