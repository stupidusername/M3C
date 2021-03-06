package bei.m3c.connections;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import bei.m3c.commands.BaseCommand;
import bei.m3c.commands.TPCAccountInfoCommand;
import bei.m3c.commands.TPCPCStatusCommand;
import bei.m3c.commands.TPCStartAudioMessageCommand;
import bei.m3c.commands.TPCTabStatusCommand;
import bei.m3c.events.TPCAccountInfoCommandEvent;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.PowerHelper;
import bei.m3c.jobs.PlayMessageJob;
import bei.m3c.jobs.UpdateRebootJob;

public class SGHConnection extends BaseConnection {

    public static final int COMMAND_LENGTH = 200;
    public static final String TAG = "SGHConnection";

    public SGHConnection(String address, int port) {
        super(address, port, COMMAND_LENGTH, TAG);
    }

    @Override
    public void onConnected() {
        // Do nothing
    }

    @Override
    public void disconnect(boolean reconnect) {
        super.disconnect(reconnect);
    }

    @Override
    public void readCommand(byte[] command) {
        Log.v(TAG, "Received command: " + FormatHelper.asHexString(command));
        if (command.length > 0) {
            byte value = command[0];
            switch (value) {
                case TPCPCStatusCommand.VALUE:
                    // App can be updated or the device can be rebooted
                    TPCPCStatusCommand tpcpcStatusCommand = new TPCPCStatusCommand(command);
                    if (tpcpcStatusCommand.appCanBeUpdated) {
                        JobManagerHelper.getJobManager().addJobInBackground(new UpdateRebootJob());
                    }
                    break;
                case TPCAccountInfoCommand.VALUE:
                    EventBus.getDefault().post(new TPCAccountInfoCommandEvent(new TPCAccountInfoCommand(command)));
                    break;
                case TPCStartAudioMessageCommand.VALUE:
                    JobManagerHelper.getJobManager().addJobInBackground(new PlayMessageJob(new TPCStartAudioMessageCommand(command)));
                    break;
            }
        }
    }

    @Override
    public BaseCommand getKeepAliveCommand() {
        return new TPCTabStatusCommand(PowerHelper.isConnected(), false);
    }
}
