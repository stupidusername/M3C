package bei.m3c.commands;

public class TRCKeepAliveCommand extends BaseCommand {

    public static final String TAG = "TRCKeepAliveCommand";
    public static final byte VALUE = 2;

    public TRCKeepAliveCommand() {
        super(TAG, VALUE);
    }
}
