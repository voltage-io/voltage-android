package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.RegistrationTable;

public class Registration {

    protected interface Fields {
        String REG_ID = "reg_id";
        String LOOKUP = "lookup";
    }

    @SerializedName(Fields.REG_ID)
    @ColumnName(RegistrationTable.Columns.REG_ID)
    private String mRegId;

    @SerializedName(Fields.LOOKUP)
    @ColumnName(RegistrationTable.Columns.LOOKUP)
    private String mLookup;

    public Registration() {}

    public Registration(final String regId) {
        mRegId = regId;
    }

    public String getRegId() {
        return mRegId;
    }

    public String getLookup() {
        return mLookup;
    }
}
