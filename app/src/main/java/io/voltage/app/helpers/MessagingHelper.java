package io.voltage.app.helpers;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import io.pivotal.arca.utils.ArrayUtils;
import io.voltage.app.application.VoltageApi;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmRequest;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.utils.Logger;

public interface MessagingHelper {

    GcmResponse sendGcmRequest(final Context context, final List<String> regIds, final GcmPayload gcmPayload) throws Exception;

    class Default implements MessagingHelper {

        protected final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

        public GcmResponse sendGcmRequest(final Context context, final List<String> regIds, final GcmPayload gcmPayload) throws Exception {

            if (ArrayUtils.isEmpty(regIds)) {
                throw new IllegalArgumentException("Message not being sent to any users.");
            }

            if (regIds.size() > 1) {
                regIds.remove(VoltagePreferences.getRegId(context));
            }

            final GcmRequest gcmRequest = new GcmRequest(gcmPayload, regIds);
            final GcmResponse gcmResponse = VoltageApi.sendMessage(gcmRequest);

            Logger.v("Response: " + gcmResponse);

            return handleGcmResponse(context, regIds, gcmResponse);
        }

        private GcmResponse handleGcmResponse(final Context context, final List<String> regIds, final GcmResponse gcmResponse) throws Exception {

            final List<GcmResponse.Result> results = gcmResponse.getResults();
            for (int i = 0; i < results.size(); i++) {

                final GcmResponse.Result result = results.get(i);
                final String newRegId = result.getRegistrationId();

                if (!TextUtils.isEmpty(newRegId)) {
                    final String oldRegId = regIds.get(i);
                    mDatabaseHelper.updateRegistration(context, newRegId, oldRegId);

                    Logger.v("New registration id: " + newRegId);
                    Logger.v("Old registration id: " + oldRegId);
                }
            }

            if (!gcmResponse.hasSuccess()) throw new RuntimeException();

            return gcmResponse;
        }
    }
}
