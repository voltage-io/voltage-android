package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GcmSyncMessage extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String MESSAGE = "message";
    }

    @SerializedName(Fields.MESSAGE)
    private GcmMessage mMessage;

    public GcmSyncMessage(final Bundle extras) {
        super(extras);
        final String string = extras.getString(Fields.MESSAGE);
        mMessage = new Gson().fromJson(string, GcmMessage.class);
        setType(extras.getString(Fields.TYPE));
    }

    public GcmSyncMessage(final String threadId, final String senderId, final GcmMessage gcmMessage) {
        super(threadId, senderId);
        mMessage = gcmMessage;
        setEnumType(Type.SYNC_MESSAGE);
    }

    public String getTimestamp() {
        return mMessage != null ? mMessage.getTimestamp() : null;
    }

    public Bundle getBundle() {
        return mMessage != null ? mMessage.getBundle() : null;
    }
}
