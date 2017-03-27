package bei.m3c.kodiMethods;

import org.greenrobot.eventbus.EventBus;

import bei.m3c.activities.MainActivity;
import bei.m3c.events.ActiveVideoPlayersEvent;
import bei.m3c.kodiResults.PlayerGetActivePlayersKodiResult;

public class PlayerGetActivePlayersKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.GetActivePlayers";

    public PlayerGetActivePlayersKodiMethod(String id) {
        super(id, METHOD);
    }

    public void processResult(String readString) {
        PlayerGetActivePlayersKodiResult result = MainActivity.getInstance().getGson().fromJson(readString, PlayerGetActivePlayersKodiResult.class);
        EventBus.getDefault().post(new ActiveVideoPlayersEvent(result.result));
    }
}
