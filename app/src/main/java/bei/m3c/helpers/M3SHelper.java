package bei.m3c.helpers;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bei.m3c.Application;
import bei.m3c.models.Radio;
import bei.m3c.services.M3SService;
import retrofit2.Call;

public final class M3SHelper {

    public static final String TAG = "M3SHelper";
    public static final String PROTOCOL = "http://";

    public static String getM3SUrl()  throws  IOException {
        String address = PreferencesHelper.getM3SAddress();
        if (address != null) {
            return PROTOCOL + PreferencesHelper.getM3SAddress();
        } else {
            throw new IOException("M3S address is not set.");
        }
    }

    public static M3SService getM3SService() throws IOException {
        M3SService m3SService = Application.getInstance().getM3SService();
        if (m3SService == null) {
            throw new IOException("M3Service is not set.");
        }
        return m3SService;
    }

    public static List<Radio> getRadios() {
        try {
            Call<List<Radio>> call = getM3SService().listRadios();
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting radios.", e);
            return new ArrayList<>();
        }
    }
}
