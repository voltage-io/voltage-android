package io.voltage.app.helpers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import io.pivotal.arca.provider.DataUtils;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;
import io.voltage.app.application.VoltageContentProvider.ThreadUserTable;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.models.Message;
import io.voltage.app.models.Participants;
import io.voltage.app.models.Thread;
import io.voltage.app.models.ThreadUser;

public class OperationHelper {

    public ContentProviderOperation insertThreadOperation(final Thread thread) {
        return ContentProviderOperation.newInsert(VoltageContentProvider.Uris.THREADS)
                .withValues(DataUtils.getContentValues(thread))
                .build();
    }

    public ContentProviderOperation deleteThreadUsersOperation(final Thread thread) {
        return ContentProviderOperation.newDelete(VoltageContentProvider.Uris.THREAD_USERS)
                .withSelection(ThreadUserTable.Columns.THREAD_ID + "=?", new String[]{thread.getId()})
                .build();
    }

    public ContentProviderOperation insertThreadUserOperation(final ThreadUser threadUser) {
        return ContentProviderOperation.newInsert(VoltageContentProvider.Uris.THREAD_USERS)
                .withValues(DataUtils.getContentValues(threadUser))
                .build();
    }

    public ContentProviderOperation insertMessageOperation(final Message message) {
        return ContentProviderOperation.newInsert(VoltageContentProvider.Uris.MESSAGES)
                .withValues(DataUtils.getContentValues(message))
                .build();
    }

    public ContentProviderOperation insertThread(final String threadId, final String threadName) {
        return ContentProviderOperation.newInsert(VoltageContentProvider.Uris.THREADS)
                .withValue(ThreadTable.Columns.ID, threadId)
                .withValue(ThreadTable.Columns.NAME, threadName)
                .build();
    }

    public ContentProviderOperation insertThreadUserOperation(final String threadId, final String userId) {
        return ContentProviderOperation.newInsert(VoltageContentProvider.Uris.THREAD_USERS)
                .withValue(ThreadUserTable.Columns.THREAD_ID, threadId)
                .withValue(ThreadUserTable.Columns.USER_ID, userId)
                .build();
    }

    public ContentProviderOperation deleteThreadUserOperation(final String threadId, final String userId) {
        return ContentProviderOperation.newDelete(VoltageContentProvider.Uris.THREAD_USERS)
                .withSelection(ThreadUserTable.Columns.THREAD_ID + "=?" + " AND " + ThreadUserTable.Columns.USER_ID + "=?", new String[]{threadId, userId})
                .build();
    }

    public ArrayList<ContentProviderOperation> insertThreadUserOperations(final Participants participants, final String regId) {
        final String threadId = participants.getThreadId();
        final List<String> userIdsList = participants.getUserIdsList();
        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        if (userIdsList != null) {
            for (final String userId : userIdsList) {
                if (userId != null && !userId.equals(regId)) {
                    operations.add(insertThreadUserOperation(threadId, userId));
                }
            }
        }
        return operations;
    }

    public ContentProviderOperation updateUserOperation(final String regId, final String oldRegId) {
        final ContentValues values = new ContentValues();
        values.put(UserTable.Columns.REG_ID, regId);

        return ContentProviderOperation.newUpdate(VoltageContentProvider.Uris.USERS)
                .withSelection(UserTable.Columns.REG_ID + "=?", new String[]{oldRegId})
                .withValues(values)
                .build();
    }

    public ContentProviderOperation updateThreadUsersOperation(final String regId, final String oldRegId) {
        final ContentValues values = new ContentValues();
        values.put(ThreadUserTable.Columns.USER_ID, regId);

        return ContentProviderOperation.newUpdate(VoltageContentProvider.Uris.THREAD_USERS)
                .withSelection(ThreadUserTable.Columns.USER_ID + "=?", new String[]{oldRegId})
                .withValues(values)
                .build();
    }

    public ContentProviderOperation updateMessagesOperation(final String regId, final String oldRegId) {
        final ContentValues values = new ContentValues();
        values.put(MessageTable.Columns.SENDER_ID, regId);

        return ContentProviderOperation.newUpdate(VoltageContentProvider.Uris.MESSAGES)
                .withSelection(MessageTable.Columns.SENDER_ID + "=?", new String[]{oldRegId})
                .withValues(values)
                .build();
    }

    public ContentProviderOperation updateMessagesMetadataOperation(final String regId, final String oldRegId) {
        final ContentValues values = new ContentValues();
        values.put(MessageTable.Columns.METADATA, regId);

        return ContentProviderOperation.newUpdate(VoltageContentProvider.Uris.MESSAGES)
                .withSelection(MessageTable.Columns.METADATA + "=?", new String[]{oldRegId})
                .withValues(values)
                .build();
    }

    public ContentProviderOperation updateThread(final String threadId, final String threadName) {
        final ContentValues values = new ContentValues();
        values.put(ThreadTable.Columns.NAME, threadName);

        return ContentProviderOperation.newUpdate(VoltageContentProvider.Uris.THREADS)
                .withSelection(ThreadTable.Columns.ID + "=?", new String[]{threadId})
                .withValues(values)
                .build();
    }
}
