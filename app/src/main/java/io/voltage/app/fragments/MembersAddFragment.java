package io.voltage.app.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.voltage.app.R;
import io.voltage.app.activities.UserNewActivity;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.monitors.MembersAddMonitor;
import io.voltage.app.requests.MessageInsert;
import io.voltage.app.requests.NonMembersQuery;
import io.voltage.app.requests.ThreadUserInsert;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_members_add,
    adapterItemLayout = R.layout.list_item_member,
    monitor = MembersAddMonitor.class
)
public class MembersAddFragment extends ArcaSimpleAdapterFragment implements View.OnClickListener {

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
        final String regId = cursor.getString(cursor.getColumnIndex(UserTable.Columns.REG_ID));
        final String senderId = VoltagePreferences.getRegId(getActivity());

        final ArcaDispatcher dispatcher = getRequestDispatcher();
        dispatcher.execute(new ThreadUserInsert(mThreadId, regId));
        dispatcher.execute(new MessageInsert(senderId, mThreadId, GcmPayload.Type.USER_ADDED.name(), regId, GcmPayload.Type.USER_ADDED));
    }

    public void setThreadId(final String threadId) {
        mThreadId = threadId;

        execute(new NonMembersQuery(mThreadId));
    }

    @Override
    public void onClick(final View view) {
        if (view == mEmptyButton) {
            UserNewActivity.newInstance(getActivity());
        }
    }
}