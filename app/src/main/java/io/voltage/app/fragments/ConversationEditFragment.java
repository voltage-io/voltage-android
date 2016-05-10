package io.voltage.app.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleItemFragment;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.monitors.ConversationEditMonitor;
import io.voltage.app.requests.MessageInsert;
import io.voltage.app.requests.ThreadQuery;
import io.voltage.app.requests.ThreadUpdate;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_conversation_edit,
    monitor = ConversationEditMonitor.class
)
public class ConversationEditFragment extends ArcaSimpleItemFragment implements View.OnClickListener {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
        new Binding(R.id.conversation_edit_name, UserTable.Columns.NAME)
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
            mThreadId = id;
            execute(new ThreadQuery(mThreadId));
        }
    }

    @Override
    public void onClick(final View view) {
        final String name = mNameView.getText().toString();
        final String senderId = VoltagePreferences.getRegId(getActivity());

        final ArcaDispatcher dispatcher = getRequestDispatcher();
        dispatcher.execute(new ThreadUpdate(name, mThreadId));
        dispatcher.execute(new MessageInsert(senderId, mThreadId, GcmPayload.Type.THREAD_RENAMED.name(), name, GcmPayload.Type.THREAD_RENAMED));

        getActivity().finish();
    }
}