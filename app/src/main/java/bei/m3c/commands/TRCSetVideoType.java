package bei.m3c.commands;

public class TRCSetVideoType extends BaseCommand {

    public static final String TAG = "TRCSetVideoTyp";
    public static final byte VALUE = 30;

    public int tvRemoteCode;

    public TRCSetVideoType(int tvRemoteCode) {
        super(TAG, VALUE, (byte) tvRemoteCode);
        this.tvRemoteCode = tvRemoteCode;
    }
}
