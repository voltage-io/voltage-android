package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmMessageState;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.Message;
import io.voltage.app.models.MessageState;

public class MessageStateOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

    private final String mMsgUuid;
    private final int mState;

    public MessageStateOperation(final String msgUuid, final int state) {
        super(VoltageContentProvider.Uris.MESSAGES);
        mMsgUuid = msgUuid;
        mState = state;
    }

    private MessageStateOperation(final Parcel in) {
        super(in);
        mMsgUuid = in.readString();
        mState = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mMsgUuid);
        dest.writeInt(mState);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("MESSAGE_STATE:" + mMsgUuid);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final Message message = mDatabaseHelper.getMessage(context, mMsgUuid);
        final List<String> regIds = Collections.singletonList(message.getSenderId());

        final MessageState messageState = new MessageState(mMsgUuid, mState);
        final GcmPayload gcmPayload = new GcmMessageState(messageState);

        return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public MessageStateOperation createFromParcel(final Parcel in) {
            return new MessageStateOperation(in);
        }

        @Override
        public MessageStateOperation[] newArray(final int size) {
            return new MessageStateOperation[size];
        }
    };
}
