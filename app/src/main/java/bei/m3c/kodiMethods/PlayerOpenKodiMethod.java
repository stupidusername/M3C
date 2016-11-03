package bei.m3c.kodiMethods;

import java.util.HashMap;

public class PlayerOpenKodiMethod extends BaseKodiMethod {

    public static final String METHOD = "Player.Open";
    public static final String ITEM_FILE = "file";
    public static final String PARAM_ITEM = "item";

    public PlayerOpenKodiMethod(String id, String file) {
        super(id, METHOD, prepareParams(file));
    }

    private static HashMap<String, Object> prepareParams(String file) {
        HashMap<String, String> item = new HashMap<>();
        item.put(ITEM_FILE, file);

        HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_ITEM, item);

        return params;
    }
}
