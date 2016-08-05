package bei.m3c.connections;

import bei.m3c.commands.BaseCommand;
import bei.m3c.commands.TRCKeepAlive;

public class PICConnection extends BaseConnection {

    public static final int COMMAND_LENGTH = 12;
    public static final String TAG = "PICConnection";

    public PICConnection(String address, int port) {
        super(address, port, COMMAND_LENGTH, TAG);
    }

    @Override
    public void readCommand(byte[] command) {

    }

    @Override
    public BaseCommand getKeepAliveCommand() {
        return new TRCKeepAlive();
    }
}
