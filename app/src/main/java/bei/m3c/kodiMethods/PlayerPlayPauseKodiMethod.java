package bei.m3c.kodiMethods;

import java.util.HashMap;

public class PlayerPlayPauseKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.PlayPause";
    public static final String PARAM_PLAYER_ID = "playerid";

    public PlayerPlayPauseKodiMethod(String id, int playerId) {
        super(id, METHOD, prepareParams(playerId));
    }

    private static HashMap<String, Object> prepareParams(int playerId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_PLAYER_ID, playerId);

        return params;
    }
}
