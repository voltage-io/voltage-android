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

    @ColumnName(RecipientView.Columns.THREAD_KEY)
    private String mThreadKey;

    @ColumnName(RecipientView.Columns.USER_IDS)
    private String mUserIds;

	@ColumnName(RecipientView.Columns.USER_NAMES)
	private String mUserNames;

    @ColumnName(RecipientView.Columns.USER_PUBLIC_KEYS)
    private String mUserPublicKeys;

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

    public String getThreadKey() {
        return mThreadKey;
    }

    public String getUserIds() {
        return mUserIds;
    }

    public String getUserNames() {
        return mUserNames;
    }

    public String getUserPublicKeys() {
        return mUserPublicKeys;
    }

    public List<String> getUserIdsList() {
        if (mUserIds != null) {
            return new ArrayList<String>(Arrays.asList(mUserIds.split(",")));
        } else {
            return new ArrayList<String>();
        }
    }

    public List<String> getUserNamesList() {
        if (mUserNames != null) {
            return new ArrayList<String>(Arrays.asList(mUserNames.split(",")));
        } else {
            return new ArrayList<String>();
        }
    }

    public List<String> getUserPublicKeysList() {
        if (mUserPublicKeys != null) {
            return new ArrayList<String>(Arrays.asList(mUserPublicKeys.split(",")));
        } else {
            return new ArrayList<String>();
        }
    }
}