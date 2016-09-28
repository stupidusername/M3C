package bei.m3c.commands;

public class TRCChannelDownCommand extends BaseCommand {

    public static final String TAG = "TRCChannelDownCommand";
    public static final byte VALUE = 15;

    public TRCChannelDownCommand() {
        super(TAG, VALUE);
    }
}
