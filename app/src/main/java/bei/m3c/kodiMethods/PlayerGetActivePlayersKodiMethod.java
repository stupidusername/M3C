package bei.m3c.kodiMethods;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import bei.m3c.events.ActiveVideoPlayersEvent;
import bei.m3c.kodiResults.PlayerGetActivePlayersKodiResult;
import bei.m3c.models.Player;

public class PlayerGetActivePlayersKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.GetActivePlayers";

    public PlayerGetActivePlayersKodiMethod(String id) {
        super(id, METHOD);
    }

    public void processResult(String readString) {
        Gson gson = new Gson();
        PlayerGetActivePlayersKodiResult result = gson.fromJson(readString, PlayerGetActivePlayersKodiResult.class);
        EventBus.getDefault().post(new ActiveVideoPlayersEvent(result.result));
    }
}
