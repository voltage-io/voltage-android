package io.voltage.app.application;

import java.io.IOException;

import io.voltage.app.models.GcmRequest;
import io.voltage.app.models.GcmResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class VoltageApi {
    private interface Service {
        String GCM_SERVER_URL = "https://android.googleapis.com";

        @POST("/gcm/send")

        @Headers({
            "Authorization: key=" + VoltageProperties.GCM_SERVER_KEY,
            "Content-Type: application/json"
        })
        Call<GcmResponse> sendMessage(@Body final GcmRequest request);
    }

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(Service.GCM_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static GcmResponse sendMessage(final GcmRequest request) throws IOException {
        final Service service = RETROFIT.create(Service.class);
        final Call<GcmResponse> call = service.sendMessage(request);
        final Response<GcmResponse> response = call.execute();
        return response.body();
    }
}
