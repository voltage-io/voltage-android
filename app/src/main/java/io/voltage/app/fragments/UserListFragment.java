package io.voltage.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.voltage.app.R;
import io.voltage.app.activities.UserEditActivity;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.helpers.SearchHelper;
import io.voltage.app.monitors.UserListMonitor;
import io.voltage.app.requests.UserDelete;
import io.voltage.app.requests.UserQuery;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_user_list,
    adapterItemLayout = R.layout.list_item_user,
    monitor = UserListMonitor.class
)
public class UserListFragment extends ArcaSimpleAdapterFragment implements SearchView.OnQueryTextListener {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
        new Binding(R.id.user_name, UserTable.Columns.NAME)
    );

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        final String regId = cursor.getString(cursor.getColumnIndex(UserTable.Columns.REG_ID));

        UserEditActivity.newInstance(getActivity(), regId);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(40);

        return showActionsDialog(position);
    }

    private boolean showActionsDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.user_list_actions, new UserClickListener(position));
        builder.setTitle(R.string.title_select_action);
        builder.create().show();
        return true;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        onQueryTextChange("");
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_list, menu);

        new SearchHelper().styleSearchView(menu, this);
    }

    @Override
    public boolean onQueryTextChange(final String text) {
        execute(new UserQuery(text, UserTable.Columns.NAME));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        return false;
    }

    private final class UserClickListener implements DialogInterface.OnClickListener {

        private final int mPosition;

        public UserClickListener(final int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            if (which == 0) {
                deleteUser(mPosition);
            }
            dialog.dismiss();
        }

        private void deleteUser(final int position) {
            final Cursor cursor = (Cursor) getAdapterView().getItemAtPosition(position);
            final String regId = cursor.getString(cursor.getColumnIndex(UserTable.Columns.REG_ID));
            getRequestDispatcher().execute(new UserDelete(regId));
        }
    }
}