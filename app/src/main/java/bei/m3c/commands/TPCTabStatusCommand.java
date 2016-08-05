package bei.m3c.commands;

public class TPCTabStatusCommand extends BaseCommand {

    public static final String TAG = "TPCTabStatusCommand";
    public static final byte VALUE = 2;

    public boolean powerConnected;

    public TPCTabStatusCommand(boolean powerConnected) {
        super(TAG, VALUE, toByte(powerConnected));
        this.powerConnected = powerConnected;
    }
}
