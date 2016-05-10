package io.voltage.app.operations;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

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

    protected final DatabaseHelper mDatabaseHelper = new DatabaseHelper();

    public GcmOperation(Uri uri) {
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

        mDatabaseHelper.updateRegistrationIds(context, regIds, gcmResponse);

        if (!gcmResponse.isSuccess()) {
            throw new RuntimeException();
        }
    }

}
