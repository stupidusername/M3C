package bei.m3c.kodiMethods;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import bei.m3c.events.ActiveVideoPlayersEvent;
import bei.m3c.kodiResults.PlayerGetActivePlayersKodiResult;

public class PlayerGetActivePlayersKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.GetActivePlayers";

    public PlayerGetActivePlayersKodiMethod(String id) {
        super(id, METHOD);
    }

    public void processResult(JSONObject jsonObject) {
        PlayerGetActivePlayersKodiResult result = new PlayerGetActivePlayersKodiResult(jsonObject);
        EventBus.getDefault().post(new ActiveVideoPlayersEvent(result.result));
    }
}
