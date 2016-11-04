package bei.m3c.kodiMethods;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import bei.m3c.events.ActiveVideoPlayerEvent;
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
        for (Player player : result.result) {
            if (player.type.equals(Player.TYPE_VIDEO)) {
                EventBus.getDefault().post(new ActiveVideoPlayerEvent(player));
            }
        }
    }
}
