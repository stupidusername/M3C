package bei.m3c.commands;

public class TPCGetAccountInfoCommand extends BaseCommand {

    public static final String TAG = "TPCGetAccountInfoCommand";
    public static final byte VALUE = 5;

    public TPCGetAccountInfoCommand() {
        super(TAG, VALUE);
    }
}
