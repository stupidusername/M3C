package bei.m3c.helpers;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bei.m3c.Application;
import bei.m3c.models.BarArticle;
import bei.m3c.models.BarGroup;
import bei.m3c.models.Channel;
import bei.m3c.models.ChannelCategory;
import bei.m3c.models.Radio;
import bei.m3c.models.Song;
import bei.m3c.services.M3SService;
import retrofit2.Call;

public final class M3SHelper {

    public static final String TAG = "M3SHelper";
    public static final String PROTOCOL = "http://";

    public static String getM3SUrl() throws IOException {
        String address = PreferencesHelper.getM3SAddress();
        if (address != null) {
            return PROTOCOL + address;
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
            Call<List<Radio>> call = getM3SService().getRadios();
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting radios.", e);
            return new ArrayList<>();
        }
    }

    public static List<Song> getRadioSongs(int id) {
        try {
            Call<List<Song>> call = getM3SService().getRadioSongs(id);
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting radio songs.", e);
            return new ArrayList<>();
        }
    }

    public static List<ChannelCategory> getChannelCategories() {
        try {
            Call<List<ChannelCategory>> call = getM3SService().getChannelCategories();
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting channel categories.", e);
            return new ArrayList<>();
        }
    }

    public static List<Channel> getChannels(int categoryId) {
        try {
            Call<List<Channel>> call = getM3SService().getChannels(categoryId);
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting channels.", e);
            return new ArrayList<>();
        }
    }

    public static List<BarGroup> getBarGroups() {
        try {
            Call<List<BarGroup>> call = getM3SService().getBarGroups();
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bar groups.", e);
            return new ArrayList<>();
        }
    }

    public static List<BarArticle> getBarArticles(int id) {
        try {
            Call<List<BarArticle>> call = getM3SService().getBarArticles(id);
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bar articles.", e);
            return new ArrayList<>();
        }
    }
}
