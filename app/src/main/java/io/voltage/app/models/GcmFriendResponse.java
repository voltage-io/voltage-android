package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmFriendResponse extends GcmPayload {
    private interface Fields extends GcmPayload.Fields {
        String NAME = "name";
        String REG_ID = "reg_id";
        String PUBLIC_KEY = "public_key";
    }

    @SerializedName(Fields.NAME)
    private String mName;

    @SerializedName(Fields.REG_ID)
    private String mRegId;

    @SerializedName(Fields.PUBLIC_KEY)
    private String mPublicKey;

    public GcmFriendResponse(final Map<String, String> data) {
        mName = data.get(Fields.NAME);
        mRegId = data.get(Fields.REG_ID);
        mPublicKey = data.get(Fields.PUBLIC_KEY);
    }

    public GcmFriendResponse(final String name, final String regId, final String publicKey) {
        mRegId = regId;
        mName = name;
        mPublicKey = publicKey;
        setEnumType(Type.FRIEND_RESPONSE);
    }

    public String getName() {
        return mName;
    }

    public String getRegId() {
        return mRegId;
    }

    public String getPublicKey() {
        return mPublicKey;
    }
}
