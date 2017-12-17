package io.voltage.app.application;

import java.io.IOException;
import java.util.List;

import io.voltage.app.models.GcmRequest;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.ImageResponse;
import io.voltage.app.models.Registration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class VoltageApi {
    private interface GcmService {
        String SERVER_URL = "https://fcm.googleapis.com";

        @POST("/fcm/send")
        @Headers({
            "Content-Type: application/json",
            "Authorization: key=" + VoltageProperties.GCM_SERVER_KEY
        })
        Call<GcmResponse> sendMessage(@Body final GcmRequest request);
    }

    private interface VoltageService {
        String SERVER_URL = "https://voltage-api.cfapps.io";

        @POST("/v1/registrations")
        @Headers({"Authorization: bearer " + VoltageProperties.SERVER_KEY})
        Call<Registration> postRegistration(@Body final Registration registration);

        @DELETE("/v1/registrations")
        @Headers({"Authorization: bearer " + VoltageProperties.SERVER_KEY})
        Call<Registration> deleteRegistration(@Query("reg_id") final String regId);

        @GET("/v1/registrations")
        @Headers({"Authorization: bearer " + VoltageProperties.SERVER_KEY})
        Call<List<Registration>> getRegistrations(@Query("search") final String search);
    }

    private interface GiphyService {
        String SERVER_URL = "https://api.giphy.com";

        @GET("/v1/gifs/search")
        Call<ImageResponse> getImages(@Query("api_key") final String key, @Query("q") final String query);
    }

    private static final GcmService GCM_SERVICE = new Retrofit.Builder()
            .baseUrl(GcmService.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createLoggingClient())
            .build().create(GcmService.class);

    private static final VoltageService VOLTAGE_SERVICE = new Retrofit.Builder()
            .baseUrl(VoltageService.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createLoggingClient())
            .build().create(VoltageService.class);

    private static final GiphyService GIPHY_SERVICE = new Retrofit.Builder()
            .baseUrl(GiphyService.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createLoggingClient())
            .build().create(GiphyService.class);

    private static OkHttpClient createLoggingClient() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(logging).build();
    }


    public static GcmResponse sendMessage(final GcmRequest request) throws IOException {
        return GCM_SERVICE.sendMessage(request).execute().body();
    }

    public static Registration postRegistration(final String regId) throws IOException {
        return VOLTAGE_SERVICE.postRegistration(new Registration(regId)).execute().body();
    }

    public static Registration deleteRegistration(final String regId) throws IOException {
        return VOLTAGE_SERVICE.deleteRegistration(regId).execute().body();
    }

    public static List<Registration> getRegistrations(final String search) throws IOException {
        return VOLTAGE_SERVICE.getRegistrations(search).execute().body();
    }

    public static ImageResponse getImages(final String search) throws IOException {
        return GIPHY_SERVICE.getImages(VoltageProperties.GIPHY_KEY, search).execute().body();
    }
}
