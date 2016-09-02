package bei.m3c.services;

import java.util.List;

import bei.m3c.models.AppVersion;
import bei.m3c.models.BarArticle;
import bei.m3c.models.BarGroup;
import bei.m3c.models.Channel;
import bei.m3c.models.ChannelCategory;
import bei.m3c.models.Radio;
import bei.m3c.models.Service;
import bei.m3c.models.ServiceTariff;
import bei.m3c.models.Song;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface M3SService {
    @GET("api/get-radios")
    Call<List<Radio>> getRadios();

    @GET("api/get-radio-songs")
    Call<List<Song>> getRadioSongs(@Query("id") int id);

    @GET("api/get-channel-categories")
    Call<List<ChannelCategory>> getChannelCategories();

    @GET("api/get-channels")
    Call<List<Channel>> getChannels(@Query("categoryId") int id);

    @GET("api/get-bar-groups")
    Call<List<BarGroup>> getBarGroups();

    @GET("api/get-bar-articles")
    Call<List<BarArticle>> getBarArticles(@Query("id") int id);

    @GET("api/get-services")
    Call<List<Service>> getServices();

    @GET("api/get-service-tariffs")
    Call<List<ServiceTariff>> getServiceTariffs();

    @GET("api/get-update")
    Call<AppVersion> getUpdate();
}
