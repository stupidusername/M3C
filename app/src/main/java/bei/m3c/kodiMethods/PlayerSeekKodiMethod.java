package bei.m3c.kodiMethods;

import java.util.HashMap;

import bei.m3c.models.GlobalTime;

public class PlayerSeekKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.Seek";
    public static final String PARAM_PLAYER_ID = "playerid";
    public static final String PARAM_VALUE = "value";

    public PlayerSeekKodiMethod(String id, int playerId, GlobalTime time) {
        super(id, METHOD, prepareParams(playerId, time));
    }

    private static HashMap<String, Object> prepareParams(int playerId, GlobalTime time) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_PLAYER_ID, playerId);
        params.put(PARAM_VALUE, time);

        return params;
    }
}
