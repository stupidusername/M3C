package bei.m3c.commands;

import android.util.Log;

public class TPCStartAudioMessageCommand extends BaseCommand {

    public static final String TAG = "TPCAccountInfoCommand";
    public static final byte VALUE = 4;
    public static final String ALARM_TID_NAME = "EventWakeUp";

    public int audioMessageKey;
    public int roomNumber;
    public int suffixCode;

    public TPCStartAudioMessageCommand(int audioMessageKey, int roomNumber, int suffixCode) {
        super(TAG, VALUE, (byte) audioMessageKey, (byte) roomNumber, (byte) suffixCode);
        this.audioMessageKey = audioMessageKey;
        this.roomNumber = roomNumber;
        this.suffixCode = suffixCode;
    }

    public TPCStartAudioMessageCommand(byte[] command) {
        this(command[1], command[2], command[3]);
    }

    public String getSuffix() {
        String suffix = "";
        try {
            String[] suffixes = {
                    "M",
                    "T",
                    "N"
            };
            suffix = suffixes[suffixCode];
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Suffix not found", e);
        }
        return suffix;
    }
}