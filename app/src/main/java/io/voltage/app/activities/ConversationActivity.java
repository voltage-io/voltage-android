package io.voltage.app.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.pivotal.arca.dispatcher.QueryListener;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.ParticipantView;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.fragments.ConversationFragment;
import io.voltage.app.fragments.MessageSendFragment;
import io.voltage.app.helpers.FormatHelper;
import io.voltage.app.helpers.NotificationHelper;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.monitors.ParticipantsMonitor;
import io.voltage.app.requests.MessageInsert;
import io.voltage.app.requests.ParticipantsQuery;
import io.voltage.app.requests.ThreadUpdate;

public class ConversationActivity extends ColorActivity implements QueryListener {

    private interface Extras {
        String THREAD_ID = "thread_id";
    }

    public static Intent newIntent(final Context context, final String threadId) {
        final Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(Extras.THREAD_ID, threadId);
        return intent;
    }

    public static void newInstance(final Context context, final String threadId) {
        final Intent intent = newIntent(context, threadId);
        context.startActivity(intent);
    }

    private final FormatHelper mFormatHelper = new FormatHelper.Default();
    private final NotificationHelper mNotificationHelper = new NotificationHelper.Default();

    private ArcaDispatcher mDispatcher;

    private String mThreadId;
    private int mThreadState;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);

        mThreadId = getIntent().getStringExtra(Extras.THREAD_ID);

        if (TextUtils.isEmpty(mThreadId)) {
            Toast.makeText(this, "Thread ID cannot be null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            getMessageSendFragment().setThreadId(mThreadId);
            getConversationFragment().setThreadId(mThreadId);
        }

        mDispatcher = ArcaDispatcherFactory.generateDispatcher(this);
        mDispatcher.setRequestMonitor(new ParticipantsMonitor());
        mDispatcher.execute(new ParticipantsQuery(mThreadId), this);
    }

    @Override
    public void onRequestComplete(final QueryResult result) {
        final Cursor cursor = result.getData();

        if (cursor != null && cursor.moveToFirst()) {
            final String threadName = cursor.getString(cursor.getColumnIndex(ParticipantView.Columns.THREAD_NAME));
            final String userNames = cursor.getString(cursor.getColumnIndex(ParticipantView.Columns.USER_NAMES));

            mThreadState = cursor.getInt(cursor.getColumnIndex(ParticipantView.Columns.THREAD_STATE));

            setTitle(mFormatHelper.getThreadName(threadName, userNames));
        }
    }

    private MessageSendFragment getMessageSendFragment() {
        final FragmentManager manager = getFragmentManager();
        return (MessageSendFragment) manager.findFragmentById(R.id.fragment_message_send);
    }

    private ConversationFragment getConversationFragment() {
        final FragmentManager manager = getFragmentManager();
        return (ConversationFragment) manager.findFragmentById(R.id.fragment_conversation);
    }

    public void clearNotifications() {
        mNotificationHelper.cancelNotification(this, mThreadId);
    }

    private void muteConversation(final boolean isChecked) {
        final int state = !isChecked ? ThreadTable.State.MUTED : ThreadTable.State.DEFAULT;
        mDispatcher.execute(new ThreadUpdate(mThreadId, state));
    }

    private void leaveConversation() {
        final String regId = VoltagePreferences.getRegId(this);
        mDispatcher.execute(new MessageInsert(regId, mThreadId, GcmPayload.Type.USER_LEFT.name(), regId, GcmPayload.Type.USER_LEFT));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_conversation, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final MenuItem item = menu.findItem(R.id.menu_mute);
        item.setChecked(mThreadState == ThreadTable.State.MUTED);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_mute:
                muteConversation(item.isChecked());
                return true;

            case R.id.menu_members_add:
                MembersAddActivity.newInstance(this, mThreadId);
                return true;

            case R.id.menu_members_remove:
                MembersRemoveActivity.newInstance(this, mThreadId);
                return true;

            case R.id.menu_view_members:
                MembersActivity.newInstance(this, mThreadId);
                return true;

            case R.id.menu_rename:
                ConversationEditActivity.newInstance(this, mThreadId);
                return true;

            case R.id.menu_leave:
                leaveConversation();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}