package io.voltage.app.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.voltage.app.R;
import io.voltage.app.activities.ConversationNewActivity;
import io.voltage.app.activities.UserNewActivity;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.requests.UserQuery;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_conversation_new_users,
    adapterItemLayout = R.layout.list_item_user_select
)
public class ConversationNewUsersFragment extends ArcaSimpleAdapterFragment implements View.OnClickListener {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
        new Binding(R.id.user_name, UserTable.Columns.NAME)
    );

    private final Set<String> mRegIds = new HashSet<String>();

    private Button mEmptyButton;
    private Button mNextButton;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AbsListView listView = (AbsListView) view.findViewById(getAdapterViewId());
        listView.setMultiChoiceModeListener(new ConversationChoiceListener());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        mEmptyButton = (Button) view.findViewById(R.id.empty_button);
        mEmptyButton.setOnClickListener(this);

        mNextButton = (Button) view.findViewById(R.id.conversation_create);
        mNextButton.setOnClickListener(this);

        execute(new UserQuery());
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        final String regId = cursor.getString(cursor.getColumnIndex(UserTable.Columns.REG_ID));

        final boolean checked = toggleChecked((AbsListView) adapterView, position);
        final boolean areAnyChecked = updateRegIds(regId, checked);

        mNextButton.setEnabled(areAnyChecked);
    }

    private boolean toggleChecked(final AbsListView listView, final int position) {
        final boolean checked = !listView.isItemChecked(position);
        listView.setItemChecked(position, checked);
        return checked;
    }

    private boolean updateRegIds(final String regId, final boolean checked) {
        if (checked) {
            mRegIds.add(regId);
        } else {
            mRegIds.remove(regId);
        }

        return mRegIds.size() > 0;
    }

    @Override
    public void onClick(final View view) {
        if (view == mEmptyButton) {
            UserNewActivity.newInstance(getActivity());

        } else if (view == mNextButton) {
            ((ConversationNewActivity) getActivity()).proceed();
        }
    }

    public Set<String> getRegistrationIds() {
        return mRegIds;
    }

    private static final class ConversationChoiceListener implements AbsListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(final ActionMode mode, final int position, final long id, final boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {

        }
    }
}