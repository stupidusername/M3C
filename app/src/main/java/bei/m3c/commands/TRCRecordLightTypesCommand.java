package bei.m3c.commands;

public class TRCRecordLightTypesCommand extends BaseCommand {

    public static final String TAG = "TRCRecordLightTypesCommand";
    public static final byte VALUE = 8;

    public byte[] lightTypes;

    public TRCRecordLightTypesCommand(byte[] lightTypes) {
        super(TAG, VALUE, lightTypes);
        this.lightTypes = lightTypes;
    }
}
