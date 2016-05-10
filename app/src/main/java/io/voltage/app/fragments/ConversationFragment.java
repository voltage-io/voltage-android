package io.voltage.app.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.pivotal.arca.provider.DataUtils;
import io.pivotal.arca.service.Operation;
import io.pivotal.arca.service.OperationService;
import io.voltage.app.R;
import io.voltage.app.activities.ConversationActivity;
import io.voltage.app.adapters.ConversationAdapter;
import io.voltage.app.adapters.ConversationAdapter.ViewType;
import io.voltage.app.application.VoltageContentProvider.ConversationView;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.binders.ConversationViewBinder;
import io.voltage.app.monitors.ConversationMonitor;
import io.voltage.app.operations.ChecksumOperation;
import io.voltage.app.requests.ConversationQuery;
import io.voltage.app.requests.MessageDelete;
import io.voltage.app.requests.MessageUpdate;
import io.voltage.app.utils.AnimUtils;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_conversation,
    monitor = ConversationMonitor.class
)
public class ConversationFragment extends ArcaSimpleAdapterFragment {

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
        ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(40);

        final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        final String senderId = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.SENDER_ID));

        return showActionsDialog(position, senderId);
    }

    private boolean showActionsDialog(final int position, final String senderId) {
        final String regId = VoltagePreferences.getRegId(getActivity());

        final int actions = senderId.equals(regId) ? R.array.conversation_full_actions : R.array.conversation_actions;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(actions, new MessageClickListener(position));
        builder.setTitle(R.string.title_select_action);
        builder.create().show();
        return true;
    }

    @Override
    public void onContentChanged(final QueryResult result) {
        super.onContentChanged(result);

        ((ConversationActivity) getActivity()).clearNotifications();
    }

    public void setThreadId(final String threadId) {
        if (threadId != null) {
            execute(new ConversationQuery(threadId));

            final Operation operation = new ChecksumOperation(threadId);
            OperationService.start(getActivity(), operation);
        }
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

        private void copyTextToClipboard(final int position) {
            final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
            final String text = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.TEXT));
            final ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("text", text));
        }

        private void deleteMessage(final int position) {
            final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
            final String uuid = cursor.getString(cursor.getColumnIndex(ConversationView.Columns.MSG_UUID));
            getRequestDispatcher().execute(new MessageDelete(uuid));
        }

        private void resendMessage(final int position) {
            final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
            final ContentValues values = DataUtils.getContentValues(cursor, MessageTable.class, position);
            values.put(MessageTable.Columns._STATE, MessageTable.State.SENDING);
            values.remove(MessageTable.Columns._ID);
            getRequestDispatcher().execute(new MessageUpdate(values));
        }
    }
}