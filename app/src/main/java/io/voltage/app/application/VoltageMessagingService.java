package io.voltage.app.application;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pivotal.arca.service.OperationService;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.NotificationHelper;
import io.voltage.app.models.GcmChecksum;
import io.voltage.app.models.GcmChecksumFailed;
import io.voltage.app.models.GcmFriend;
import io.voltage.app.models.GcmFriendRequest;
import io.voltage.app.models.GcmMessage;
import io.voltage.app.models.GcmMessageState;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmSyncMessage;
import io.voltage.app.models.GcmSyncReady;
import io.voltage.app.models.GcmSyncRequest;
import io.voltage.app.models.GcmSyncStart;
import io.voltage.app.models.Message;
import io.voltage.app.models.MessageState;
import io.voltage.app.models.Thread;
import io.voltage.app.models.ThreadUser;
import io.voltage.app.models.User;
import io.voltage.app.operations.ChecksumReplyOperation;
import io.voltage.app.operations.MessageStateOperation;
import io.voltage.app.operations.SyncMessagesOperation;
import io.voltage.app.operations.SyncReadyOperation;
import io.voltage.app.operations.SyncRequestOperation;
import io.voltage.app.operations.SyncStartOperation;
import io.voltage.app.operations.UserOperation;
import io.voltage.app.operations.UserRequestOperation;
import io.voltage.app.utils.CryptoUtils;
import io.voltage.app.utils.Logger;

public class VoltageMessagingService extends FirebaseMessagingService {

    private Messenger mMessenger;

    @Override
    public void onCreate() {
        super.onCreate();

        final DatabaseHelper.Default databaseHelper = new DatabaseHelper.Default();
        final NotificationHelper.Default notificationHelper = new NotificationHelper.Default();
        mMessenger = new Messenger(databaseHelper, notificationHelper);
    }

    @Override
    public void onMessageReceived(final RemoteMessage message) {
        super.onMessageReceived(message);

        try {
            mMessenger.handleMessage(this, message.getData());
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    private static class Messenger {

        private static final Map<String, GcmSyncMessageList> MESSAGES = new HashMap<>();

        private final DatabaseHelper mDatabaseHelper;
        private final NotificationHelper mNotificationHelper;

        public Messenger(final DatabaseHelper databaseHelper, final NotificationHelper notificationHelper) {
            mDatabaseHelper = databaseHelper;
            mNotificationHelper = notificationHelper;
        }

        private void handleMessage(final Context context, final Map<String, String> data) {

            final GcmPayload.Type type = GcmPayload.getType(data);

            Logger.v("MESSAGE [" + type + "] " + data);

            switch (type) {
                case MESSAGE:
                    handleMessage(context, new GcmMessage(data));
                    break;

                case THREAD_CREATED:
                    handleThreadCreated(context, new GcmMessage(data));
                    break;

                case THREAD_RENAMED:
                    handleThreadRenamed(context, new GcmMessage(data));
                    break;

                case THREAD_PROGRESS:
                    handleThreadProgress(context, new GcmMessage(data));
                    break;

                case USER_ADDED:
                    handleUserAdded(context, new GcmMessage(data));
                    break;

                case USER_REMOVED:
                    handleUserRemoved(context, new GcmMessage(data));
                    break;

                case USER_LEFT:
                    handleUserLeft(context, new GcmMessage(data));
                    break;

                case RECEIPT:
                    handleReceipt(context, new GcmMessageState(data));
                    break;

                case FRIEND_ADDED:
                    handleFriendAdded(context, new GcmFriend(data));
                    break;

                case FRIEND_REQUEST:
                    handleFriendRequest(context, new GcmFriendRequest(data));
                    break;

                case CHECKSUM:
                    handleChecksum(context, new GcmChecksum(data));
                    break;

                case CHECKSUM_FAILED:
                    handleChecksumFailed(context, new GcmChecksumFailed(data));
                    break;

                case SYNC_REQUEST:
                    handleSyncRequest(context, new GcmSyncRequest(data));
                    break;

                case SYNC_START:
                    handleSyncStart(context, new GcmSyncStart(data));
                    break;

                case SYNC_READY:
                    handleSyncReady(context, new GcmSyncReady(data));
                    break;

                case SYNC_MESSAGE:
                    handleSyncMessage(context, new GcmSyncMessage(data));
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

            mDatabaseHelper.updateMessageState(context, state);
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

        private void handleChecksum(final Context context, final GcmChecksum gcmChecksum) {
            final String threadId = gcmChecksum.getThreadId();
            final String senderId = gcmChecksum.getSenderId();
            final String checksum = gcmChecksum.getChecksum();

            OperationService.start(context, new ChecksumReplyOperation(threadId, senderId, checksum));
        }

        private void handleChecksumFailed(final Context context, final GcmChecksumFailed gcmChecksumFailed) {
            final String threadId = gcmChecksumFailed.getThreadId();
            final String senderId = gcmChecksumFailed.getSenderId();

            OperationService.start(context, new SyncRequestOperation(threadId, senderId));
        }

        private void handleSyncRequest(final Context context, final GcmSyncRequest gcmSyncRequest) {
            final String threadId = gcmSyncRequest.getThreadId();
            final String senderId = gcmSyncRequest.getSenderId();
            final String msgUuid = gcmSyncRequest.getMsgUuid();
            final int msgIndex = gcmSyncRequest.getMsgIndex();

            OperationService.start(context, new SyncStartOperation(threadId, senderId, msgUuid, msgIndex));
        }

        private void handleSyncStart(final Context context, final GcmSyncStart gcmSyncStart) {
            final String threadId = gcmSyncStart.getThreadId();
            final String senderId = gcmSyncStart.getSenderId();
            final int count = gcmSyncStart.getCount();
            final String lookup = threadId + senderId;

            MESSAGES.put(lookup, new GcmSyncMessageList(count));

            OperationService.start(context, new SyncReadyOperation(threadId, senderId, count));
        }

        private void handleSyncReady(final Context context, final GcmSyncReady gcmSyncReady) {
            final String threadId = gcmSyncReady.getThreadId();
            final String senderId = gcmSyncReady.getSenderId();
            final int count = gcmSyncReady.getCount();

            OperationService.start(context, new SyncMessagesOperation(threadId, senderId, count));
        }

        private void handleSyncMessage(final Context context, final GcmSyncMessage gcmSyncMessage) {
            final String threadId = gcmSyncMessage.getThreadId();
            final String senderId = gcmSyncMessage.getSenderId();
            final String lookup = threadId + senderId;

            final GcmSyncMessageList list = MESSAGES.get(lookup);

            if (list != null) {
                list.add(gcmSyncMessage);

                if (list.hasAllMessages()) {
                    handleLastMessage(context, lookup);
                }
            }
        }

        private void handleLastMessage(final Context context, final String lookup) {
            final List<GcmSyncMessage> messages = MESSAGES.remove(lookup);

            Collections.sort(messages, new GcmSyncMessageComparator());

            for (final GcmSyncMessage message : messages) {
                handleMessage(context, message.toMap());
            }
        }

        private static class GcmSyncMessageComparator implements Comparator<GcmSyncMessage> {

            @Override
            public int compare(final GcmSyncMessage left, final GcmSyncMessage right) {
                final String leftTimestamp = left.getTimestamp();
                final String rightTimestamp = right.getTimestamp();

                return leftTimestamp.compareTo(rightTimestamp);
            }
        }

        private static class GcmSyncMessageList extends ArrayList<GcmSyncMessage> {

            private int mCapacity;

            public GcmSyncMessageList(final int capacity) {
                super(capacity);
                mCapacity = capacity;
            }

            public boolean hasAllMessages() {
                return size() == mCapacity;
            }
        }

    }
}
