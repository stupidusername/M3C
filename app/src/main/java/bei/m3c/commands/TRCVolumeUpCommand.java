package bei.m3c.commands;

public class TRCVolumeUpCommand extends BaseCommand {

    public static final String TAG = "TRCVolumeUpCommand";
    public static final byte VALUE = 16;

    public TRCVolumeUpCommand() {
        super(TAG, VALUE);
    }
}
