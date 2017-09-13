package bei.m3c.connections;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import bei.m3c.commands.BaseCommand;
import bei.m3c.commands.TRCIntroCommand;
import bei.m3c.commands.TRCKeepAliveCommand;
import bei.m3c.commands.TRCRecordLightTypesCommand;
import bei.m3c.commands.TRCStatusCommand;
import bei.m3c.events.IntroEvent;
import bei.m3c.events.TRCStatusCommandEvent;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.PICConnectionHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.models.Light;

public class PICConnection extends BaseConnection {

    public static final int COMMAND_LENGTH = 12;
    public static final String TAG = "PICConnection";
    public static final int LIGHT_TYPES_RECORD_RETRY_INTERVAL_MILLIS = 1000;

    public PICConnection(String address, int port) {
        super(address, port, COMMAND_LENGTH, TAG);
    }

    @Override
    public void onConnected() {
        List<Light> lights = PreferencesHelper.getLights();
        byte[] lightTypes = new byte[Light.MAX_LIGHTS];
        for(int i = 0; i < lights.size(); i++) {
            lightTypes[i] = (byte) lights.get(i).type;
        }
        PICConnectionHelper.sendCommand(new TRCRecordLightTypesCommand(lightTypes), LIGHT_TYPES_RECORD_RETRY_INTERVAL_MILLIS, true);
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
