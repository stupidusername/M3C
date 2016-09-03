package bei.m3c.commands;

public class TRCGetStatusCommand extends BaseCommand {

    public static final String TAG = "TRCGetStatusCommand";
    public static final byte VALUE = 3;

    public TRCGetStatusCommand() {
        super(TAG, VALUE);
    }
}
