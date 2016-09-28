package bei.m3c.commands;

public class TRCChannelUpCommand extends BaseCommand {

    public static final String TAG = "TRCChannelUpCommand";
    public static final byte VALUE = 14;

    public TRCChannelUpCommand() {
        super(TAG, VALUE);
    }
}
