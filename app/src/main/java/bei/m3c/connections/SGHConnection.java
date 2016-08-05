package bei.m3c.connections;

import bei.m3c.commands.BaseCommand;
import bei.m3c.commands.TPCTabStatusCommand;
import bei.m3c.helpers.PowerHelper;

public class SGHConnection extends BaseConnection {

    public static final int COMMAND_LENGTH = 200;
    public static final String TAG = "SGHConnection";

    public SGHConnection(String address, int port) {
        super(address, port, COMMAND_LENGTH, TAG);
    }

    @Override
    public void readCommand(byte[] command) {

    }

    @Override
    public BaseCommand getKeepAliveCommand() {
        return new TPCTabStatusCommand(PowerHelper.isConnected());
    }
}
