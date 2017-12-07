package io.voltage.app.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collection;
import java.util.Collections;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleRecyclerViewFragment;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.CrashTable;
import io.voltage.app.requests.CrashDelete;
import io.voltage.app.requests.CrashQuery;

public class CrashListActivity extends ColorActivity {


    public static void newInstance(final Context context) {
        final Intent intent = new Intent(context, CrashListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_list);
    }

    @ArcaFragment(
            fragmentLayout = R.layout.fragment_crash_list,
            adapterItemLayout = R.layout.list_item_crash
    )
    public static class CrashListFragment extends ArcaSimpleRecyclerViewFragment {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Collections.singletonList(
                new Binding(R.id.crash_trace, CrashTable.Columns.TRACE)
        );

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            execute(new CrashQuery());
        }

        @Override
        public void onItemLongClick(final RecyclerView recyclerView, final View view, final int position, final long id) {

            final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
            final String crashId = cursor.getString(cursor.getColumnIndex(CrashTable.Columns.ID));

            execute(new CrashDelete(crashId));
        }
    }
}