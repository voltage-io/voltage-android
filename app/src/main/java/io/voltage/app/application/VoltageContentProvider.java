package io.voltage.app.application;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.List;

import io.pivotal.arca.provider.Column;
import io.pivotal.arca.provider.ColumnOptions;
import io.pivotal.arca.provider.DataUtils;
import io.pivotal.arca.provider.DatabaseProvider;
import io.pivotal.arca.provider.GroupBy;
import io.pivotal.arca.provider.Joins;
import io.pivotal.arca.provider.OrderBy;
import io.pivotal.arca.provider.SQLiteTable;
import io.pivotal.arca.provider.SQLiteView;
import io.pivotal.arca.provider.SearchDataset;
import io.pivotal.arca.provider.Select;
import io.pivotal.arca.provider.SelectAs;
import io.pivotal.arca.provider.SelectFrom;
import io.pivotal.arca.provider.Unique;
import io.pivotal.arca.provider.Where;
import io.voltage.app.models.Registration;

public class VoltageContentProvider extends DatabaseProvider {

    public static final String AUTHORITY = VoltageContentProvider.class.getName();
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public interface Uris {
        Uri CRASHES = Uri.withAppendedPath(BASE_URI, Paths.CRASHES);
        Uri REGISTRATIONS = Uri.withAppendedPath(BASE_URI, Paths.REGISTRATIONS);
        Uri THREADS = Uri.withAppendedPath(BASE_URI, Paths.THREADS);
        Uri USERS =  Uri.withAppendedPath(BASE_URI, Paths.USERS);
        Uri THREAD_USERS = Uri.withAppendedPath(BASE_URI, Paths.THREAD_USERS);
        Uri MESSAGES = Uri.withAppendedPath(BASE_URI, Paths.MESSAGES);
        Uri INBOX = Uri.withAppendedPath(BASE_URI, Paths.INBOX);
        Uri CONVERSATION = Uri.withAppendedPath(BASE_URI, Paths.CONVERSATION);
        Uri PARTICIPANTS = Uri.withAppendedPath(BASE_URI, Paths.PARTICIPANTS);
        Uri MEMBERS = Uri.withAppendedPath(BASE_URI, Paths.MEMBERS);
        Uri RECIPIENTS = Uri.withAppendedPath(BASE_URI, Paths.RECIPIENTS);
        Uri TRANSACTIONS = Uri.withAppendedPath(BASE_URI, Paths.TRANSACTIONS);
        Uri USER_SEARCH = Uri.withAppendedPath(BASE_URI, Paths.USER_SEARCH);
    }

    private interface Paths {
        String CRASHES = "crashes";
        String REGISTRATIONS = "registrations";
        String THREADS = "threads";
        String USERS = "users";
        String THREAD_USERS = "thread_users";
        String MESSAGES = "messages";
        String INBOX = "inbox";
        String CONVERSATION = "conversation";
        String PARTICIPANTS = "participants";
        String MEMBERS = "members";
        String RECIPIENTS = "recipients";
        String TRANSACTIONS = "transactions";
        String USER_SEARCH = "user_search";
    }

    @Override
    public boolean onCreate() {
        registerDataset(AUTHORITY, Paths.CRASHES, CrashTable.class);
        registerDataset(AUTHORITY, Paths.REGISTRATIONS, RegistrationTable.class);
        registerDataset(AUTHORITY, Paths.THREADS, ThreadTable.class);
        registerDataset(AUTHORITY, Paths.USERS, UserTable.class);
        registerDataset(AUTHORITY, Paths.THREAD_USERS, ThreadUserTable.class);
        registerDataset(AUTHORITY, Paths.MESSAGES, MessageTable.class);
        registerDataset(AUTHORITY, Paths.INBOX, InboxView.class);
        registerDataset(AUTHORITY, Paths.CONVERSATION, ConversationView.class);
        registerDataset(AUTHORITY, Paths.PARTICIPANTS, ParticipantView.class);
        registerDataset(AUTHORITY, Paths.MEMBERS, MemberView.class);
        registerDataset(AUTHORITY, Paths.RECIPIENTS, RecipientView.class);
        registerDataset(AUTHORITY, Paths.TRANSACTIONS, TransactionView.class);
        registerDataset(AUTHORITY, Paths.USER_SEARCH, UserSearchView.class);
        return true;
    }

    public static class CrashTable extends SQLiteTable {
        public interface Columns extends SQLiteTable.Columns {
            @Unique(Unique.OnConflict.REPLACE)
            @Column(Column.Type.TEXT) String ID = "id";
            @Column(Column.Type.TEXT) String TRACE = "trace";
        }
    }

    public static class RegistrationTable extends SQLiteTable {
        public interface Columns extends SQLiteTable.Columns {
            @Unique(Unique.OnConflict.REPLACE)
            @Column(Column.Type.TEXT) String REG_ID = "reg_id";
            @Column(Column.Type.TEXT) String LOOKUP = "lookup";
        }

        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    public static class ThreadTable extends SQLiteTable {

        public interface State {
            int DEFAULT = 0, MUTED = 1;
        }

        public interface Columns extends SQLiteTable.Columns {
            @Unique(Unique.OnConflict.IGNORE)
            @Column(Column.Type.TEXT) String ID = "id";
            @Column(Column.Type.TEXT) String NAME = "name";
            @Column(Column.Type.TEXT) String KEY = "key";
        }

        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    public static class UserTable extends SQLiteTable {
        public interface Columns extends SQLiteTable.Columns {
            @Unique(Unique.OnConflict.REPLACE)
            @Column(Column.Type.TEXT) String REG_ID = "reg_id";
            @ColumnOptions("DEFAULT (hex(randomblob(3)))")
            @Column(Column.Type.TEXT) String NAME = "name";
            @ColumnOptions("DEFAULT ''")
            @Column(Column.Type.TEXT) String PUBLIC_KEY = "public_key";
        }

        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    public static class ThreadUserTable extends SQLiteTable {
        public interface Columns extends SQLiteTable.Columns {
            @Unique(Unique.OnConflict.REPLACE)
            @Column(Column.Type.TEXT) String THREAD_ID = "thread_id";
            @Unique(Unique.OnConflict.REPLACE)
            @Column(Column.Type.TEXT) String USER_ID = "user_id";
        }

        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    public static class MessageTable extends SQLiteTable {

        public interface State {
            int DEFAULT = 0, SENDING = 1, UNREAD = 2, ERROR = 3, RECEIPT = 4;
        }

        public interface Columns extends SQLiteTable.Columns {
            @Unique(Unique.OnConflict.REPLACE)
            @Column(Column.Type.TEXT) String MSG_UUID = "msg_uuid";
            @Column(Column.Type.TEXT) String THREAD_ID = "thread_id";
            @Column(Column.Type.TEXT) String SENDER_ID = "sender_id";
            @Column(Column.Type.TEXT) String TEXT = "text";
            @Column(Column.Type.TEXT) String METADATA = "metadata";
            @Column(Column.Type.INTEGER) String TIMESTAMP = "timestamp";
            @ColumnOptions("DEFAULT 'MESSAGE'")
            @Column(Column.Type.TEXT) String TYPE = "type";
        }

        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }


    public static class InboxView extends SQLiteView {

        @SelectFrom("MessageTable m")

        @Joins({
            "LEFT JOIN ThreadTable t ON t.id = m.thread_id",
            "LEFT JOIN ThreadUserTable tu ON tu.thread_id = m.thread_id",
            "LEFT JOIN UserTable u ON u.reg_id = tu.user_id",
        })

        @GroupBy("m.thread_id")

        @OrderBy("message_timestamp DESC")

        public interface Columns {
            @Select("t._id") String _ID = "_id";

            @Select("t.id") String THREAD_ID = "thread_id";

            @Select("t.name") String THREAD_NAME = "thread_name";

            @Select("GROUP_CONCAT(DISTINCT tu.user_id)") String USER_IDS = "user_ids";

            @Select("GROUP_CONCAT(DISTINCT u.name)") String USER_NAMES = "user_names";

            @Select("MAX(m.timestamp)") String MESSAGE_TIMESTAMP = "message_timestamp";

            @Select("m.text") String MESSAGE_TEXT = "message_text";

            @Select("m._state") String MESSAGE_STATE = "message_state";
        }
    }

    public static class ConversationView extends SQLiteView {

        @SelectFrom("MessageTable m")

        @Joins({
            "LEFT JOIN ThreadTable t ON t.id = m.thread_id",
            "LEFT JOIN UserTable u ON u.reg_id = m.sender_id",
            "LEFT JOIN UserTable mu ON mu.reg_id = m.metadata"
        })

        @OrderBy("m.timestamp")

        public interface Columns {
            @Select("m._id") String _ID = "_id";

            @Select("m.msg_uuid") String MSG_UUID = "msg_uuid";

            @Select("m.thread_id") String THREAD_ID = "thread_id";

            @Select("t.name") String THREAD_NAME = "thread_name";

            @Select("m.sender_id") String SENDER_ID = "sender_id";

            @Select("u.name") String SENDER_NAME = "sender_name";

            @Select("m.text") String TEXT = "text";

            @Select("m.metadata") String METADATA = "metadata";

            @Select("m.timestamp") String TIMESTAMP = "timestamp";

            @Select("mu.name") String META_USER = "meta_user";

            @Select("m.type") String TYPE = "type";

            @Select("m._state") String _STATE = "_state";
        }
    }

    public static class ParticipantView extends SQLiteView {

        @SelectFrom("ThreadUserTable tu")

        @Joins({
            "LEFT JOIN ThreadTable t ON t.id = tu.thread_id",
            "LEFT JOIN UserTable u ON u.reg_id = tu.user_id"
        })

        @GroupBy("tu.thread_id")

        public interface Columns {
            @Select("tu._id") String _ID = "_id";

            @Select("tu.thread_id") String THREAD_ID = "thread_id";

            @Select("t.name") String THREAD_NAME = "thread_name";

            @Select("t._state") String THREAD_STATE = "thread_state";

            @Select("GROUP_CONCAT(tu.user_id)") String USER_IDS = "user_ids";

            @Select("GROUP_CONCAT(u.name)") String USER_NAMES = "user_names";
        }
    }

    public static class MemberView extends SQLiteView {

        @SelectFrom("ThreadUserTable tu")

        @Joins({
            "LEFT JOIN ThreadTable t ON t.id = tu.thread_id",
            "LEFT JOIN UserTable u ON u.reg_id = tu.user_id"
        })

        @Where("tu.user_id NOT IN (SELECT reg_id from RegistrationTable)")

        @OrderBy("u.name")

        public interface Columns {
            @Select("tu._id") String _ID = "_id";

            @Select("tu.thread_id") String THREAD_ID = "thread_id";

            @Select("u.reg_id") String USER_ID = "user_id";

            @Select("u.name") String USER_NAME = "user_name";
        }
    }

    public static class RecipientView extends SQLiteView {

        @SelectFrom("MessageTable m")

        @Joins({
            "INNER JOIN ThreadUserTable tu ON tu.thread_id = m.thread_id",
            "INNER JOIN UserTable u ON u.reg_id = tu.user_id",
            "LEFT JOIN ThreadTable t ON t.id = m.thread_id"
        })

        @GroupBy("m.msg_uuid")

        public interface Columns {
            @Select("m._id") String _ID = "_id";

            @Select("m.msg_uuid") String MSG_UUID = "msg_uuid";

            @Select("t.id") String THREAD_ID = "thread_id";

            @Select("t.name") String THREAD_NAME = "thread_name";

            @Select("t.key") String THREAD_KEY = "thread_key";

            @Select("GROUP_CONCAT(tu.user_id)") String USER_IDS = "user_ids";

            @Select("GROUP_CONCAT(u.name)") String USER_NAMES = "user_names";

            @Select("GROUP_CONCAT(u.public_key)") String USER_PUBLIC_KEYS = "user_public_keys";
        }
    }

    public static class TransactionView extends SQLiteView {

        @SelectFrom(
            "SELECT msg_uuid, thread_id, ThreadTable.* FROM MessageTable INNER JOIN ThreadTable " +
                "ON ThreadTable.id = MessageTable.thread_id WHERE type != 'MESSAGE' ORDER BY timestamp"
        )

        @SelectAs("t")

        @GroupBy("t.thread_id")

        public interface Columns {
            @Select("t._id") String _ID = "_id";

            @Select("t.thread_id") String THREAD_ID = "thread_id";

            @Select("t.name") String THREAD_NAME = "thread_name";

            @Select("GROUP_CONCAT(t.msg_uuid)") String MSG_UUIDS = "msg_uuids";

            @Select("COUNT(t.msg_uuid)") String COUNT = "count";
        }
    }

    public static class UserSearchView extends SearchDataset {

        public interface Columns {
            @Column(Column.Type.TEXT) String _ID = "_id";
            @Column(Column.Type.TEXT) String REG_ID = "reg_id";
            @Column(Column.Type.TEXT) String LOOKUP = "lookup";
        }

        public Cursor search(final String[] args) throws Exception {
            final List<Registration> registrations = VoltageApi.getRegistrations(args[0]);
            return DataUtils.getCursor(registrations);
        }
    }

}