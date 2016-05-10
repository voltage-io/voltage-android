package io.voltage.app.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GcmRequest {
    private interface Fields {
        String REGISTRATION_IDS = "registration_ids";
        String COLLAPSE_KEY = "collapse_key";
        String DELAY_WHILE_IDLE = "delay_while_idle";
        String DATA = "data";
    }

    @SerializedName(Fields.REGISTRATION_IDS)
    private List<String> mRegistrationIds;

    @SerializedName(Fields.COLLAPSE_KEY)
    private String mCollapseKey;

    @SerializedName(Fields.DELAY_WHILE_IDLE)
    private boolean mDelayWhileIdle = false;

    @SerializedName(Fields.DATA)
    private GcmPayload mData;

    public GcmRequest(final GcmPayload data, final List<String> regIds) {
        mData = data;
        mRegistrationIds = regIds;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
