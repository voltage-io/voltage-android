package io.voltage.app.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import io.voltage.app.R;
import io.voltage.app.fragments.MembersFragment;

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
}