package io.voltage.app.helpers;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import io.pivotal.arca.provider.DataUtils;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.application.VoltageContentProvider.ParticipantView;
import io.voltage.app.application.VoltageContentProvider.RecipientView;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;
import io.voltage.app.application.VoltageContentProvider.TransactionView;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.Message;
import io.voltage.app.models.MessageState;
import io.voltage.app.models.Participants;
import io.voltage.app.models.Recipients;
import io.voltage.app.models.Thread;
import io.voltage.app.models.ThreadUser;
import io.voltage.app.models.Transactions;
import io.voltage.app.models.User;
import io.voltage.app.utils.Logger;

public class DatabaseHelper {

    private final OperationHelper mOperationHelper = new OperationHelper();

    public Message getMessage(final Context context, final String msgUuid) {
        final Uri uri = VoltageContentProvider.Uris.MESSAGES;
        final String whereClause = MessageTable.Columns.MSG_UUID + "=?";
        final String[] whereArgs = new String[]{ msgUuid };

        final ContentResolver resolver = context.getContentResolver();
        final Cursor cursor = resolver.query(uri, null, whereClause, whereArgs, null);

        try {
            return DataUtils.getList(cursor, Message.class).get(0);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    public User getUser(final Context context, final String regId) {
        final Uri uri = VoltageContentProvider.Uris.USERS;
        final String whereClause = UserTable.Columns.REG_ID + "=?";
        final String[] whereArgs = new String[]{ regId };

        final ContentResolver resolver = context.getContentResolver();
        final Cursor cursor = resolver.query(uri, null, whereClause, whereArgs, null);

        try {
            return DataUtils.getList(cursor, User.class).get(0);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    public Thread getThread(final Context context, final String threadId) {
        final Uri uri = VoltageContentProvider.Uris.THREADS;
        final String whereClause = ThreadTable.Columns.ID + "=?";
        final String[] whereArgs = new String[]{ threadId };

        final ContentResolver resolver = context.getContentResolver();
        final Cursor cursor = resolver.query(uri, null, whereClause, whereArgs, null);

        try {
            return DataUtils.getList(cursor, Thread.class).get(0);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    public List<Message> getThreadMetadata(final Context context, final String threadId) {
        final Uri uri = VoltageContentProvider.Uris.MESSAGES;
        final String whereClause = MessageTable.Columns.THREAD_ID + "=?" + " AND " + MessageTable.Columns.TYPE + "!=?";
        final String[] whereArgs = new String[]{ threadId, "MESSAGE" };

        final ContentResolver resolver = context.getContentResolver();
        final Cursor cursor = resolver.query(uri, null, whereClause, whereArgs, MessageTable.Columns.TIMESTAMP);

        try {
            return DataUtils.getList(cursor, Message.class);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    public Transactions getTransactions(final Context context, final String threadId) {
        final Uri uri = VoltageContentProvider.Uris.TRANSACTIONS;
        final String whereClause = TransactionView.Columns.THREAD_ID + "=?";
        final String[] whereArgs = new String[]{ threadId };

        final ContentResolver resolver = context.getContentResolver();
        final Cursor cursor = resolver.query(uri, null, whereClause, whereArgs, null);

        try {
            return DataUtils.getList(cursor, Transactions.class).get(0);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    public Participants getParticipants(final Context context, final String threadId) {
        final Uri uri = VoltageContentProvider.Uris.PARTICIPANTS;
        final String whereClause = ParticipantView.Columns.THREAD_ID + "=?";
        final String[] whereArgs = new String[]{threadId};

        final ContentResolver resolver = context.getContentResolver();
        final Cursor cursor = resolver.query(uri, null, whereClause, whereArgs, null);

        try {
            return DataUtils.getList(cursor, Participants.class).get(0);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    public Recipients getRecipients(final Context context, final String msgUuid) {
        final Uri uri = VoltageContentProvider.Uris.RECIPIENTS;
        final String whereClause = RecipientView.Columns.MSG_UUID + "=?";
        final String[] whereArgs = new String[]{msgUuid};

        final ContentResolver resolver = context.getContentResolver();
        final Cursor cursor = resolver.query(uri, null, whereClause, whereArgs, null);

        try {
            return DataUtils.getList(cursor, Recipients.class).get(0);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        } finally {
            cursor.close();
        }
    }

    public void insertRecords(final Context context, final Thread thread, final ThreadUser threadUser, final Message message) {
        try {
            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            operations.add(mOperationHelper.insertThreadOperation(thread));
            operations.add(mOperationHelper.insertMessageOperation(message));

            if (!VoltagePreferences.getRegId(context).equals(threadUser.getUserId())) {
                operations.add(mOperationHelper.insertThreadUserOperation(threadUser));
            }

            final ContentResolver resolver = context.getContentResolver();
            resolver.applyBatch(VoltageContentProvider.AUTHORITY, operations);
            resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
            resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    public void updateThread(final Context context, final String threadId, final String threadName) {
        try {
            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            operations.add(mOperationHelper.updateThread(threadId, threadName));

            final ContentResolver resolver = context.getContentResolver();
            resolver.applyBatch(VoltageContentProvider.AUTHORITY, operations);
            resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
            resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    public void insertThreadUser(final Context context, final String threadId, final String userId) {
        try {
            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            operations.add(mOperationHelper.insertThreadUserOperation(threadId, userId));

            final ContentResolver resolver = context.getContentResolver();
            resolver.applyBatch(VoltageContentProvider.AUTHORITY, operations);
            resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
            resolver.notifyChange(VoltageContentProvider.Uris.MEMBERS, null);
            resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    public void deleteThreadUser(final Context context, final String threadId, final String userId) {
        try {
            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            operations.add(mOperationHelper.deleteThreadUserOperation(threadId, userId));

            final ContentResolver resolver = context.getContentResolver();
            resolver.applyBatch(VoltageContentProvider.AUTHORITY, operations);
            resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
            resolver.notifyChange(VoltageContentProvider.Uris.MEMBERS, null);
            resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    public void updateState(final Context context, final MessageState state) {
        try {
            final String where = MessageTable.Columns.MSG_UUID + "=?";
            final String[] whereArgs = new String[]{state.getMsgUuid()};
            final ContentValues values = DataUtils.getContentValues(state);

            final ContentResolver resolver = context.getContentResolver();
            resolver.update(VoltageContentProvider.Uris.MESSAGES, values, where, whereArgs);
            resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
            resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    public void updateMessageState(final Context context, final String msgUuid, final int state) {
        try {
            final String where = MessageTable.Columns.MSG_UUID + "=?";
            final String[] whereArgs = new String[] { msgUuid };
            final ContentValues values = new ContentValues();
            values.put(MessageTable.Columns._STATE, state);

            final ContentResolver resolver = context.getContentResolver();
            resolver.update(VoltageContentProvider.Uris.MESSAGES, values, where, whereArgs);
            resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
            resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    public void insertUser(final Context context, final User user) {
        try {
            final ContentValues values = DataUtils.getContentValues(user);

            final ContentResolver resolver = context.getContentResolver();
            resolver.insert(VoltageContentProvider.Uris.USERS, values);
            resolver.notifyChange(VoltageContentProvider.Uris.USERS, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }

    public void updateRegistrationIds(final Context context, final List<String> regIds, final GcmResponse response) {
        final List<GcmResponse.Result> results = response.getResults();
        for (int i = 0; i < results.size(); i++) {

            final GcmResponse.Result result = results.get(i);
            final String regId = result.getRegistrationId();

            if (regId != null) {
                final String oldRegId = regIds.get(i);
                updateRegistrationId(context, regId, oldRegId);

                Logger.v("New registration id: " + regId);
                Logger.v("Old registration id: " + oldRegId);
            }
        }
    }

    private void updateRegistrationId(final Context context, final String regId, final String oldRegId) {
        try {
            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            operations.add(mOperationHelper.updateUserOperation(regId, oldRegId));
            operations.add(mOperationHelper.updateThreadUsersOperation(regId, oldRegId));
            operations.add(mOperationHelper.updateMessagesOperation(regId, oldRegId));
            operations.add(mOperationHelper.updateMessagesMetadataOperation(regId, oldRegId));

            final ContentResolver resolver = context.getContentResolver();
            resolver.applyBatch(VoltageContentProvider.AUTHORITY, operations);
            resolver.notifyChange(VoltageContentProvider.Uris.USERS, null);
        } catch (final Exception e) {
            Logger.ex(e);
        }
    }
}
