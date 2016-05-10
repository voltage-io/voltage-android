package io.voltage.app.models;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.ParticipantView;

public class Participants {

    @ColumnName(ParticipantView.Columns.THREAD_ID)
    private String mThreadId;

    @ColumnName(ParticipantView.Columns.THREAD_NAME)
    private String mThreadName;

    @ColumnName(ParticipantView.Columns.USER_IDS)
    private String mUserIds;

	@ColumnName(ParticipantView.Columns.USER_NAMES)
	private String mUserNames;

    public Participants() {}

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
        if (!TextUtils.isEmpty(mUserIds)) {
            return new ArrayList<String>(Arrays.asList(mUserIds.split(",")));
        } else {
            return null;
        }
    }

    public List<String> getUserNamesList() {
        if (!TextUtils.isEmpty(mUserNames)) {
            return new ArrayList<String>(Arrays.asList(mUserNames.split(",")));
        } else {
            return null;
        }
    }
}