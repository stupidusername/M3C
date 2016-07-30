package bei.m3c.services;

import java.util.List;

import bei.m3c.models.BarArticle;
import bei.m3c.models.BarGroup;
import bei.m3c.models.Radio;
import bei.m3c.models.Song;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface M3SService {
    @GET("api/get-radios")
    Call<List<Radio>> getRadios();

    @GET("api/get-radio-songs")
    Call<List<Song>> getRadioSongs(@Query("id") int id);

    @GET("api/get-bar-groups")
    Call<List<BarGroup>> getBarGroups();

    @GET("api/get-bar-articles")
    Call<List<BarArticle>> getBarArticles(@Query("id") int id);
}
