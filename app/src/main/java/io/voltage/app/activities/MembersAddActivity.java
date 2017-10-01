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
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.monitors.MembersAddMonitor;
import io.voltage.app.requests.NonMembersQuery;
import io.voltage.app.requests.ThreadUserInsertBatch;

public class MembersAddActivity extends FragmentActivity {

	private interface Extras {
		String THREAD_ID = "thread_id";
	}

    public static void newInstance(final Context context, final String threadId) {
        final Intent intent = new Intent(context, MembersAddActivity.class);
		intent.putExtra(Extras.THREAD_ID, threadId);
        context.startActivity(intent);
    }

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_members_add);
		setTitle(R.string.title_members_add);

		final String threadId = getIntent().getStringExtra(Extras.THREAD_ID);

		if (TextUtils.isEmpty(threadId)) {
			Toast.makeText(this, "Thread ID cannot be null", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			getFragment().setThreadId(threadId);
		}
	}

	private MembersAddFragment getFragment() {
		final FragmentManager manager = getFragmentManager();
		return (MembersAddFragment) manager.findFragmentById(R.id.fragment_members_add);
	}

	@ArcaFragment(
        fragmentLayout = R.layout.fragment_members_add,
        adapterItemLayout = R.layout.list_item_member,
        monitor = MembersAddMonitor.class
    )
    public static class MembersAddFragment extends ArcaSimpleAdapterFragment implements View.OnClickListener {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.user_name, UserTable.Columns.NAME)
        );

        private String mThreadId;
        private Button mEmptyButton;

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mEmptyButton = (Button) view.findViewById(R.id.empty_button);
            mEmptyButton.setOnClickListener(this);
        }

        @Override
        public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
            final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            final String userId = cursor.getString(cursor.getColumnIndex(UserTable.Columns.REG_ID));
            final String senderId = VoltagePreferences.getRegId(getActivity());

            execute(new ThreadUserInsertBatch(mThreadId, senderId, userId));
        }

        public void setThreadId(final String threadId) {
            if (threadId != null) {
                execute(new NonMembersQuery(mThreadId = threadId));
            }
        }

        @Override
        public void onClick(final View view) {
            if (view == mEmptyButton) {
                UserNewActivity.newInstance(getActivity());
            }
        }
    }
}