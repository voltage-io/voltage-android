package io.voltage.app.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.provider.DataUtils;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageExecutor;
import io.voltage.app.models.Message;
import io.voltage.app.models.MessageState;
import io.voltage.app.models.Participants;
import io.voltage.app.models.Recipients;
import io.voltage.app.models.Registration;
import io.voltage.app.models.Thread;
import io.voltage.app.models.ThreadUser;
import io.voltage.app.models.Transactions;
import io.voltage.app.models.User;
import io.voltage.app.requests.MessageInsert;
import io.voltage.app.requests.MessageInsertBatch;
import io.voltage.app.requests.MessageQuery;
import io.voltage.app.requests.MessageUpdate;
import io.voltage.app.requests.ParticipantsQuery;
import io.voltage.app.requests.RecipientsQuery;
import io.voltage.app.requests.RegistrationQuery;
import io.voltage.app.requests.RegistrationUpdateBatch;
import io.voltage.app.requests.ThreadInsert;
import io.voltage.app.requests.ThreadKeyUpdate;
import io.voltage.app.requests.ThreadMetadataQuery;
import io.voltage.app.requests.ThreadQuery;
import io.voltage.app.requests.ThreadUpdate;
import io.voltage.app.requests.ThreadUserDelete;
import io.voltage.app.requests.ThreadUserInsert;
import io.voltage.app.requests.TransactionsQuery;
import io.voltage.app.requests.UserDelete;
import io.voltage.app.requests.UserInsert;
import io.voltage.app.requests.UserQuery;
import io.voltage.app.requests.UserUpdate;
import io.voltage.app.utils.Logger;

public interface DatabaseHelper {

    Registration getRegistration(final Context context);
    Message getMessage(final Context context, final String msgUuid);
    User getUser(final Context context, final String regId);
    Thread getThread(final Context context, final String threadId);
    List<Message> getThreadMetadata(final Context context, final String threadId);
    Transactions getTransactions(final Context context, final String threadId);
    Participants getParticipants(final Context context, final String threadId);
    Recipients getRecipients(final Context context, final String msgUuid);
    void insertRecords(final Context context, final Thread thread, final ThreadUser threadUser, final Message message);
    void updateThread(final Context context, final String threadId, final String threadName);
    void insertThread(final Context context, final String threadId, final String threadName);
    void insertThreadUser(final Context context, final String threadId, final String userId);
    void deleteThreadUser(final Context context, final String threadId, final String userId);
    void insertMessage(final Context context, final Message message);
    void updateMessageState(final Context context, final MessageState state);
    void updateMessageState(final Context context, final String msgUuid, final int state);
    void insertUser(final Context context, final User user);
    void deleteUser(final Context context, final String regId);
    void updateRegistration(final Context context, final String regId, final String oldRegId);

    class Default implements DatabaseHelper {

        public Registration getRegistration(final Context context) {
            return executeForObject(context, new RegistrationQuery(), Registration.class);
        }

        public Message getMessage(final Context context, final String msgUuid) {
            return executeForObject(context, new MessageQuery(msgUuid), Message.class);
        }

        public User getUser(final Context context, final String regId) {
            return executeForObject(context, new UserQuery(regId), User.class);
        }

        public Thread getThread(final Context context, final String threadId) {
            return executeForObject(context, new ThreadQuery(threadId), Thread.class);
        }

        public List<Message> getThreadMetadata(final Context context, final String threadId) {
            return executeForList(context, new ThreadMetadataQuery(threadId), Message.class);
        }

        public Transactions getTransactions(final Context context, final String threadId) {
            return executeForObject(context, new TransactionsQuery(threadId), Transactions.class);
        }

        public Participants getParticipants(final Context context, final String threadId) {
            return executeForObject(context, new ParticipantsQuery(threadId), Participants.class);
        }

        public Recipients getRecipients(final Context context, final String msgUuid) {
            return executeForObject(context, new RecipientsQuery(msgUuid), Recipients.class);
        }

        public void insertRecords(final Context context, final Thread thread, final ThreadUser threadUser, final Message message) {
            VoltageExecutor.execute(context, new MessageInsertBatch(thread, threadUser, message));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX);
        }

        public void insertThread(final Context context, final String threadId, final String threadName) {
            VoltageExecutor.execute(context, new ThreadInsert(threadId, threadName));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX);
        }

        public void updateThread(final Context context, final String threadId, final String threadName) {
            VoltageExecutor.execute(context, new ThreadUpdate(threadId, threadName));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX);
        }

        public void updateThreadKey(final Context context, final String threadId, final String key) {
            VoltageExecutor.execute(context, new ThreadKeyUpdate(threadId, key));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX);
        }

        public void insertThreadUser(final Context context, final String threadId, final String userId) {
            VoltageExecutor.execute(context, new ThreadUserInsert(threadId, userId));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX, VoltageContentProvider.Uris.MEMBERS);
        }

        public void deleteThreadUser(final Context context, final String threadId, final String userId) {
            VoltageExecutor.execute(context, new ThreadUserDelete(threadId, userId));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX, VoltageContentProvider.Uris.MEMBERS);
        }

        public void insertMessage(final Context context, final Message message) {
            VoltageExecutor.execute(context, new MessageInsert(message));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX);
        }

        public void updateMessageState(final Context context, final MessageState state) {
            VoltageExecutor.execute(context, new MessageUpdate(state.getMsgUuid(), state.getState()));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX);
        }

        public void updateMessageState(final Context context, final String msgUuid, final int state) {
            VoltageExecutor.execute(context, new MessageUpdate(msgUuid, state));

            notify(context, VoltageContentProvider.Uris.CONVERSATION, VoltageContentProvider.Uris.INBOX);
        }

        public void insertUser(final Context context, final User user) {
            VoltageExecutor.execute(context, new UserInsert(user.getName(), user.getRegId(), user.getPublicKey()));

            notify(context, VoltageContentProvider.Uris.USERS);
        }

        public void updateUser(final Context context, final User user) {
            VoltageExecutor.execute(context, new UserUpdate(user.getName(), user.getRegId(), user.getPublicKey()));

            notify(context, VoltageContentProvider.Uris.USERS);
        }

        public void deleteUser(final Context context, final String regId) {
            VoltageExecutor.execute(context, new UserDelete(regId));

            notify(context, VoltageContentProvider.Uris.USERS);
        }

        public void updateRegistration(final Context context, final String regId, final String oldRegId) {
            VoltageExecutor.execute(context, new RegistrationUpdateBatch(regId, oldRegId));

            notify(context, VoltageContentProvider.Uris.USERS);
        }


        // ===============================================


        private <T> T executeForObject(final Context context, final Query request, final Class<T> klass) {
            final List<T> list = executeForList(context, request, klass);
            return list != null && list.size() > 0 ? list.get(0) : null;
        }

        private <T> List<T> executeForList(final Context context, final Query request, final Class<T> klass) {
            final Cursor cursor = VoltageExecutor.execute(context, request).getData();

            try {
                return DataUtils.getList(cursor, klass);
            } catch (final Exception e) {
                Logger.ex(e);
                return null;
            } finally {
                cursor.close();
            }
        }

        private void notify(final Context context, final Uri... uris) {
            for (final Uri uri: uris) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }
    }
}
