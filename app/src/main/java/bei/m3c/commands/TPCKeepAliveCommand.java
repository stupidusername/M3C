package bei.m3c.commands;

public class TPCKeepAliveCommand extends BaseCommand {

    public static final String TAG = "TPCKeepAliveCommand";
    public static final byte VALUE = 3;

    public TPCKeepAliveCommand() {
        super(TAG, VALUE);
    }
}
