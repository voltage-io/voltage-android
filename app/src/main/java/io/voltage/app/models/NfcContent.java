package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

public class NfcContent {

    public interface Fields {
        String REG_ID = "reg_id";
        String NAME = "name";
    }

    @SerializedName(Fields.REG_ID)
    private final String mRegId;

    @SerializedName(Fields.NAME)
    private final String mName;

    public NfcContent(final String name, final String regId) {
        mName = name;
        mRegId = regId;
    }

    public String getRegId() {
        return mRegId;
    }

    public String getName() {
        return mName;
    }
}
