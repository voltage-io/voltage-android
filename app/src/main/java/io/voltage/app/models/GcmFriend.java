package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class GcmFriend extends GcmPayload {
    private interface Fields extends GcmPayload.Fields {
        String NAME = "name";
        String REG_ID = "reg_id";
    }

    @SerializedName(Fields.NAME)
    private String mName;

    @SerializedName(Fields.REG_ID)
    private String mRegId;

    public GcmFriend(final Bundle extras) {
        mName = extras.getString(Fields.NAME);
        mRegId = extras.getString(Fields.REG_ID);
    }

    public GcmFriend(final String name, final String regId) {
        mRegId = regId;
        mName = name;
        setEnumType(Type.FRIEND_ADDED);
    }

    public String getName() {
        return mName;
    }

    public String getRegId() {
        return mRegId;
    }
}
