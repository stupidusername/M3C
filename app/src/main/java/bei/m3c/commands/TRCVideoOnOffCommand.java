package bei.m3c.commands;

public class TRCVideoOnOffCommand extends BaseCommand {

    public static final String TAG = "TRCVideoOnOffCommand";
    public static final byte VALUE = 13;

    public TRCVideoOnOffCommand() {
        super(TAG, VALUE);
    }
}
