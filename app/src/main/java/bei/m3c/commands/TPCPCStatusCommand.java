package bei.m3c.commands;

public class TPCPCStatusCommand extends BaseCommand {

    public static final String TAG = "TPCPCStatusCommand";
    public static final byte VALUE = 1;

    public boolean appCanBeUpdated;

    public TPCPCStatusCommand(boolean appCanBeUpdated) {
        super(TAG, VALUE, toByte(appCanBeUpdated));
        this.appCanBeUpdated = appCanBeUpdated;
    }

    public TPCPCStatusCommand(byte[] command) {
        this(toBoolean(command[1]));
    }
}
