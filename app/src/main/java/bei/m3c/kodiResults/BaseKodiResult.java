package bei.m3c.kodiResults;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseKodiResult {

    public String jsonrpc;
    public String id;

    public BaseKodiResult(JSONObject jsonObject) {
        try {
            jsonrpc = jsonObject.getString("jsonrpc");
        } catch (JSONException e) {
            jsonrpc = null;
        }
        try {
            id = jsonObject.getString("id");
        } catch (JSONException e) {
            id = null;
        }
    }
}
