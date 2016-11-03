package bei.m3c.kodiMethods;

import java.util.HashMap;

public class PlayerSetSpeedKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.SetSpeed";
    public static final String PARAM_PLAYER_ID = "playerid";
    public static final String PARAM_SPEED = "speed";

    public PlayerSetSpeedKodiMethod(String id, int playerId, int speed) {
        super(id, METHOD, prepareParams(playerId, speed));
    }

    private static HashMap<String, Object> prepareParams(int playerId, int speed) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_PLAYER_ID, playerId);
        params.put(PARAM_SPEED, speed);

        return params;
    }
}
