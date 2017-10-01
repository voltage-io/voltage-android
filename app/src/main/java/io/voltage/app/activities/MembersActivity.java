package io.voltage.app.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.binders.MembersViewBinder;
import io.voltage.app.requests.MembersQuery;

public class MembersActivity extends FragmentActivity {

	private interface Extras {
		String THREAD_ID = "thread_id";
	}

    public static void newInstance(final Context context, final String threadId) {
        final Intent intent = new Intent(context, MembersActivity.class);
		intent.putExtra(Extras.THREAD_ID, threadId);
        context.startActivity(intent);
    }

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_members);
		setTitle(R.string.title_members);

		final String threadId = getIntent().getStringExtra(Extras.THREAD_ID);

		if (TextUtils.isEmpty(threadId)) {
			Toast.makeText(this, "Thread ID cannot be null", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			getFragment().setThreadId(threadId);
		}
	}

	private MembersFragment getFragment() {
		final FragmentManager manager = getFragmentManager();
		return (MembersFragment) manager.findFragmentById(R.id.fragment_members);
	}

	@ArcaFragment(
        fragmentLayout = R.layout.fragment_members,
        adapterItemLayout = R.layout.list_item_member,
        binder = MembersViewBinder.class
    )
    public static class MembersFragment extends ArcaSimpleAdapterFragment {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.user_name, VoltageContentProvider.MemberView.Columns.USER_NAME)
        );

        @Override
        public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
            final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            final String name = cursor.getString(cursor.getColumnIndex(VoltageContentProvider.MemberView.Columns.USER_NAME));

            return !TextUtils.isEmpty(name) || showActionsDialog(position);
        }

        private boolean showActionsDialog(final int position) {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(40);

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
                final String userId = cursor.getString(cursor.getColumnIndex(VoltageContentProvider.MemberView.Columns.USER_ID));

                UserAddParamsActivity.newInstance(getActivity(), userId);
            }
        }
    }
}