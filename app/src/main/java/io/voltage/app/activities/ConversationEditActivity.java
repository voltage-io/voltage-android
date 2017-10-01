package io.voltage.app.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleItemFragment;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.monitors.ConversationEditMonitor;
import io.voltage.app.requests.ThreadQuery;
import io.voltage.app.requests.ThreadUpdateBatch;

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

    @ArcaFragment(
        fragmentLayout = R.layout.fragment_conversation_edit,
        monitor = ConversationEditMonitor.class
    )
    public static class ConversationEditFragment extends ArcaSimpleItemFragment implements View.OnClickListener {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.conversation_edit_name, VoltageContentProvider.UserTable.Columns.NAME)
        );

        private Button mEditConversation;
        private TextView mNameView;
        private String mThreadId;

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mEditConversation = (Button) view.findViewById(R.id.conversation_edit_button);
            mEditConversation.setOnClickListener(this);

            mNameView = (TextView) view.findViewById(R.id.conversation_edit_name);
        }

        public void setThreadId(final String id) {
            if (id != null) {
                execute(new ThreadQuery(mThreadId = id));
            }
        }

        @Override
        public void onClick(final View view) {
            final String name = mNameView.getText().toString();
            final String senderId = VoltagePreferences.getRegId(getActivity());

            execute(new ThreadUpdateBatch(mThreadId, senderId, name));

            getActivity().finish();
        }
    }
}