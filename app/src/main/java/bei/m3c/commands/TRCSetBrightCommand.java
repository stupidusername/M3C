package bei.m3c.commands;

public class TRCSetBrightCommand extends BaseCommand {

    public static final String TAG = "TRCSetBrightCommand";
    public static final byte VALUE = 4;

    public byte[] lightValues;

    public TRCSetBrightCommand(byte[] lightValues) {
        super(TAG, VALUE, lightValues);
        this.lightValues = lightValues;
    }
}
