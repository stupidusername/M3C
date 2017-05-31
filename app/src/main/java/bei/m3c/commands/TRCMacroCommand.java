package bei.m3c.commands;

import bei.m3c.helpers.FormatHelper;

public class TRCMacroCommand extends BaseCommand {

    public static final String TAG = "TRCMacroCommand";
    public static final byte VALUE = 18;
    public static final int MACRO_LENGTH = 3;

    public int number;

    public TRCMacroCommand(int number) {
        super(TAG, VALUE, getMacro(FormatHelper.asNumberByteArray(number)));
        this.number = number;
    }

    private static byte[] getMacro(byte[] number) {
        byte[] macro = new byte[MACRO_LENGTH];
        System.arraycopy(number, 0, macro, MACRO_LENGTH - number.length, number.length);
        return macro;
    }
}
