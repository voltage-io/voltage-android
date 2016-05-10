package io.voltage.app.binders;

import android.database.Cursor;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ViewBinder;
import io.pivotal.arca.utils.StringUtils;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.InboxView;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.helpers.FormatHelper;

public class InboxViewBinder implements ViewBinder {

    private final FormatHelper mFormatHelper = new FormatHelper();

    @Override
    public boolean setViewValue(final View view, final Cursor cursor, final Binding binding) {
        if (view.getId() == R.id.inbox_letter) {
            return setInboxLetter((TextView) view, cursor, binding);

        } else if (view.getId() == R.id.inbox_message_timestamp) {
            return setInboxTimestamp((TextView) view, cursor, binding);

        } else if (view.getId() == R.id.inbox_user_name) {
            return setInboxThreadName((TextView) view, cursor, binding);

        } else if (view.getId() == R.id.inbox_message_text) {
            return setInboxMessage((TextView) view, cursor, binding);

        } else {
            return false;
        }
    }

    private boolean setInboxLetter(final TextView view, final Cursor cursor, final Binding binding) {
        final String threadName = cursor.getString(binding.getColumnIndex());
        final String userNames = cursor.getString(cursor.getColumnIndex(InboxView.Columns.USER_NAMES));
        final String name = mFormatHelper.getThreadName(threadName, userNames);

        final String left = StringUtils.left(name, 1);
        view.setText(TextUtils.isEmpty(left) ? "?" : left);
        return true;
    }

    private boolean setInboxTimestamp(final TextView view, final Cursor cursor, final Binding binding) {
        final String timestamp = cursor.getString(binding.getColumnIndex());
        if (timestamp != null) {
            view.setText(DateUtils.getRelativeTimeSpanString(view.getContext(), Long.parseLong(timestamp)));
        }
        return true;
    }

    private boolean setInboxThreadName(final TextView view, final Cursor cursor, final Binding binding) {
        final String threadName = cursor.getString(binding.getColumnIndex());
        final String userNames = cursor.getString(cursor.getColumnIndex(InboxView.Columns.USER_NAMES));
        final int state = cursor.getInt(cursor.getColumnIndex(InboxView.Columns.MESSAGE_STATE));

        view.setText(mFormatHelper.getThreadName(threadName, userNames));
        setTypeface(view, state);
        return true;
    }

    private boolean setInboxMessage(final TextView view, final Cursor cursor, final Binding binding) {
        final String text = cursor.getString(binding.getColumnIndex());
        final int state = cursor.getInt(cursor.getColumnIndex(InboxView.Columns.MESSAGE_STATE));

        view.setText(text);
        setTypeface(view, state);
        return true;
    }

    private void setTypeface(final TextView view, final int state) {
        if (state == MessageTable.State.UNREAD) {
            view.setTypeface(null, Typeface.BOLD);
        } else {
            view.setTypeface(null, Typeface.NORMAL);
        }
    }
}
