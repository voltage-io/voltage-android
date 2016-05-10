package io.voltage.app.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GcmResponse {
    private interface Fields {
        String MULTICAST_ID = "multicast_id";
        String SUCCESS = "success";
        String FAILURE = "failure";
        String CANONICAL_IDS = "canonical_ids";
        String RESULTS = "results";
    }

    @SerializedName(Fields.MULTICAST_ID)
    private String mMulticastId;

    @SerializedName(Fields.SUCCESS)
    private String mSuccess;

    @SerializedName(Fields.FAILURE)
    private String mFailure;

    @SerializedName(Fields.CANONICAL_IDS)
    private String mCanonicalIds;

    @SerializedName(Fields.RESULTS)
    private List<Result> mResults;

    public boolean isSuccess() {
        return mSuccess != null && Integer.parseInt(mSuccess) > 0;
    }

    public List<Result> getResults() {
        return mResults;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static class Result {
        public interface Fields {
            String MESSAGE_ID = "message_id";
            String REGISTRATION_ID = "registration_id";
            String ERROR = "error";
        }

        @SerializedName(Fields.MESSAGE_ID)
        private String mMessageId;

        @SerializedName(Fields.REGISTRATION_ID)
        private String mRegistrationId;

        @SerializedName(Fields.ERROR)
        private String mError;

        public String getRegistrationId() {
            return mRegistrationId;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
