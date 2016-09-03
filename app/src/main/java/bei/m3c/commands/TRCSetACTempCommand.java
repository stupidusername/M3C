package bei.m3c.commands;

public class TRCSetACTempCommand extends BaseCommand {

    public static final String TAG = "TRCSetACTempCommand";
    public static final byte VALUE = 11;

    public int acTempCode;

    public TRCSetACTempCommand(int acTempCode) {
        super(TAG, VALUE, (byte) acTempCode);
        this.acTempCode = acTempCode;
    }
}
