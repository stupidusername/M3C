package bei.m3c.models;

import com.google.gson.annotations.SerializedName;

/**
 * Kodi player properties model
 */
public class PlayerProperties {

    public int speed;

    public GlobalTime time;

    @SerializedName("totaltime")
    public GlobalTime totalTime;
}
