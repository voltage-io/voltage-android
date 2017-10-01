package io.voltage.app.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import io.voltage.app.application.VoltageContentProvider.MemberView;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.monitors.MembersRemoveMonitor;
import io.voltage.app.requests.MembersQuery;
import io.voltage.app.requests.ThreadUserDeleteBatch;

public class MembersRemoveActivity extends FragmentActivity {

	private interface Extras {
		String THREAD_ID = "thread_id";
	}

    public static void newInstance(final Context context, final String threadId) {
        final Intent intent = new Intent(context, MembersRemoveActivity.class);
		intent.putExtra(Extras.THREAD_ID, threadId);
        context.startActivity(intent);
    }

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_members_remove);
		setTitle(R.string.title_members_remove);

		final String threadId = getIntent().getStringExtra(Extras.THREAD_ID);

		if (TextUtils.isEmpty(threadId)) {
			Toast.makeText(this, "Thread ID cannot be null", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			getFragment().setThreadId(threadId);
		}
	}

	private MembersRemoveFragment getFragment() {
		final FragmentManager manager = getFragmentManager();
		return (MembersRemoveFragment) manager.findFragmentById(R.id.fragment_members_remove);
	}

    @ArcaFragment(
        fragmentLayout = R.layout.fragment_members_remove,
        adapterItemLayout = R.layout.list_item_member,
        monitor = MembersRemoveMonitor.class
    )
    public static class MembersRemoveFragment extends ArcaSimpleAdapterFragment {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.user_name, VoltageContentProvider.MemberView.Columns.USER_NAME)
        );

        private String mThreadId;

        @Override
        public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
            final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            final String userId = cursor.getString(cursor.getColumnIndex(MemberView.Columns.USER_ID));
            final String senderId = VoltagePreferences.getRegId(getActivity());

            execute(new ThreadUserDeleteBatch(mThreadId, senderId, userId));
        }

        public void setThreadId(final String threadId) {
            if (threadId != null) {
                execute(new MembersQuery(mThreadId = threadId));
            }
        }
    }
}