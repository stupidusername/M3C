package bei.m3c.connections;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import bei.m3c.commands.BaseCommand;
import bei.m3c.commands.TPCKeepAliveCommand;
import bei.m3c.commands.TPCTabStatusCommand;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.PowerHelper;

public class SGHConnection extends BaseConnection {

    public static final int COMMAND_LENGTH = 200;
    public static final int ACK_MAX_DELAY_MILLIS = 1000;
    public static final String TAG = "SGHConnection";

    private Timer ackTimer;

    public SGHConnection(String address, int port) {
        super(address, port, COMMAND_LENGTH, TAG);
    }

    @Override
    public boolean sendCommand(BaseCommand command) {
        boolean success = super.sendCommand(command);
        if (ackTimer == null) {
            ackTimer = new Timer();
            ackTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "ACK not received.");
                    disconnect();
                    ackTimer = null;
                }
            }, ACK_MAX_DELAY_MILLIS);
        }
        return success;
    }

    @Override
    public void readCommand(byte[] command) {
        Log.v(TAG, "Received command: " + FormatHelper.asHexString(command));
        if (command.length > 0) {
            byte value = command[0];
            switch (value) {
                case TPCKeepAliveCommand.VALUE:
                    if (ackTimer != null) {
                        ackTimer.cancel();
                        ackTimer = null;
                    }
                    break;
            }
        }
    }

    @Override
    public BaseCommand getKeepAliveCommand() {
        return new TPCTabStatusCommand(PowerHelper.isConnected());
    }
}
