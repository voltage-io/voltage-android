package io.voltage.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleRecyclerViewFragment;
import io.voltage.app.R;
import io.voltage.app.activities.ConversationActivity;
import io.voltage.app.activities.ConversationEditActivity;
import io.voltage.app.application.VoltageContentProvider.InboxView;
import io.voltage.app.binders.InboxViewBinder;
import io.voltage.app.monitors.InboxMonitor;
import io.voltage.app.requests.InboxQuery;
import io.voltage.app.requests.ThreadMessagesDelete;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_inbox,
    adapterItemLayout = R.layout.list_item_inbox,
    binder = InboxViewBinder.class,
    monitor = InboxMonitor.class
)
public class InboxFragment extends ArcaSimpleRecyclerViewFragment {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
        new Binding(R.id.inbox_letter, InboxView.Columns.THREAD_NAME),
        new Binding(R.id.inbox_user_name, InboxView.Columns.THREAD_NAME),
        new Binding(R.id.inbox_message_text, InboxView.Columns.MESSAGE_TEXT),
        new Binding(R.id.inbox_message_timestamp, InboxView.Columns.MESSAGE_TIMESTAMP)
    );

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        execute(new InboxQuery());
    }

    @Override
    public void onItemClick(final RecyclerView recyclerView, final View view, final int position, final long id) {
        final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
        final String threadId = cursor.getString(cursor.getColumnIndex(InboxView.Columns.THREAD_ID));

        ConversationActivity.newInstance(getActivity(), threadId);
    }

    @Override
    public void onItemLongClick(final RecyclerView recyclerView, final View view, final int position, final long id) {
        ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(40);

        showActionsDialog(position);
    }

    private boolean showActionsDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.inbox_actions, new ConversationClickListener(position));
        builder.setTitle(R.string.title_select_action);
        builder.create().show();
        return true;
    }



    private final class ConversationClickListener implements DialogInterface.OnClickListener {

        private final int mPosition;

        public ConversationClickListener(final int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            if (which == 0) {
                renameThread(mPosition);
            } else if (which == 1) {
                deleteThread(mPosition);
            }
            dialog.dismiss();
        }

        private void deleteThread(final int position) {
            final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
            final String threadId = cursor.getString(cursor.getColumnIndex(InboxView.Columns.THREAD_ID));
            getRequestDispatcher().execute(new ThreadMessagesDelete(threadId));
        }

        private void renameThread(final int position) {
            final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
            final String threadId = cursor.getString(cursor.getColumnIndex(InboxView.Columns.THREAD_ID));
            ConversationEditActivity.newInstance(getActivity(), threadId);
        }
    }
}