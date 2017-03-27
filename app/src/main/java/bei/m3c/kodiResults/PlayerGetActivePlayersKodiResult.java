package bei.m3c.kodiResults;

import org.json.JSONObject;

import bei.m3c.models.Player;

public class PlayerGetActivePlayersKodiResult extends BaseKodiResult {

    public Player[] result;

    public PlayerGetActivePlayersKodiResult(JSONObject jsonObject) {
        super(jsonObject);
        result = Player.getPlayersFromJSONObject(jsonObject);
    }
}
