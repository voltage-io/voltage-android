package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class GcmSyncStart extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String COUNT = "count";
    }

    @SerializedName(Fields.COUNT)
    private String mCount;

    public GcmSyncStart(final Bundle extras) {
        super(extras);
        mCount = extras.getString(Fields.COUNT);
    }

    public GcmSyncStart(final String threadId, final String senderId, final int count) {
        super(threadId, senderId);
        mCount = String.valueOf(count);
        setEnumType(Type.SYNC_START);
    }

    public int getCount() {
        return Integer.valueOf(mCount);
    }
}
