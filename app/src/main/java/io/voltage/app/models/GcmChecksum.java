package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmChecksum extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String CHECKSUM = "checksum";
    }

    @SerializedName(Fields.CHECKSUM)
    private String mChecksum;

    public GcmChecksum(final Map<String, String> data) {
        super(data);
        mChecksum = data.get(Fields.CHECKSUM);
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
