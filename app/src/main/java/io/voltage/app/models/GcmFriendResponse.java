package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmFriendResponse extends GcmPayload {
    private interface Fields extends GcmPayload.Fields {
        String REG_ID = "reg_id";
        String PUBLIC_KEY = "public_key";
    }

    @SerializedName(Fields.REG_ID)
    private String mRegId;

    @SerializedName(Fields.PUBLIC_KEY)
    private String mPublicKey;

    public GcmFriendResponse(final Map<String, String> data) {
        mRegId = data.get(Fields.REG_ID);
        mPublicKey = data.get(Fields.PUBLIC_KEY);
    }

    public GcmFriendResponse(final String regId, final String publicKey) {
        mRegId = regId;
        mPublicKey = publicKey;
        setEnumType(Type.FRIEND_RESPONSE);
    }

    public String getRegId() {
        return mRegId;
    }

    public String getPublicKey() {
        return mPublicKey;
    }
}
