package bei.m3c.models;

import com.google.gson.annotations.SerializedName;

public class AudioMessage {

    public static final String TAG = "AudioMessage";

    public int id;

    public int key;

    public String name;

    @SerializedName("name_spanish")
    public String nameSpanish;

    public String filename;

    public int kind;

    @SerializedName("audio_output")
    public int audioOutput;

    public int delay;

    public int manual;

    public String audioMessageUrl;

    public AudioMessage(int id, int key, String name, String nameSpanish, String filename, int kind, int audioOutput, int delay, int manual, String audioMessageUrl) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.nameSpanish = nameSpanish;
        this.filename = filename;
        this.kind = kind;
        this.audioOutput = audioOutput;
        this.delay = delay;
        this.manual = manual;
        this.audioMessageUrl = audioMessageUrl;
    }
}