package bei.m3c.helpers;

import bei.m3c.Application;
import bei.m3c.commands.BaseCommand;
import bei.m3c.jobs.SendCommandJob;

public final class PICConnectionHelper {

    public static void sendCommand(BaseCommand command) {
        sendCommand(command, SendCommandJob.DEFAULT_INTERVAL);
    }

    public static void sendCommand(BaseCommand command, int interval) {
        Application.getInstance().getPICConnection().addCommandJob(command, interval);
    }
}