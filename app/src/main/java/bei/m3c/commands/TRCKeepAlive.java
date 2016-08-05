package bei.m3c.commands;

public class TRCKeepAlive extends BaseCommand {

    public static final String TAG = "TRCKeepAlive";
    public static final byte VALUE = 2;

    public TRCKeepAlive() {
        super(TAG, VALUE);
    }
}
