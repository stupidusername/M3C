package bei.m3c.helpers;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bei.m3c.activities.MainActivity;
import bei.m3c.commands.TPCStartAudioMessageCommand;
import bei.m3c.models.AppVersion;
import bei.m3c.models.AudioMessage;
import bei.m3c.models.BarArticle;
import bei.m3c.models.BarGroup;
import bei.m3c.models.Channel;
import bei.m3c.models.ChannelCategory;
import bei.m3c.models.Radio;
import bei.m3c.models.Service;
import bei.m3c.models.ServiceTariff;
import bei.m3c.models.Song;
import bei.m3c.services.M3SService;
import retrofit2.Call;

public final class M3SHelper {

    public static final String TAG = "M3SHelper";
    public static final String PROTOCOL = "http://";
    public static final String SERVICE_IMAGE_PATH = "/images/services/hotel.jpg";

    public static String getM3SUrl() throws IOException {
        String address = PreferencesHelper.getM3SAddress();
        if (address != null) {
            return PROTOCOL + address;
        } else {
            throw new IOException("M3S address is not set.");
        }
    }

    public static M3SService getM3SService() throws IOException {
        M3SService m3SService = MainActivity.getInstance().getM3SService();
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

    public static List<Service> getServices() {
        try {
            Call<List<Service>> call = getM3SService().getServices();
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting services.", e);
            return new ArrayList<>();
        }
    }

    public static List<ServiceTariff> getServiceTariffs() {
        try {
            Call<List<ServiceTariff>> call = getM3SService().getServiceTariffs();
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting service tariffs.", e);
            return new ArrayList<>();
        }
    }

    public static String getServicesImageUrl() {
        try {
            return getM3SUrl() + SERVICE_IMAGE_PATH;
        } catch (Exception e) {
            Log.e(TAG, "Cannot resolve services image url.", e);
            return null;
        }
    }

    public static AppVersion getUpdate() {
        try {
            Call<AppVersion> call = getM3SService().getUpdate();
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting update.", e);
            return null;
        }
    }

    public static AudioMessage getAudioMessage(TPCStartAudioMessageCommand command) {
        try {
            Call<AudioMessage> call = getM3SService().getAudioMessage(command.getTidName(), "" + command.roomNumber, command.getSuffix());
            return call.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error getting audio message.", e);
            return null;
        }
    }
}
