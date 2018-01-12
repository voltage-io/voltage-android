package io.voltage.app.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.dispatcher.QueryListener;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.pivotal.arca.fragments.ArcaSimpleDispatcherFragment;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.pivotal.arca.provider.DataUtils;
import io.pivotal.arca.service.OperationService;
import io.voltage.app.R;
import io.voltage.app.adapters.ConversationAdapter;
import io.voltage.app.adapters.ConversationAdapter.ViewType;
import io.voltage.app.application.VoltageContentProvider.ConversationView;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.application.VoltageContentProvider.ParticipantView;
import io.voltage.app.application.VoltageContentProvider.ParticipantView.Columns;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.binders.ConversationViewBinder;
import io.voltage.app.helpers.FormatHelper;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.monitors.ConversationMonitor;
import io.voltage.app.monitors.MessageSendMonitor;
import io.voltage.app.monitors.ParticipantsMonitor;
import io.voltage.app.operations.ChecksumOperation;
import io.voltage.app.requests.ConversationQuery;
import io.voltage.app.requests.MessageDelete;
import io.voltage.app.requests.MessageInsert;
import io.voltage.app.requests.MessageUpdate;
import io.voltage.app.requests.ParticipantsQuery;
import io.voltage.app.requests.ThreadColorUpdate;
import io.voltage.app.requests.ThreadUpdate;
import io.voltage.app.utils.AnimUtils;

public class ConversationActivity extends ColorActivity implements QueryListener {

    private static final int REQUEST_COLOR = 1000;
    private static final int REQUEST_IMAGE = 10000;

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

    private ArcaDispatcher mDispatcher;

    private String mThreadId;
    private int mThreadState;

	@Override
    @AddTrace(name = "ConversationActivity:onCreate")
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

            mDispatcher = ArcaDispatcherFactory.generateDispatcher(this);
            mDispatcher.setRequestMonitor(new ParticipantsMonitor());
            mDispatcher.execute(new ParticipantsQuery(mThreadId), this);

            OperationService.start(this, new ChecksumOperation(mThreadId));
        }
    }

    @Override
    @AddTrace(name = "ConversationActivity:onRequestComplete")
    public void onRequestComplete(final QueryResult result) {
        final Cursor cursor = result.getData();

        if (cursor != null && cursor.moveToFirst()) {
            final String threadName = cursor.getString(cursor.getColumnIndex(ParticipantView.Columns.THREAD_NAME));
            final String userNames = cursor.getString(cursor.getColumnIndex(ParticipantView.Columns.USER_NAMES));
            final String color = cursor.getString(cursor.getColumnIndex(Columns.THREAD_COLOR));

            mThreadState = cursor.getInt(cursor.getColumnIndex(ParticipantView.Columns.THREAD_STATE));

            setTitle(mFormatHelper.getThreadName(threadName, userNames));
            setColor(mFormatHelper.getThreadColor(color));
        }
    }

    private void setColor(final String color) {
        updateColor(color);
        getConversationFragment().setColor(color);
    }

    private MessageSendFragment getMessageSendFragment() {
        final FragmentManager manager = getFragmentManager();
        return (MessageSendFragment) manager.findFragmentById(R.id.fragment_message_send);
    }

    private ConversationFragment getConversationFragment() {
        final FragmentManager manager = getFragmentManager();
        return (ConversationFragment) manager.findFragmentById(R.id.fragment_conversation);
    }

    @AddTrace(name = "ConversationActivity:muteConversation")
    private void muteConversation(final boolean isChecked) {
        final int state = !isChecked ? ThreadTable.State.MUTED : ThreadTable.State.DEFAULT;
        mDispatcher.execute(new ThreadUpdate(mThreadId, state));
    }

    @AddTrace(name = "ConversationActivity:leaveConversation")
    private void leaveConversation() {
        final String regId = VoltagePreferences.getRegId(this);
        mDispatcher.execute(new MessageInsert(mThreadId, regId, GcmPayload.Type.USER_LEFT.name(), regId, GcmPayload.Type.USER_LEFT));
        finish();
    }

    @AddTrace(name = "ConversationActivity:insertImage")
    private void insertImage(final String url) {
        final String regId = VoltagePreferences.getRegId(this);
        mDispatcher.execute(new MessageInsert(mThreadId, regId, url, null, GcmPayload.Type.MESSAGE));
    }

    @AddTrace(name = "ConversationActivity:changeColor")
    private void changeColor(final String color) {
        mDispatcher.execute(new ThreadColorUpdate(mThreadId, color));
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

            case R.id.menu_image_search:
                ImageSearchActivity.newInstance(this, REQUEST_IMAGE);
                return true;

            case R.id.menu_color_change:
                ColorSelectionActivity.newInstance(this, REQUEST_COLOR);
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            insertImage(ImageSearchActivity.extractImageUrl(data));
        }

	    if (requestCode == REQUEST_COLOR && resultCode == RESULT_OK) {
            changeColor(ColorSelectionActivity.extractColor(data));
        }
    }

    @ArcaFragment(
        fragmentLayout = R.layout.fragment_conversation,
        monitor = ConversationMonitor.class
    )
    public static class ConversationFragment extends ArcaSimpleAdapterFragment {

        private static final Collection<Binding> BINDINGS = Arrays.asList(
            ViewType.ACTION.newBinding(R.id.message_text, ConversationView.Columns.TEXT),
            ViewType.ACTION.newBinding(R.id.message_timestamp, ConversationView.Columns.TIMESTAMP),
            ViewType.ACTION.newBinding(R.id.message_metadata, ConversationView.Columns.METADATA),

            ViewType.MESSAGE.newBinding(R.id.message_text, ConversationView.Columns.TEXT),
            ViewType.MESSAGE.newBinding(R.id.message_letter, ConversationView.Columns.SENDER_NAME),
            ViewType.MESSAGE.newBinding(R.id.message_user_name, ConversationView.Columns.SENDER_NAME),
            ViewType.MESSAGE.newBinding(R.id.message_timestamp, ConversationView.Columns.TIMESTAMP),
            ViewType.MESSAGE.newBinding(R.id.message_state, ConversationView.Columns._STATE),

            ViewType.IMAGE.newBinding(R.id.message_image, ConversationView.Columns.TEXT),
            ViewType.IMAGE.newBinding(R.id.message_letter, ConversationView.Columns.SENDER_NAME),
            ViewType.IMAGE.newBinding(R.id.message_user_name, ConversationView.Columns.SENDER_NAME),
            ViewType.IMAGE.newBinding(R.id.message_timestamp, ConversationView.Columns.TIMESTAMP)
        );

        @Override
        public CursorAdapter onCreateAdapter(final AdapterView<CursorAdapter> adapterView, final Bundle savedInstanceState) {
            final ConversationAdapter adapter = new ConversationAdapter(getActivity(), BINDINGS);
            adapter.setViewBinder(new ConversationViewBinder(getActivity()));
            return adapter;
        }

        @Override
        public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
            // AnimUtils.fadeIn(view.findViewById(R.id.message_info));
            AnimUtils.animate(view.findViewById(R.id.message_image));
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
            final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            final String senderId = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.SENDER_ID));

            return showActionsDialog(position, senderId);
        }

        private boolean showActionsDialog(final int position, final String senderId) {
            final String regId = VoltagePreferences.getRegId(getActivity());

            final int actions = senderId.equals(regId) ? R.array.conversation_full_actions : R.array.conversation_actions;

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(actions, new MessageClickListener(position));
            builder.setTitle(R.string.title_action_select);
            builder.create().show();
            return true;
        }

        public void setThreadId(final String threadId) {
            if (threadId != null) {
                execute(new ConversationQuery(threadId));
            }
        }

        public void setColor(final String color) {
            if (color != null) {
                getCursorAdapter().setColor(color);
            }
        }

        @Override
        public ConversationAdapter getCursorAdapter() {
            return (ConversationAdapter) super.getCursorAdapter();
        }

        private final class MessageClickListener implements DialogInterface.OnClickListener {

            private final int mPosition;

            public MessageClickListener(final int position) {
                mPosition = position;
            }

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                if (which == 0) {
                    copyTextToClipboard(mPosition);
                } else if (which == 1) {
                    deleteMessage(mPosition);
                } else if (which == 2) {
                    resendMessage(mPosition);
                }
                dialog.dismiss();
            }

            @AddTrace(name =  "ConversationActivity:copyTextToClipboard")
            private void copyTextToClipboard(final int position) {
                final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
                final String text = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.TEXT));
                final ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);

                clipboard.setPrimaryClip(ClipData.newPlainText("text", text));
            }

            @AddTrace(name = "ConversationActivity:deleteMessage")
            private void deleteMessage(final int position) {
                final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
                final String uuid = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.MSG_UUID));

                execute(new MessageDelete(uuid));
            }

            @AddTrace(name = "ConversationActivity:resendMessage")
            private void resendMessage(final int position) {
                final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
                final ContentValues values = DataUtils.getContentValues(cursor, MessageTable.class, position);
                values.put(MessageTable.Columns._STATE, MessageTable.State.SENDING);
                values.remove(MessageTable.Columns._ID);

                execute(new MessageUpdate(values));
            }
        }
    }

    @ArcaFragment(
            fragmentLayout = R.layout.fragment_message_send,
            monitor = MessageSendMonitor.class
    )
    public static class MessageSendFragment extends ArcaSimpleDispatcherFragment implements View.OnClickListener {

        private View mSendButton;
        private View mEmojiButton;
        private EditText mMessageView;
        private String mThreadId;

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mMessageView = (EditText) view.findViewById(R.id.message_send_text);

            mSendButton = view.findViewById(R.id.message_send_action);
            mSendButton.setOnClickListener(this);

            mEmojiButton = view.findViewById(R.id.message_emojis);
            mEmojiButton.setOnClickListener(this);
        }

        public void setThreadId(final String threadId) {
            mThreadId = threadId;
        }

        @Override
        public void onClick(final View view) {
            if (view == mSendButton) {
                sendMessage();

            } else if (view == mEmojiButton) {
                showEmojis();
            }
        }

        @AddTrace(name = "ConversationActivity:sendMessage")
        private void sendMessage() {
            if (mMessageView.getText().length() > 0) {
                final String text = mMessageView.getText().toString();
                final String senderId = VoltagePreferences.getRegId(getActivity());

                execute(new MessageInsert(mThreadId, senderId, text, null, GcmPayload.Type.MESSAGE));

                mMessageView.getText().clear();
            }
        }

        @AddTrace(name = "ConversationActivity:showEmojis")
        public void showEmojis() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.emojicons, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final String[] emojis = getResources().getStringArray(R.array.emojicons);

                    mMessageView.append(emojis[which]);
                }
            });
            builder.create().show();
        }
    }
}