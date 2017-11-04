package io.voltage.app.models;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class User {

    @ColumnName(UserTable.Columns.NAME)
    private String mName;

    @ColumnName(UserTable.Columns.REG_ID)
    private String mRegId;

    @ColumnName(UserTable.Columns.PUBLIC_KEY)
    private String mPublicKey;

    public User() {}

    public User(final GcmFriendResponse gcmFriendResponse) {
        mRegId = gcmFriendResponse.getRegId();
        mPublicKey = gcmFriendResponse.getPublicKey();
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
