package bei.m3c.commands;

public class TRCRecordLightSceneCommand extends BaseCommand {

    public static final String TAG = "TRCRecordLightSceneCommand";
    public static final byte VALUE = 7;

    public TRCRecordLightSceneCommand(byte sceneCode) {
        super(TAG, VALUE, sceneCode);
    }
}
