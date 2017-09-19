package io.voltage.app.operations;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import io.pivotal.arca.dispatcher.ErrorBroadcaster;
import io.pivotal.arca.provider.DataUtils;
import io.pivotal.arca.service.ServiceError;
import io.pivotal.arca.service.SimpleOperation;
import io.voltage.app.application.VoltageApi;
import io.voltage.app.application.VoltageContentProvider;

public class RegistrationPostOperation extends SimpleOperation {

    private final String mRegId;

    public RegistrationPostOperation(final String regId) {
        super(VoltageContentProvider.Uris.REGISTRATIONS);
        mRegId = regId;
    }

    private RegistrationPostOperation(final Parcel in) {
        super(in);
        mRegId = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mRegId);
    }

    @Override
    public ContentValues[] onExecute(final Context context) throws Exception {
        return new ContentValues[]{DataUtils.getContentValues(VoltageApi.postRegistration(mRegId))};
    }

    @Override
    public void onPostExecute(final Context context, final ContentValues[] values) throws Exception {
        context.getContentResolver().insert(getUri(), values[0]);
        context.getContentResolver().notifyChange(VoltageContentProvider.Uris.REGISTRATIONS, null);
    }

    @Override
    public void onComplete(final Context context, final Results results) {
        if (results.hasFailedTasks()) {
            final ServiceError error = results.getFailedTasks().get(0).getError();
            ErrorBroadcaster.broadcast(context, getUri(), error.getCode(), error.getMessage());
        }
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public RegistrationPostOperation createFromParcel(final Parcel in) {
            return new RegistrationPostOperation(in);
        }

        @Override
        public RegistrationPostOperation[] newArray(final int size) {
            return new RegistrationPostOperation[size];
        }
    };
}
