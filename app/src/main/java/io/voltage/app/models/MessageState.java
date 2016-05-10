package io.voltage.app.models;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.MessageTable;

public class MessageState {

    @ColumnName(MessageTable.Columns.MSG_UUID)
    private String mMsgUuid;

    @ColumnName(MessageTable.Columns._STATE)
    private int mState;

    public MessageState() {}

    public MessageState(final GcmMessageState gcmState) {
        mMsgUuid = gcmState.getMsgUuid();
        mState = gcmState.getState();
    }

    public MessageState(final String msgUuid, final int state) {
        mMsgUuid = msgUuid;
        mState = state;
    }

    public String getMsgUuid() {
        return mMsgUuid;
    }

    public int getState() {
        return mState;
    }
}
