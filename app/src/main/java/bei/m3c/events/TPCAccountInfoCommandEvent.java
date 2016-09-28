package bei.m3c.events;

import bei.m3c.commands.TPCAccountInfoCommand;

public class TPCAccountInfoCommandEvent {

    public TPCAccountInfoCommand command;

    public TPCAccountInfoCommandEvent(TPCAccountInfoCommand command) {
        this.command = command;
    }
}
