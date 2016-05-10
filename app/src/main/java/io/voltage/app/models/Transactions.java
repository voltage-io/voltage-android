package io.voltage.app.models;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.TransactionView;

public class Transactions {

    @ColumnName(TransactionView.Columns.THREAD_ID)
    private String mThreadId;

    @ColumnName(TransactionView.Columns.THREAD_NAME)
    private String mThreadName;

	@ColumnName(TransactionView.Columns.MSG_UUIDS)
	private String mMsgUuids;

    @ColumnName(TransactionView.Columns.COUNT)
    private int mCount;

    public Transactions() {}

    public String getThreadId() {
        return mThreadId;
    }

    public String getThreadName() {
        return mThreadName;
    }

    public String getMsgUuids() {
        return mMsgUuids;
    }

    public int getCount() {
        return mCount;
    }

    public List<String> getMsgUuidsList() {
        if (!TextUtils.isEmpty(mMsgUuids)) {
            return new ArrayList<String>(Arrays.asList(mMsgUuids.split(",")));
        } else {
            return null;
        }
    }
}