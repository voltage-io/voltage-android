package io.voltage.app.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class GcmPayload {
    protected interface Fields {
        String TYPE = "type";
    }

    public enum Type {
        MESSAGE,
        RECEIPT,
        FRIEND_REQUEST,
        FRIEND_RESPONSE,
        THREAD_CREATED,
        THREAD_RENAMED,
        THREAD_PROGRESS,
        THREAD_KEY_ROTATED,
        CHECKSUM,
        CHECKSUM_FAILED,
        SYNC_REQUEST,
        SYNC_START,
        SYNC_READY,
        SYNC_MESSAGE,
        SYNC_END,
        USER_ADDED,
        USER_REMOVED,
        USER_LEFT,
        UNKNOWN
    }

    @SerializedName(Fields.TYPE)
    private String mType;


    public void setEnumType(final Type type) {
        mType = type != null ? type.name() : null;
    }

    public void setType(final String type) {
        mType = type;
    }

    public Type getEnumType() {
        return Type.valueOf(mType);
    }

    public String getType() {
        return mType;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Map<String, String> toMap() {
        final Map<String, String> map = new HashMap<>();
        map.put(Fields.TYPE, mType);
        return map;
    }

    public static Type getType(final Map<String, String> data) {
        try {
            final String type = data.get(Fields.TYPE);
            return Type.valueOf(type);
        } catch (final Exception e) {
            return Type.UNKNOWN;
        }
    }
}
