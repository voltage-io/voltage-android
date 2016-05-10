package io.voltage.app.application;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import io.pivotal.arca.service.OperationService;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.NotificationHelper;
import io.voltage.app.models.GcmFriend;
import io.voltage.app.models.GcmFriendRequest;
import io.voltage.app.models.GcmMessage;
import io.voltage.app.models.GcmMessageState;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.Message;
import io.voltage.app.models.MessageState;
import io.voltage.app.models.Thread;
import io.voltage.app.models.ThreadUser;
import io.voltage.app.models.User;
import io.voltage.app.operations.MessageStateOperation;
import io.voltage.app.operations.UserOperation;
import io.voltage.app.operations.UserRequestOperation;
import io.voltage.app.utils.CryptoUtils;
import io.voltage.app.utils.Logger;

public class VoltageGcmService extends IntentService {

    private final Messenger mMessenger = new Messenger();

    public VoltageGcmService() {
        super(VoltageGcmService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        final String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            mMessenger.handleMessage(this, intent.getExtras());
        }

        VoltageGcmReceiver.completeWakefulIntent(intent);
    }

    public static class Messenger {

        private final DatabaseHelper mDatabaseHelper = new DatabaseHelper();
        private final NotificationHelper mNotificationHelper = new NotificationHelper();

        public void handleMessage(final Context context, final Bundle extras) {
            Logger.v(extras.toString());

            switch (GcmPayload.getType(extras)) {
                case MESSAGE:
                    handleMessage(context, new GcmMessage(extras));
                    break;

                case THREAD_CREATED:
                    handleThreadCreated(context, new GcmMessage(extras));
                    break;

                case THREAD_RENAMED:
                    handleThreadRenamed(context, new GcmMessage(extras));
                    break;

                case THREAD_PROGRESS:
                    handleThreadProgress(context, new GcmMessage(extras));
                    break;

                case USER_ADDED:
                    handleUserAdded(context, new GcmMessage(extras));
                    break;

                case USER_REMOVED:
                    handleUserRemoved(context, new GcmMessage(extras));
                    break;

                case USER_LEFT:
                    handleUserLeft(context, new GcmMessage(extras));
                    break;

                case RECEIPT:
                    handleReceipt(context, new GcmMessageState(extras));
                    break;

                case FRIEND_ADDED:
                    handleFriendAdded(context, new GcmFriend(extras));
                    break;

                case FRIEND_REQUEST:
                    handleFriendRequest(context, new GcmFriendRequest(extras));
                    break;
            }
        }

        private void handleMessage(final Context context, final GcmMessage gcmMessage) {
            final Thread thread = new Thread(gcmMessage);
            final ThreadUser threadUser = new ThreadUser(gcmMessage);
            final Message message = new Message(gcmMessage);
            message.setState(MessageTable.State.UNREAD);

            mDatabaseHelper.insertRecords(context, thread, threadUser, message);
            mNotificationHelper.addNewMessageNotification(context, message);

            final String msgUuid = message.getMsgUuid();
            final int state = MessageTable.State.RECEIPT;

            OperationService.start(context, new MessageStateOperation(msgUuid, state));
        }

        private void handleThreadCreated(final Context context, final GcmMessage gcmMessage) {
            handleMessage(context, gcmMessage);

            final String threadId = gcmMessage.getThreadId();
            final String threadName = gcmMessage.getMetadata();

            mDatabaseHelper.updateThread(context, threadId, threadName);
        }

        private void handleThreadRenamed(final Context context, final GcmMessage gcmMessage) {
            handleMessage(context, gcmMessage);

            final String threadId = gcmMessage.getThreadId();
            final String threadName = gcmMessage.getMetadata();

            mDatabaseHelper.updateThread(context, threadId, threadName);
        }

        private void handleThreadProgress(final Context context, final GcmMessage gcmMessage) {
            handleMessage(context, gcmMessage);

            final String threadId = gcmMessage.getThreadId();
            final String senderId = gcmMessage.getSenderId();

            final String msgUuid = CryptoUtils.checksum(threadId + ":" + senderId);

            Logger.v("UUID: " + msgUuid);
        }

        private void handleUserAdded(final Context context, final GcmMessage gcmMessage) {
            handleMessage(context, gcmMessage);

            final String senderId = gcmMessage.getSenderId();
            final String threadId = gcmMessage.getThreadId();
            final String userId = gcmMessage.getMetadata();

            if (!VoltagePreferences.getRegId(context).equals(userId)) {
                mDatabaseHelper.insertThreadUser(context, threadId, userId);

                if (VoltagePreferences.shouldAutoAddUsers(context)) {
                    OperationService.start(context, new UserRequestOperation(userId, senderId));
                }
            }
        }

        private void handleUserRemoved(final Context context, final GcmMessage gcmMessage) {
            handleMessage(context, gcmMessage);

            final String threadId = gcmMessage.getThreadId();
            final String userId = gcmMessage.getMetadata();

            if (!VoltagePreferences.getRegId(context).equals(userId)) {
                mDatabaseHelper.deleteThreadUser(context, threadId, userId);
            }
        }

        private void handleUserLeft(final Context context, final GcmMessage gcmMessage) {
            handleMessage(context, gcmMessage);

            final String threadId = gcmMessage.getThreadId();
            final String senderId = gcmMessage.getSenderId();

            mDatabaseHelper.deleteThreadUser(context, threadId, senderId);
        }

        private void handleReceipt(final Context context, final GcmMessageState gcmState) {
            final MessageState state = new MessageState(gcmState);

            mDatabaseHelper.updateState(context, state);
        }

        private void handleFriendAdded(final Context context, final GcmFriend gcmFriend) {
            final User user = new User(gcmFriend);

            mDatabaseHelper.insertUser(context, user);
            mNotificationHelper.addNewFriendNotification(context, user);
        }

        private void handleFriendRequest(final Context context, final GcmFriendRequest gcmRequest) {
            final String senderId = gcmRequest.getSenderId();
            final String regId = gcmRequest.getRegId();

            OperationService.start(context, new UserOperation(regId, senderId));
        }
    }
}
