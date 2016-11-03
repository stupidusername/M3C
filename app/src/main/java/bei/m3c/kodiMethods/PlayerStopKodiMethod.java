package bei.m3c.kodiMethods;

import java.util.HashMap;

public class PlayerStopKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.Stop";
    public static final String PARAM_PLAYER_ID = "playerid";

    public PlayerStopKodiMethod(String id, int playerId) {
        super(id, METHOD, prepareParams(playerId));
    }

    private static HashMap<String, Object> prepareParams(int playerId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_PLAYER_ID, playerId);

        return params;
    }
}
