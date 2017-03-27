package bei.m3c.kodiMethods;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bei.m3c.activities.MainActivity;
import bei.m3c.kodiResults.BaseKodiResult;

public abstract class BaseKodiMethod {

    @SuppressWarnings("unused")
    public String jsonrpc = "2.0";
    public String id;
    public String method;
    public Map<String, Object> params;

    public BaseKodiMethod(String id, String method) {
        this(id, method, new HashMap<String, Object>());
    }

    public BaseKodiMethod(String id, String method, Map<String, Object> params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public String getJsonRPCString() {
        return MainActivity.getInstance().getGson().toJson(this);
    }

    /**
     * This method does nothings but an implementation can be done on child classes
     */
    public void processResult(JSONObject jsonObject) {
    }
}
