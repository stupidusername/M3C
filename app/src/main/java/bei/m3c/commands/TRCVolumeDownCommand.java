package bei.m3c.commands;

public class TRCVolumeDownCommand extends BaseCommand {

    public static final String TAG = "TRCVolumeDownCommand";
    public static final byte VALUE = 17;

    public TRCVolumeDownCommand() {
        super(TAG, VALUE);
    }
}
