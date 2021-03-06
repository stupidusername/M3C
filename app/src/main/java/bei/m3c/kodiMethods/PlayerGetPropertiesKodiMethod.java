package bei.m3c.kodiMethods;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import bei.m3c.activities.MainActivity;
import bei.m3c.events.PlayerPropertiesEvent;
import bei.m3c.kodiResults.PlayerGetPropertiesKodiResult;

public class PlayerGetPropertiesKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.GetProperties";
    public static final String PARAM_PLAYER_ID = "playerid";
    public static final String PARAM_PROPERTIES = "properties";

    public static final String PROPERTY_SPEED = "speed";
    public static final String PROPERTY_TIME = "time";
    public static final String PROPERTY_TOTAL_TIME = "totaltime";

    public PlayerGetPropertiesKodiMethod(String id, int playerId) {
        super(id, METHOD, prepareParams(playerId));
    }

    private static HashMap<String, Object> prepareParams(int playerId) {
        String[] properties = {PROPERTY_SPEED, PROPERTY_TIME, PROPERTY_TOTAL_TIME};
        HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_PLAYER_ID, playerId);
        params.put(PARAM_PROPERTIES, properties);

        return params;
    }

    public void processResult(String readString) {
        PlayerGetPropertiesKodiResult result = MainActivity.getInstance().getGson().fromJson(readString, PlayerGetPropertiesKodiResult.class);
        EventBus.getDefault().post(new PlayerPropertiesEvent(result.result));
    }
}
