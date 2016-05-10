package io.voltage.app.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import io.voltage.app.R;
import io.voltage.app.fragments.ConversationEditFragment;

public class ConversationEditActivity extends ColorActivity {

    private interface Extras {
        String THREAD_ID = "thread_id";
    }

	public static void newInstance(final Context context, final String threadId) {
        final Intent intent = new Intent(context, ConversationEditActivity.class);
        intent.putExtra(Extras.THREAD_ID, threadId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_edit);
        setTitle(R.string.title_conversation_edit);

        final String threadId = getIntent().getStringExtra(Extras.THREAD_ID);

        if (TextUtils.isEmpty(threadId)) {
            Toast.makeText(this, "Thread ID cannot be null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            getFragment().setThreadId(threadId);
        }
    }

    private ConversationEditFragment getFragment() {
        final FragmentManager manager = getFragmentManager();
        return (ConversationEditFragment) manager.findFragmentById(R.id.fragment_conversation_edit);
    }
}