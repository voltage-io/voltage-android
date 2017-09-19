package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmSyncStart extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String COUNT = "count";
    }

    @SerializedName(Fields.COUNT)
    private String mCount;

    public GcmSyncStart(final Map<String, String> data) {
        super(data);
        mCount = data.get(Fields.COUNT);
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
