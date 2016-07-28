package bei.m3c.services;

import java.util.List;

import bei.m3c.models.Radio;
import retrofit2.Call;
import retrofit2.http.GET;

public interface M3SService {
    @GET("api/get-radios")
    Call<List<Radio>> listRadios();
}
