package bei.m3c.connections;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import bei.m3c.commands.BaseCommand;
import bei.m3c.commands.TPCAccountInfo;
import bei.m3c.commands.TPCKeepAliveCommand;
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
                case TPCPCStatusCommand.VALUE:
                    // App can be updated or the device can be rebooted
                    TPCPCStatusCommand tpcpcStatusCommand = new TPCPCStatusCommand(command);
                    if (tpcpcStatusCommand.appCanBeUpdated) {
                        JobManagerHelper.getJobManager().addJobInBackground(new UpdateRebootJob());
                    }
                    break;
                case TPCAccountInfo.VALUE:
                    EventBus.getDefault().post(new TPCAccountInfoCommandEvent(new TPCAccountInfo(command)));
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
