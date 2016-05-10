package io.voltage.app.operations;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

import java.util.List;

import io.voltage.app.models.GcmPayload;

public abstract class GcmPayloadOperation extends GcmOperation {

    public GcmPayloadOperation(Uri uri) {
        super(uri);
    }

    protected GcmPayloadOperation(final Parcel in) {
        super(in);
    }

    @Override
    public ContentValues[] onExecute(final Context context) throws Exception {

        final List<String> regIds = onCreateRecipientList(context);
        final GcmPayload gcmPayload = onCreateGcmPayload(context);

        sendGcmRequest(context, regIds, gcmPayload);

        return null;
    }

    public abstract List<String> onCreateRecipientList(final Context context);
    public abstract GcmPayload onCreateGcmPayload(final Context context);

}
