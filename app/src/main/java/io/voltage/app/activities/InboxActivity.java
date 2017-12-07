package io.voltage.app.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleItemFragment;
import io.pivotal.arca.fragments.ArcaSimpleRecyclerViewFragment;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.InboxView;
import io.voltage.app.application.VoltageContentProvider.RegistrationTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.binders.InboxViewBinder;
import io.voltage.app.monitors.InboxMonitor;
import io.voltage.app.requests.InboxQuery;
import io.voltage.app.requests.RegistrationQuery;
import io.voltage.app.requests.ThreadMessagesDelete;
import io.voltage.app.utils.ColorUtils;

public class InboxActivity extends ColorActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    public static void newInstance(final Context context) {
		final Intent intent = new Intent(context, InboxActivity.class);
		context.startActivity(intent);
	}

	@Override
    @AddTrace(name = "InboxActivity:onCreate")
	protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		setTitle(R.string.title_inbox);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {

            @Override
            public void onDrawerStateChanged(final int newState) {
                super.onDrawerStateChanged(newState);
                invalidateOptionsMenu();
            }
        };

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    @AddTrace(name = "InboxActivity:onResume")
    protected void onResume() {
        super.onResume();

        final String color = VoltagePreferences.getPrimaryColour(this);
        final int darkColor = ColorUtils.darkenColor(color, 0.2f);

        findViewById(R.id.drawer_header).setBackgroundColor(darkColor);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    @AddTrace(name = "InboxActivity:onPause")
    protected void onPause() {
        super.onPause();

        mDrawerLayout.removeDrawerListener(mDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_add:
                ConversationNewActivity.newInstance(this);
                return true;

            case R.id.menu_friend_new:
                UserNewActivity.newInstance(this);
                return true;

            case R.id.menu_friend_list:
                UserListActivity.newInstance(this);
                return true;

            case R.id.menu_crash_list:
                CrashListActivity.newInstance(this);
                return true;

            case R.id.menu_settings:
                SettingsActivity.newInstance(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @ArcaFragment(
        fragmentLayout = R.layout.fragment_inbox,
        adapterItemLayout = R.layout.list_item_inbox,
        binder = InboxViewBinder.class,
        monitor = InboxMonitor.class
    )
    public static class InboxFragment extends ArcaSimpleRecyclerViewFragment {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.inbox_letter, InboxView.Columns.THREAD_NAME),
            new Binding(R.id.inbox_user_name, InboxView.Columns.THREAD_NAME),
            new Binding(R.id.inbox_message_text, InboxView.Columns.MESSAGE_TEXT),
            new Binding(R.id.inbox_message_timestamp, InboxView.Columns.MESSAGE_TIMESTAMP)
        );

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            execute(new InboxQuery());
        }

        @Override
        @AddTrace(name = "InboxActivity:onItemClick")
        public void onItemClick(final RecyclerView recyclerView, final View view, final int position, final long id) {
            final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
            final String threadId = cursor.getString(cursor.getColumnIndex(InboxView.Columns.THREAD_ID));

            ConversationActivity.newInstance(getActivity(), threadId);
        }

        @Override
        @AddTrace(name = "InboxActivity:onItemLongClick")
        public void onItemLongClick(final RecyclerView recyclerView, final View view, final int position, final long id) {
            ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(40);

            showActionsDialog(position);
        }

        private boolean showActionsDialog(final int position) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.inbox_actions, new ConversationClickListener(position));
            builder.setTitle(R.string.title_select_action);
            builder.create().show();
            return true;
        }

        private final class ConversationClickListener implements DialogInterface.OnClickListener {

            private final int mPosition;

            public ConversationClickListener(final int position) {
                mPosition = position;
            }

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                if (which == 0) {
                    renameThread(mPosition);
                } else if (which == 1) {
                    deleteThread(mPosition);
                }
                dialog.dismiss();
            }

            @AddTrace(name = "InboxActivity:deleteThread")
            private void deleteThread(final int position) {
                final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
                final String threadId = cursor.getString(cursor.getColumnIndex(InboxView.Columns.THREAD_ID));

                execute(new ThreadMessagesDelete(threadId));
            }

            @AddTrace(name = "InboxActivity:renameThread")
            private void renameThread(final int position) {
                final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
                final String threadId = cursor.getString(cursor.getColumnIndex(InboxView.Columns.THREAD_ID));

                ConversationEditActivity.newInstance(getActivity(), threadId);
            }
        }
    }

    @ArcaFragment(
            fragmentLayout = R.layout.fragment_registration
    )
    public static class RegistrationFragment extends ArcaSimpleItemFragment implements OnAccountsUpdateListener {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
                new Binding(R.id.registration_id, RegistrationTable.Columns.REG_ID),
                new Binding(R.id.registration_lookup, RegistrationTable.Columns.LOOKUP)
        );

        private TextView mRegistrationView;

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mRegistrationView = (TextView) view.findViewById(R.id.registration_name);
            mRegistrationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AccountActivity.newInstance(getActivity());
                }
            });

            mRegistrationView.setText(VoltagePreferences.getUserName(getActivity()));

            execute(new RegistrationQuery());
        }

        @Override
        public void onResume() {
            super.onResume();

            final AccountManager manager = AccountManager.get(getActivity());
            manager.addOnAccountsUpdatedListener(this, null, true);
        }

        @Override
        public void onPause() {
            super.onPause();

            final AccountManager manager = AccountManager.get(getActivity());
            manager.removeOnAccountsUpdatedListener(this);
        }

        @Override
        public void onAccountsUpdated(final Account[] accounts) {

            mRegistrationView.setText(VoltagePreferences.getUserName(getActivity()));
        }

    }
}