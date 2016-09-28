package bei.m3c.commands;

import bei.m3c.helpers.FormatHelper;

public class TRCDigitCommand extends BaseCommand {

    public static final String TAG = "TRCDigitCommand";
    public static final byte BASE_VALUE = 20;

    public int number;

    public TRCDigitCommand(int number) {
        super(TAG, (byte) (BASE_VALUE + number));
        this.number = number;
    }
}
