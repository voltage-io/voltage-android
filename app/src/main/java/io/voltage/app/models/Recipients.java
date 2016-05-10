package io.voltage.app.models;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.RecipientView;

public class Recipients {

    @ColumnName(RecipientView.Columns.MSG_UUID)
    private String mMsgUuid;

    @ColumnName(RecipientView.Columns.THREAD_ID)
    private String mThreadId;

    @ColumnName(RecipientView.Columns.THREAD_NAME)
    private String mThreadName;

    @ColumnName(RecipientView.Columns.USER_IDS)
    private String mUserIds;

	@ColumnName(RecipientView.Columns.USER_NAMES)
	private String mUserNames;

    public Recipients() {}

    public String getMsgUuid() {
        return mMsgUuid;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public String getThreadName() {
        return mThreadName;
    }

    public String getUserIds() {
        return mUserIds;
    }

    public String getUserNames() {
        return mUserNames;
    }

    public List<String> getUserIdsList() {
        if (mUserIds != null) {
            final List<String> list = Arrays.asList(mUserIds.split(","));
            return new ArrayList<String>(list);
        } else {
            return null;
        }
    }

    public List<String> getUserNamesList() {
        if (mUserNames != null) {
            final List<String> list = Arrays.asList(mUserNames.split(","));
            return new ArrayList<String>(list);
        } else {
            return null;
        }
    }
}