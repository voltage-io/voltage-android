package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GcmPayload {
    protected interface Fields {
        String TYPE = "type";
    }

    public enum Type {
        MESSAGE,
        RECEIPT,
        FRIEND_ADDED,
        FRIEND_REQUEST,
        THREAD_CREATED,
        THREAD_RENAMED,
        THREAD_PROGRESS,
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

    public Bundle getBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString(Fields.TYPE, mType);
        return bundle;
    }

    public static Type getType(final Bundle extras) {
        try {
            final String type = extras.getString(Fields.TYPE);
            return Type.valueOf(type);
        } catch (final Exception e) {
            return Type.UNKNOWN;
        }
    }
}
