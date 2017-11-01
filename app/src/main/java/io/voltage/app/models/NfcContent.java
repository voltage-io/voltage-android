package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

public class NfcContent {
    public interface Fields {
        String REG_ID = "reg_id";
        String NAME = "name";
        String PUBLIC_KEY = "public_key";
    }

    @SerializedName(Fields.REG_ID)
    private final String mRegId;

    @SerializedName(Fields.NAME)
    private final String mName;

    @SerializedName(Fields.PUBLIC_KEY)
    private final String mPublicKey;

    public NfcContent(final String name, final String regId, final String publicKey) {
        mName = name;
        mRegId = regId;
        mPublicKey = publicKey;
    }

    public String getRegId() {
        return mRegId;
    }

    public String getName() {
        return mName;
    }

    public String getPublicKey() {
        return mPublicKey;
    }
}
