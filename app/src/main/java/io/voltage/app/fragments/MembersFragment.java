package io.voltage.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.voltage.app.R;
import io.voltage.app.activities.UserAddParamsActivity;
import io.voltage.app.application.VoltageContentProvider.MemberView;
import io.voltage.app.binders.MembersViewBinder;
import io.voltage.app.requests.MembersQuery;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_members,
    adapterItemLayout = R.layout.list_item_member,
    binder = MembersViewBinder.class
)
public class MembersFragment extends ArcaSimpleAdapterFragment {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
        new Binding(R.id.user_name, MemberView.Columns.USER_NAME)
    );

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        final String name = cursor.getString(cursor.getColumnIndex(MemberView.Columns.USER_NAME));

        return !TextUtils.isEmpty(name) || showActionsDialog(position);
    }

    private boolean showActionsDialog(final int position) {
        ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(40);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.members_actions, new MemberClickListener(position));
        builder.setTitle(R.string.title_select_action);
        builder.create().show();
        return true;
    }

    public void setThreadId(final String threadId) {
        if (threadId != null) {
            execute(new MembersQuery(threadId));
        }
    }

    private final class MemberClickListener implements DialogInterface.OnClickListener {

        private final int mPosition;

        public MemberClickListener(final int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            if (which == 0) {
                addFriend(mPosition);
            }
            dialog.dismiss();
        }

        private void addFriend(final int position) {
            final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
            final String userId = cursor.getString(cursor.getColumnIndex(MemberView.Columns.USER_ID));

            UserAddParamsActivity.newInstance(getActivity(), userId);
        }
    }
}