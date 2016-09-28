package bei.m3c.commands;

import bei.m3c.helpers.FormatHelper;

public class TRCMacroCommand extends BaseCommand {

    public static final String TAG = "TRCMacroCommand";
    public static final byte VALUE = 18;

    public int number;

    public TRCMacroCommand(int number) {
        super(TAG, VALUE, FormatHelper.asNumberByteArray(number));
        this.number = number;
    }
}
