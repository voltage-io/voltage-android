package io.voltage.app.application;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pivotal.arca.service.OperationService;
import io.voltage.app.models.GcmChecksum;
import io.voltage.app.models.GcmChecksumFailed;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmSyncMessage;
import io.voltage.app.models.GcmSyncReady;
import io.voltage.app.models.GcmSyncRequest;
import io.voltage.app.models.GcmSyncStart;
import io.voltage.app.operations.ChecksumFailedOperation;
import io.voltage.app.operations.SyncMessagesOperation;
import io.voltage.app.operations.SyncReadyOperation;
import io.voltage.app.operations.SyncRequestOperation;
import io.voltage.app.operations.SyncStartOperation;
import io.voltage.app.utils.Logger;


public class VoltageGcmSyncingService extends Service implements Handler.Callback {

    private final BackgroundHandler mBackgroundHandler = new BackgroundHandler(this);
    private final Map<String, GcmSyncMessageList> mMessages = new HashMap<String, GcmSyncMessageList>();
    private final VoltageGcmService.Messenger mMessenger = new VoltageGcmService.Messenger();

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        mBackgroundHandler.start(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mBackgroundHandler.destroy();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public boolean handleMessage(final Message msg) {
        handleMessage((Intent) msg.obj, msg.arg1);
        return true;
    }

    private void handleMessage(final Intent intent, final int startId) {
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        final String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            handleMessage(intent.getExtras(), startId);
        }

        VoltageGcmReceiver.completeWakefulIntent(intent);
    }

    private void handleMessage(final Bundle extras, final int startId) {
        Logger.v(extras.toString());

        switch (GcmPayload.getType(extras)) {
            case CHECKSUM:
                handleChecksum(new GcmChecksum(extras), startId);
                break;

            case CHECKSUM_FAILED:
                handleChecksumFailed(new GcmChecksumFailed(extras), startId);
                break;

            case SYNC_REQUEST:
                handleSyncRequest(new GcmSyncRequest(extras), startId);
                break;

            case SYNC_START:
                handleSyncStart(new GcmSyncStart(extras), startId);
                break;

            case SYNC_READY:
                handleSyncReady(new GcmSyncReady(extras), startId);
                break;

            case SYNC_MESSAGE:
                handleSyncMessage(new GcmSyncMessage(extras), startId);
                break;
        }
    }

    private void handleChecksum(final GcmChecksum gcmChecksum, final int startId) {
        final String threadId = gcmChecksum.getThreadId();
        final String senderId = gcmChecksum.getSenderId();
        final String checksum = gcmChecksum.getChecksum();

        OperationService.start(this, new ChecksumFailedOperation(threadId, senderId, checksum));

        stopSelf(startId);
    }

    private void handleChecksumFailed(final GcmChecksumFailed gcmChecksumFailed, final int startId) {
        final String threadId = gcmChecksumFailed.getThreadId();
        final String senderId = gcmChecksumFailed.getSenderId();

        OperationService.start(this, new SyncRequestOperation(threadId, senderId));

        stopSelf(startId);
    }

    private void handleSyncRequest(final GcmSyncRequest gcmSyncRequest, final int startId) {
        final String threadId = gcmSyncRequest.getThreadId();
        final String senderId = gcmSyncRequest.getSenderId();
        final String msgUuid = gcmSyncRequest.getMsgUuid();
        final int msgIndex = gcmSyncRequest.getMsgIndex();

        OperationService.start(this, new SyncStartOperation(threadId, senderId, msgUuid, msgIndex));

        stopSelf(startId);
    }

    private void handleSyncStart(final GcmSyncStart gcmSyncStart, final int startId) {
        final String threadId = gcmSyncStart.getThreadId();
        final String senderId = gcmSyncStart.getSenderId();
        final int count = gcmSyncStart.getCount();
        final String lookup = threadId + senderId;

        mMessages.put(lookup, new GcmSyncMessageList(count));

        OperationService.start(this, new SyncReadyOperation(threadId, senderId, count));
    }

    private void handleSyncReady(final GcmSyncReady gcmSyncReady, final int startId) {
        final String threadId = gcmSyncReady.getThreadId();
        final String senderId = gcmSyncReady.getSenderId();
        final int count = gcmSyncReady.getCount();

        OperationService.start(this, new SyncMessagesOperation(threadId, senderId, count));

        stopSelf(startId);
    }

    private void handleSyncMessage(final GcmSyncMessage gcmSyncMessage, final int startId) {
        final String threadId = gcmSyncMessage.getThreadId();
        final String senderId = gcmSyncMessage.getSenderId();
        final String lookup = threadId + senderId;

        final GcmSyncMessageList list = mMessages.get(lookup);

        if (list != null) {
            list.add(gcmSyncMessage);

            if (list.hasAllMessages()) {
                handleLastMessage(lookup);
                stopSelf(startId);
            }
        }
    }

    private void handleLastMessage(final String lookup) {
        final List<GcmSyncMessage> messages = mMessages.remove(lookup);

        Collections.sort(messages, new GcmSyncMessageComparator());

        for (final GcmSyncMessage message : messages) {
            mMessenger.handleMessage(this, message.getBundle());
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

    public static final class BackgroundHandler extends Handler {

        public BackgroundHandler(final Callback callback) {
            super(newThread().getLooper(), callback);
        }

        private static HandlerThread newThread() {
            final HandlerThread thread = new HandlerThread("[bg]");
            thread.start();
            return thread;
        }

        public void start(final Intent intent, final int startId) {
            final Message msg = obtainMessage(intent, startId);
            sendMessage(msg);
        }

        private Message obtainMessage(final Intent intent, final int startId) {
            final Message msg = obtainMessage();
            msg.arg1 = startId;
            msg.obj = intent;
            return msg;
        }

        public void destroy() {
            getLooper().quit();
        }
    }


}
