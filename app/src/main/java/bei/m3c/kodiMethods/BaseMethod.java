package bei.m3c.kodiMethods;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public abstract class BaseMethod {

    @SuppressWarnings("unused")
    public String jsonrpc = "2.0";
    public String id;
    public String method;
    public Map<String, Object> params;

    public BaseMethod(String id, String method, Map<String, Object> params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public String getJsonRPCString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
