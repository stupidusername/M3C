package bei.m3c.commands;

public class TRCSetVideoTypeCommand extends BaseCommand {

    public static final String TAG = "TRCSetVideoTypeCommand";
    public static final byte VALUE = 30;

    public int tvRemoteCode;

    public TRCSetVideoTypeCommand(int tvRemoteCode) {
        super(TAG, VALUE, (byte) tvRemoteCode);
        this.tvRemoteCode = tvRemoteCode;
    }
}
