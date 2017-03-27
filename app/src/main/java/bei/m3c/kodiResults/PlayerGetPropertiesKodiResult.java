package bei.m3c.kodiResults;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import bei.m3c.models.PlayerProperties;

public class PlayerGetPropertiesKodiResult extends BaseKodiResult {

    public static final String TAG = "PropertiesKodiResult";

    public PlayerProperties result;

    public PlayerGetPropertiesKodiResult(JSONObject jsonObject) {
        super(jsonObject);
        try {
            result = new PlayerProperties(jsonObject.getJSONObject("result"));
        } catch (JSONException e) {
            Log.e(TAG, "Error getting result object", e);
        }
    }
}
