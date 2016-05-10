package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class GcmChecksum extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String CHECKSUM = "checksum";
    }

    @SerializedName(Fields.CHECKSUM)
    private String mChecksum;

    public GcmChecksum(final Bundle extras) {
        super(extras);
        mChecksum = extras.getString(Fields.CHECKSUM);
    }

    public GcmChecksum(final String threadId, final String senderId, final String checksum) {
        super(threadId, senderId);
        mChecksum = checksum;
        setEnumType(Type.CHECKSUM);
    }

    public String getChecksum() {
        return mChecksum;
    }
}
