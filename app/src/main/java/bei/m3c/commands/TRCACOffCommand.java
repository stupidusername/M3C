package bei.m3c.commands;

public class TRCACOffCommand extends BaseCommand {

    public static final String TAG = "TRCACOffCommand";
    public static final byte VALUE = 10;

    public TRCACOffCommand() {
        super(TAG, VALUE);
    }
}
