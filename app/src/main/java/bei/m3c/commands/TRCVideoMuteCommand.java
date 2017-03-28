package bei.m3c.commands;

public class TRCVideoMuteCommand extends BaseCommand {

    public static final String TAG = "TRCVideoMuteCommand";
    public static final byte VALUE = 32;

    public TRCVideoMuteCommand() {
        super(TAG, VALUE);
    }
}
