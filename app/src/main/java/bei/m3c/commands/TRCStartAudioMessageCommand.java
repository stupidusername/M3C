package bei.m3c.commands;

public class TRCStartAudioMessageCommand extends BaseCommand {

    public static final String TAG = "TRCStartAudioMessageCommand";
    public static final byte VALUE = 5;

    public boolean isPlaying;
    public int audioMessageKey;

    public TRCStartAudioMessageCommand(boolean isPlaying, int audioMessageKey) {
        super(TAG, VALUE, toByte(isPlaying), (byte) audioMessageKey);
        this.isPlaying = isPlaying;
        this.audioMessageKey = audioMessageKey;
    }
}
