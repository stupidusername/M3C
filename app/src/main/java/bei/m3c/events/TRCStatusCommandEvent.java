package bei.m3c.events;

import bei.m3c.commands.TRCStatusCommand;

public class TRCStatusCommandEvent {

    public TRCStatusCommand command;

    public TRCStatusCommandEvent(TRCStatusCommand command) {
        this.command = command;
    }
}
