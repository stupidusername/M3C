package bei.m3c.kodiMethods;

import java.util.HashMap;

public class PlayerSetSubtitleKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.SetSubtitle";
    public static final String PARAM_PLAYER_ID = "playerid";
    public static final String PARAM_SUBTITLE = "subtitle";

    public PlayerSetSubtitleKodiMethod(String id, int playerId, String subtitle) {
        super(id, METHOD, prepareParams(playerId, subtitle));
    }

    private static HashMap<String, Object> prepareParams(int playerId, String subtitle) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_PLAYER_ID, playerId);
        params.put(PARAM_SUBTITLE, subtitle);

        return params;
    }
}
