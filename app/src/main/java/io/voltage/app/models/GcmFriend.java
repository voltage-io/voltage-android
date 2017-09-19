package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmFriend extends GcmPayload {
    private interface Fields extends GcmPayload.Fields {
        String NAME = "name";
        String REG_ID = "reg_id";
    }

    @SerializedName(Fields.NAME)
    private String mName;

    @SerializedName(Fields.REG_ID)
    private String mRegId;

    public GcmFriend(final Map<String, String> data) {
        mName = data.get(Fields.NAME);
        mRegId = data.get(Fields.REG_ID);
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
