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

public class RegistrationDeleteOperation extends SimpleOperation {

    private final String mRegId;

    public RegistrationDeleteOperation(final String regId) {
        super(VoltageContentProvider.Uris.REGISTRATIONS);
        mRegId = regId;
    }

    private RegistrationDeleteOperation(final Parcel in) {
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
        return new ContentValues[]{DataUtils.getContentValues(VoltageApi.deleteRegistration(mRegId))};
    }

    @Override
    public void onPostExecute(final Context context, final ContentValues[] values) throws Exception {

        values[0].remove(VoltageContentProvider.RegistrationTable.Columns.LOOKUP);

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
        public RegistrationDeleteOperation createFromParcel(final Parcel in) {
            return new RegistrationDeleteOperation(in);
        }

        @Override
        public RegistrationDeleteOperation[] newArray(final int size) {
            return new RegistrationDeleteOperation[size];
        }
    };
}
