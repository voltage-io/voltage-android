package io.voltage.app.models;

import java.util.Map;

public class GcmChecksumFailed extends GcmSync {
    private interface Fields extends GcmSync.Fields {
    }

    public GcmChecksumFailed(final Map<String, String> data) {
        super(data);
    }

    public GcmChecksumFailed(final String threadId, final String senderId) {
        super(threadId, senderId);
        setEnumType(Type.CHECKSUM_FAILED);
    }
}
