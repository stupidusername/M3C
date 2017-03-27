package bei.m3c.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Kodi player properties model
 */
public class PlayerProperties {

    public static String TAG = "PlayerProperties";

    public int speed;

    public GlobalTime time;

    @SerializedName("totaltime")
    public GlobalTime totalTime;

    public  PlayerProperties(JSONObject jsonObject) {
        try {
            speed = jsonObject.getInt("speed");
            time = new GlobalTime(jsonObject.getJSONObject("time"));
            totalTime = new GlobalTime(jsonObject.getJSONObject("totaltime"));
        } catch (JSONException e) {
            Log.e(TAG, "Error creating from JSONObject", e);
            speed = 0;
            time = null;
            totalTime = null;
        }
    }
}
