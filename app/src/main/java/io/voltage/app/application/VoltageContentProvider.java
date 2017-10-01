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
import io.pivotal.arca.provider.SelectFrom;
import io.pivotal.arca.provider.Unique;
import io.voltage.app.models.Registration;

public class VoltageContentProvider extends DatabaseProvider {

    public static final String AUTHORITY = VoltageContentProvider.class.getName();
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public interface Uris {
        Uri REGISTRATIONS = Uri.withAppendedPath(BASE_URI, Paths.REGISTRATIONS);
        Uri THREADS = Uri.withAppendedPath(BASE_URI, Paths.THREADS);
        Uri USERS =  Uri.withAppendedPath(BASE_URI, Paths.USERS);
        Uri THREAD_USERS = Uri.withAppendedPath(BASE_URI, Paths.THREAD_USERS);
        Uri MESSAGES = Uri.withAppendedPath(BASE_URI, Paths.MESSAGES);
        Uri INBOX = Uri.withAppendedPath(BASE_URI, Paths.INBOX);
        Uri CONVERSATION = Uri.withAppendedPath(BASE_URI, Paths.CONVERSATION);
        Uri PARTICIPANTS = Uri.withAppendedPath(BASE_URI, Paths.PARTICIPANTS);
        Uri RECIPIENTS = Uri.withAppendedPath(BASE_URI, Paths.RECIPIENTS);
        Uri MEMBERS = Uri.withAppendedPath(BASE_URI, Paths.MEMBERS);
        Uri TRANSACTIONS = Uri.withAppendedPath(BASE_URI, Paths.TRANSACTIONS);
        Uri USER_SEARCH = Uri.withAppendedPath(BASE_URI, Paths.USER_SEARCH);
    }

    private interface Paths {
        String REGISTRATIONS = "registrations";
        String THREADS = "threads";
        String USERS = "users";
        String THREAD_USERS = "thread_users";
        String MESSAGES = "messages";
        String INBOX = "inbox";
        String CONVERSATION = "conversation";
        String PARTICIPANTS = "participants";
        String RECIPIENTS = "recipients";
        String MEMBERS = "members";
        String TRANSACTIONS = "transactions";
        String USER_SEARCH = "user_search";
    }

    @Override
    public boolean onCreate() {
        registerDataset(AUTHORITY, Paths.REGISTRATIONS, RegistrationTable.class);
        registerDataset(AUTHORITY, Paths.THREADS, ThreadTable.class);
        registerDataset(AUTHORITY, Paths.USERS, UserTable.class);
        registerDataset(AUTHORITY, Paths.THREAD_USERS, ThreadUserTable.class);
        registerDataset(AUTHORITY, Paths.MESSAGES, MessageTable.class);
        registerDataset(AUTHORITY, Paths.INBOX, InboxView.class);
        registerDataset(AUTHORITY, Paths.CONVERSATION, ConversationView.class);
        registerDataset(AUTHORITY, Paths.PARTICIPANTS, ParticipantView.class);
        registerDataset(AUTHORITY, Paths.RECIPIENTS, RecipientView.class);
        registerDataset(AUTHORITY, Paths.MEMBERS, MemberView.class);
        registerDataset(AUTHORITY, Paths.TRANSACTIONS, TransactionView.class);
        registerDataset(AUTHORITY, Paths.USER_SEARCH, UserSearchView.class);
        return true;
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
            @Unique(Unique.OnConflict.IGNORE)
            @Column(Column.Type.TEXT) String REG_ID = "reg_id";
            @Column(Column.Type.TEXT) String NAME = "name";
            @Column(Column.Type.TEXT) String IMAGE = "image";
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

        @SelectFrom("ThreadTable")

        @Joins({
            "LEFT JOIN ThreadUserTable ON ThreadTable.id = ThreadUserTable.thread_id",
            "LEFT JOIN UserTable ON ThreadUserTable.user_id = UserTable.reg_id",
            "INNER JOIN (" +
                "SELECT MAX(timestamp) timestamp, thread_id, text, _state FROM MessageTable GROUP BY thread_id" +
            ") as MessageTable ON ThreadTable.id = MessageTable.thread_id"
        })

        @GroupBy("ThreadTable.id")

        @OrderBy("MessageTable.timestamp DESC")

        public interface Columns {

            @Select("ThreadTable._id")
            public static final String _ID = "_id";

            @Select("ThreadTable.id")
            public static final String THREAD_ID = "thread_id";

            @Select("ThreadTable.name")
            public static final String THREAD_NAME = "thread_name";

            @Select("ThreadTable._state")
            public static final String THREAD_STATE = "thread_state";

            @Select("GROUP_CONCAT(DISTINCT ThreadUserTable.user_id)")
            public static final String USER_IDS = "user_ids";

            @Select("GROUP_CONCAT(DISTINCT UserTable.name)")
            public static final String USER_NAMES = "user_names";

            @Select("MessageTable.text")
            public static final String MESSAGE_TEXT = "message_text";

            @Select("MessageTable.timestamp")
            public static final String MESSAGE_TIMESTAMP = "message_timestamp";

            @Select("MessageTable._state")
            public static final String MESSAGE_STATE = "message_state";
        }
    }


    public static class ConversationView extends SQLiteView {

        @SelectFrom("ThreadTable")

        @Joins({
            "INNER JOIN MessageTable ON ThreadTable.id = MessageTable.thread_id",
            "LEFT JOIN UserTable ON MessageTable.sender_id = UserTable.reg_id",
            "LEFT JOIN UserTable as MetaUser ON MessageTable.metadata = MetaUser.reg_id"
        })

        @OrderBy("MessageTable.timestamp")

        public interface Columns {

            @Select("MessageTable._id")
            public static final String _ID = "_id";

            @Select("MessageTable.msg_uuid")
            public static final String MSG_UUID = "msg_uuid";

            @Select("ThreadTable.id")
            public static final String THREAD_ID = "thread_id";

            @Select("ThreadTable.name")
            public static final String THREAD_NAME = "thread_name";

            @Select("MessageTable.sender_id")
            public static final String SENDER_ID = "sender_id";

            @Select("UserTable.name")
            public static final String SENDER_NAME = "sender_name";

            @Select("MessageTable.text")
            public static final String TEXT = "text";

            @Select("MessageTable.metadata")
            public static final String METADATA = "metadata";

            @Select("MessageTable.timestamp")
            public static final String TIMESTAMP = "timestamp";

            @Select("MetaUser.name")
            public static final String META_USER = "meta_user";

            @Select("MessageTable.type")
            public static final String TYPE = "type";

            @Select("MessageTable._state")
            public static final String _STATE = "_state";
        }
    }

    public static class ParticipantView extends SQLiteView {

        @SelectFrom("ThreadTable")

        @Joins({
            "INNER JOIN ThreadUserTable ON ThreadTable.id = ThreadUserTable.thread_id",
            "LEFT JOIN UserTable ON ThreadUserTable.user_id = UserTable.reg_id"
        })

        @GroupBy("ThreadTable.id")

        public interface Columns {

            @Select("ThreadTable._id")
            public static final String _ID = "_id";

            @Select("ThreadTable.id")
            public static final String THREAD_ID = "thread_id";

            @Select("ThreadTable.name")
            public static final String THREAD_NAME = "thread_name";

            @Select("ThreadTable._state")
            public static final String THREAD_STATE = "thread_state";

            @Select("GROUP_CONCAT(DISTINCT ThreadUserTable.user_id)")
            public static final String USER_IDS = "user_ids";

            @Select("GROUP_CONCAT(DISTINCT UserTable.name)")
            public static final String USER_NAMES = "user_names";
        }
    }

    public static class RecipientView extends SQLiteView {

        @SelectFrom("MessageTable")

        @Joins({
            "LEFT JOIN ThreadTable ON MessageTable.thread_id = ThreadTable.id",
            "INNER JOIN ThreadUserTable ON ThreadTable.id = ThreadUserTable.thread_id",
            "LEFT JOIN UserTable ON ThreadUserTable.user_id = UserTable.reg_id"
        })

        @GroupBy("MessageTable.msg_uuid")

        public interface Columns {

            @Select("MessageTable._id")
            public static final String _ID = "_id";

            @Select("MessageTable.msg_uuid")
            public static final String MSG_UUID = "msg_uuid";

            @Select("ThreadTable.id")
            public static final String THREAD_ID = "thread_id";

            @Select("ThreadTable.name")
            public static final String THREAD_NAME = "thread_name";

            @Select("ThreadTable._state")
            public static final String THREAD_STATE = "thread_state";

            @Select("GROUP_CONCAT(DISTINCT ThreadUserTable.user_id)")
            public static final String USER_IDS = "user_ids";

            @Select("GROUP_CONCAT(DISTINCT UserTable.name)")
            public static final String USER_NAMES = "user_names";
        }
    }

    public static class MemberView extends SQLiteView {

        @SelectFrom("ThreadTable")

        @Joins({
            "INNER JOIN ThreadUserTable ON ThreadTable.id = ThreadUserTable.thread_id",
            "LEFT JOIN UserTable ON ThreadUserTable.user_id = UserTable.reg_id"
        })

        public interface Columns {

            @Select("ThreadTable._id")
            public static final String _ID = "_id";

            @Select("ThreadTable.id")
            public static final String THREAD_ID = "thread_id";

            @Select("ThreadTable.name")
            public static final String THREAD_NAME = "thread_name";

            @Select("ThreadTable._state")
            public static final String THREAD_STATE = "thread_state";

            @Select("ThreadUserTable.user_id")
            public static final String USER_ID = "user_id";

            @Select("UserTable.name")
            public static final String USER_NAME = "user_name";
        }
    }

    public static class NonMemberView extends SQLiteView {

        @SelectFrom("ThreadTable")

        @Joins({
            "OUTER JOIN ThreadUserTable ON ThreadTable.id = ThreadUserTable.thread_id",
            "LEFT JOIN UserTable ON ThreadUserTable.user_id = UserTable.reg_id"
        })

        public interface Columns {

            @Select("ThreadTable._id")
            public static final String _ID = "_id";

            @Select("ThreadTable.id")
            public static final String THREAD_ID = "thread_id";

            @Select("ThreadTable.name")
            public static final String THREAD_NAME = "thread_name";

            @Select("ThreadTable._state")
            public static final String THREAD_STATE = "thread_state";

            @Select("ThreadUserTable.user_id")
            public static final String USER_ID = "user_id";

            @Select("UserTable.name")
            public static final String USER_NAME = "user_name";
        }
    }

    public static class TransactionView extends SQLiteView {

        @SelectFrom("ThreadTable")

        @Joins({
            "INNER JOIN (" +
                "SELECT COUNT(*) count, GROUP_CONCAT(msg_uuid) msg_uuids, thread_id FROM (" +
                    "SELECT msg_uuid, thread_id FROM MessageTable WHERE type != 'MESSAGE' ORDER BY timestamp" +
                ") GROUP BY thread_id" +
            ") as TransactionTable ON ThreadTable.id = TransactionTable.thread_id"
        })

        public interface Columns {

            @Select("ThreadTable._id")
            public static final String _ID = "_id";

            @Select("ThreadTable.id")
            public static final String THREAD_ID = "thread_id";

            @Select("ThreadTable.name")
            public static final String THREAD_NAME = "thread_name";

            @Select("ThreadTable._state")
            public static final String THREAD_STATE = "thread_state";

            @Select("TransactionTable.msg_uuids")
            public static final String MSG_UUIDS = "msg_uuids";

            @Select("TransactionTable.count")
            public static final String COUNT = "count";
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