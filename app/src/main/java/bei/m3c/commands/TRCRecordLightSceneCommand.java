package bei.m3c.commands;

public class TRCRecordLightSceneCommand extends BaseCommand {

    public static final String TAG = "TRCRecordLightSceneCommand";
    public static final byte VALUE = 7;
    public static final byte CODE_INTRO_INIT = 0;
    public static final byte CODE_INTRO_END = 1;
    public static final byte CODE_CLEAN = 2;

    public TRCRecordLightSceneCommand(byte sceneCode) {
        super(TAG, VALUE, sceneCode);
    }
}
