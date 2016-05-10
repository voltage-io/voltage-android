package io.voltage.app.binders;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.adapters.ViewBinder;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.MemberView;

public class MembersViewBinder implements ViewBinder {

    @Override
    public boolean setViewValue(final View view, final Cursor cursor, final Binding binding) {
        if (view.getId() == R.id.user_name) {
            return setUserNameViewValue((TextView) view, cursor, binding);

        } else {
            return false;
        }
    }

    private boolean setUserNameViewValue(final TextView view, final Cursor cursor, final Binding binding) {
        final String name = cursor.getString(binding.getColumnIndex());
        final String regId = cursor.getString(cursor.getColumnIndex(MemberView.Columns.USER_ID));

        view.setText(!TextUtils.isEmpty(name) ? name : regId);
        return true;
    }

}
