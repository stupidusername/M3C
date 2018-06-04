package bei.m3c.commands;

public class TPCStartAudioMessageCommand extends BaseCommand {

    public static final String TAG = "TPCAccountInfoCommand";
    public static final byte VALUE = 4;

    public int audioMessageKey;
    public int roomNumber;

    public TPCStartAudioMessageCommand(int audioMessageKey, int roomNumber) {
        super(TAG, VALUE, (byte) audioMessageKey, (byte) roomNumber);
        this.audioMessageKey = audioMessageKey;
        this.roomNumber = roomNumber;
    }

    public TPCStartAudioMessageCommand(byte[] command) {
        this(command[1], command[2]);
    }
}