package bei.m3c.commands;

public class TRCACOnCommand extends BaseCommand {

    public static final String TAG = "TRCACOnCommand";
    public static final byte VALUE = 9;

    public int acTempCode;

    public TRCACOnCommand(int acTempCode) {
        super(TAG, VALUE, (byte) acTempCode);
        this.acTempCode = acTempCode;
    }
}
