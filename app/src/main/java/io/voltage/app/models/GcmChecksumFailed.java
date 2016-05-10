package io.voltage.app.models;

import android.os.Bundle;

public class GcmChecksumFailed extends GcmSync {
    private interface Fields extends GcmSync.Fields {
    }

    public GcmChecksumFailed(final Bundle extras) {
        super(extras);
    }

    public GcmChecksumFailed(final String threadId, final String senderId) {
        super(threadId, senderId);
        setEnumType(Type.CHECKSUM_FAILED);
    }
}
