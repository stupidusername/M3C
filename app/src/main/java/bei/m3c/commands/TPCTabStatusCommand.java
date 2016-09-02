package bei.m3c.commands;

public class TPCTabStatusCommand extends BaseCommand {

    public static final String TAG = "TPCTabStatusCommand";
    public static final byte VALUE = 2;

    public boolean powerConnected;
    public boolean deviceRebooting;

    public TPCTabStatusCommand(boolean powerConnected, boolean deviceRebooting) {
        super(TAG, VALUE, toByte(powerConnected), toByte(deviceRebooting));
        this.powerConnected = powerConnected;
        this.deviceRebooting = deviceRebooting;
    }
}
