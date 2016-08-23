package bei.m3c.commands;

import bei.m3c.helpers.FormatHelper;

public class TRCMacro extends BaseCommand {

    public static final String TAG = "TRCMacro";
    public static final byte VALUE = 18;

    public int number;

    public TRCMacro(int number) {
        super(TAG, VALUE, FormatHelper.asNumberByteArray(number));
        this.number = number;
    }
}
