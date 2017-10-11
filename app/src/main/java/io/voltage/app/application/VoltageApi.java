package io.voltage.app.application;

import java.io.IOException;
import java.util.List;

import io.voltage.app.models.GcmRequest;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.Registration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
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
        String SERVER_URL = "http://api.voltage.io";

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

    private static final GcmService GCM_SERVICE = new Retrofit.Builder()
            .baseUrl(GcmService.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createAuthClient())
            .build().create(GcmService.class);

    private static final VoltageService VOLTAGE_SERVICE = new Retrofit.Builder()
            .baseUrl(VoltageService.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createAuthClient())
            .build().create(VoltageService.class);

    private static OkHttpClient createAuthClient() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(logging).build();
    }


    public static GcmResponse sendMessage(final GcmRequest request) throws IOException {
        final Call<GcmResponse> call = GCM_SERVICE.sendMessage(request);
        final Response<GcmResponse> response = call.execute();
        return response.body();
    }

    public static Registration postRegistration(final String regId) throws IOException {
        final Call<Registration> call = VOLTAGE_SERVICE.postRegistration(new Registration(regId));
        final Response<Registration> response = call.execute();
        return response.body();
    }

    public static Registration deleteRegistration(final String regId) throws IOException {
        final Call<Registration> call = VOLTAGE_SERVICE.deleteRegistration(regId);
        final Response<Registration> response = call.execute();
        return response.body();
    }

    public static List<Registration> getRegistrations(final String search) throws IOException {
        final Call<List<Registration>> call = VOLTAGE_SERVICE.getRegistrations(search);
        final Response<List<Registration>> response = call.execute();
        return response.body();
    }
}
