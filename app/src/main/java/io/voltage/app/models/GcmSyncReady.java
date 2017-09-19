package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmSyncReady extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String COUNT = "count";
    }

    @SerializedName(Fields.COUNT)
    private String mCount;

    public GcmSyncReady(final Map<String, String> data) {
        super(data);
        mCount = data.get(Fields.COUNT);
    }

    public GcmSyncReady(final String threadId, final String senderId, final int count) {
        super(threadId, senderId);
        mCount = String.valueOf(count);
        setEnumType(Type.SYNC_READY);
    }

    public int getCount() {
        return Integer.valueOf(mCount);
    }

}
