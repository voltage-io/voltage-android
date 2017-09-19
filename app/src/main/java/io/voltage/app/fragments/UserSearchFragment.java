package io.voltage.app.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleRecyclerViewFragment;
import io.voltage.app.R;
import io.voltage.app.activities.UserAddParamsActivity;
import io.voltage.app.application.VoltageContentProvider.UserSearchView;
import io.voltage.app.helpers.SearchHelper;
import io.voltage.app.monitors.UserListMonitor;
import io.voltage.app.requests.UserSearchQuery;

@ArcaFragment(
    fragmentLayout = R.layout.fragment_user_search,
    adapterItemLayout = R.layout.list_item_user,
    monitor = UserListMonitor.class
)
public class UserSearchFragment extends ArcaSimpleRecyclerViewFragment implements SearchView.OnQueryTextListener {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
        new Binding(R.id.user_name, UserSearchView.Columns.LOOKUP)
    );

    @Override
    public void onItemClick(final RecyclerView recyclerView, final View view, final int position, final long id) {
        final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
        final String regId = cursor.getString(cursor.getColumnIndex(UserSearchView.Columns.REG_ID));

        UserAddParamsActivity.newInstance(getActivity(), regId);
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
        execute(new UserSearchQuery(text));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        return false;
    }
}