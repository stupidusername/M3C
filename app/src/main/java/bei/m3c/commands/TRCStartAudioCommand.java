package bei.m3c.commands;

public class TRCStartAudioCommand extends BaseCommand {

    public static final String TAG = "TRCStartAudioCommand";
    public static final byte VALUE = 5;

    public boolean isPlaying;

    public TRCStartAudioCommand(boolean isPlaying) {
        super(TAG, VALUE, toByte(isPlaying));
        this.isPlaying = isPlaying;
    }
}
