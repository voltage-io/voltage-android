package io.voltage.app.operations;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;

import java.util.List;

import io.pivotal.arca.service.SimpleOperation;
import io.voltage.app.application.VoltageApi;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmRequest;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.utils.Logger;

public abstract class GcmOperation extends SimpleOperation {

    protected final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

    public GcmOperation(final Uri uri) {
        super(uri);
    }

    protected GcmOperation(final Parcel in) {
        super(in);
    }

    protected void sendGcmRequest(final Context context, final List<String> regIds, final GcmPayload gcmPayload) throws Exception {

        if (regIds == null) {
            throw new IllegalArgumentException("Message not being sent to any users.");
        }

        if (regIds.size() > 1) {
            regIds.remove(VoltagePreferences.getRegId(context));
        }

        final GcmRequest gcmRequest = new GcmRequest(gcmPayload, regIds);
        final GcmResponse gcmResponse = VoltageApi.sendMessage(gcmRequest);

        Logger.v("Response: " + gcmResponse);

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
    }

}
