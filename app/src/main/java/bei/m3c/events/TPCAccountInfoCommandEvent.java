package bei.m3c.events;

import bei.m3c.commands.TPCAccountInfo;

public class TPCAccountInfoCommandEvent {

    public TPCAccountInfo command;

    public TPCAccountInfoCommandEvent(TPCAccountInfo command) {
        this.command = command;
    }
}
