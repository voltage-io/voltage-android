package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.helpers.NotificationHelper;
import io.voltage.app.models.GcmMessage;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.Message;
import io.voltage.app.models.Recipients;

public class MessageOperation extends TaskOperation<List<GcmResponse>> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();
    private final NotificationHelper mNotificationHelper = new NotificationHelper.Default();

    private final String mMsgUuid;

    public MessageOperation(final String msgUuid) {
        super(VoltageContentProvider.Uris.MESSAGES);
        mMsgUuid = msgUuid;
    }

    private MessageOperation(final Parcel in) {
        super(in);
        mMsgUuid = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mMsgUuid);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("MESSAGE:" + mMsgUuid);
    }

    @Override
    public List<GcmResponse> onExecute(final Context context) throws Exception {

        final Message message = mDatabaseHelper.getMessage(context, mMsgUuid);
        final Recipients recipients = mDatabaseHelper.getRecipients(context, mMsgUuid);

        final GcmMessage gcmMessage = new GcmMessage(message);

        if (gcmMessage.getType().equals("MESSAGE")) {
            return mMessagingHelper.aesEncryptAndSend(context, recipients, gcmMessage);
        } else {
            return mMessagingHelper.rsaEncryptAndSend(context, recipients, gcmMessage);
        }
    }

    @Override
    public void onComplete(final Context context, final Results results) {
        if (results.hasFailedTasks()) {
            mDatabaseHelper.updateMessageState(context, mMsgUuid, MessageTable.State.ERROR);
        } else {
            mDatabaseHelper.updateMessageState(context, mMsgUuid, MessageTable.State.DEFAULT);
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public MessageOperation createFromParcel(final Parcel in) {
            return new MessageOperation(in);
        }

        @Override
        public MessageOperation[] newArray(final int size) {
            return new MessageOperation[size];
        }
    };
}
