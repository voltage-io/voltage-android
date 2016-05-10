package io.voltage.app.fragments;

import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.MemberView;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.monitors.MembersRemoveMonitor;
import io.voltage.app.requests.MembersQuery;
import io.voltage.app.requests.MessageInsert;
import io.voltage.app.requests.ThreadUserDelete;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_members_remove,
    adapterItemLayout = R.layout.list_item_member,
    monitor = MembersRemoveMonitor.class
)
public class MembersRemoveFragment extends ArcaSimpleAdapterFragment {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
        new Binding(R.id.user_name, MemberView.Columns.USER_NAME)
    );

    private String mThreadId;

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        final String userId = cursor.getString(cursor.getColumnIndex(MemberView.Columns.USER_ID));
        final String senderId = VoltagePreferences.getRegId(getActivity());

        final ArcaDispatcher dispatcher = getRequestDispatcher();
        dispatcher.execute(new ThreadUserDelete(mThreadId, userId));
        dispatcher.execute(new MessageInsert(senderId, mThreadId, GcmPayload.Type.USER_REMOVED.name(), userId, GcmPayload.Type.USER_REMOVED));
    }

    public void setThreadId(final String threadId) {
        mThreadId = threadId;

        execute(new MembersQuery(mThreadId));
    }
}