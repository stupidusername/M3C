package bei.m3c.connections;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import bei.m3c.commands.BaseCommand;
import bei.m3c.commands.TRCIntroCommand;
import bei.m3c.commands.TRCKeepAliveCommand;
import bei.m3c.commands.TRCStatusCommand;
import bei.m3c.events.IntroEvent;
import bei.m3c.events.TRCStatusCommandEvent;
import bei.m3c.helpers.FormatHelper;

public class PICConnection extends BaseConnection {

    public static final int COMMAND_LENGTH = 12;
    public static final String TAG = "PICConnection";

    public PICConnection(String address, int port) {
        super(address, port, COMMAND_LENGTH, TAG);
    }

    @Override
    public void readCommand(byte[] command) {
        Log.v(TAG, "Received command: " + FormatHelper.asHexString(command));
        if (command.length > 0) {
            byte value = command[0];
            switch (value) {
                case TRCIntroCommand.VALUE:
                    EventBus.getDefault().post(new IntroEvent());
                    break;
                case TRCStatusCommand.VALUE:
                    EventBus.getDefault().post(new TRCStatusCommandEvent(new TRCStatusCommand(command)));
                    break;
            }
        }
    }

    @Override
    public BaseCommand getKeepAliveCommand() {
        return new TRCKeepAliveCommand();
    }
}
