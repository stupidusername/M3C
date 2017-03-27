package bei.m3c.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Kodi player model
 */
public class Player {

    public static final String TAG = "Player";

    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_PICTURE = "picture";

    public String type;
    public int playerid;

    public Player(int playerid, String type) {
        this.playerid = playerid;
        this.type = type;
    }

    public static Player[] getPlayersFromJSONObject(JSONObject jsonObject) {
        ArrayList<Player> players = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childJSONObject = jsonArray.getJSONObject(i);
                try {
                    Player player = new Player(childJSONObject.getInt("playerid"), childJSONObject.getString("type"));
                    players.add(player);
                } catch (Exception e) {
                    Log.e(TAG, "Error creating player", e);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating players", e);
        }
        return players.toArray(new Player[0]);
    }
}
