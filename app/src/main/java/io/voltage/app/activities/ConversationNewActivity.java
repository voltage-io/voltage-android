package io.voltage.app.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleAdapterFragment;
import io.pivotal.arca.fragments.ArcaSimpleDispatcherFragment;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.monitors.ConversationAddMonitor;
import io.voltage.app.requests.ThreadInsertBatch;
import io.voltage.app.requests.UserQuery;
import io.voltage.app.utils.CryptoUtils;

public class ConversationNewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    public static void newInstance(final Context context) {
        final Intent intent = new Intent(context, ConversationNewActivity.class);
        context.startActivity(intent);
    }

    private ConversationNewUsersFragment mUsersFragment = new ConversationNewUsersFragment();
    private ConversationNewNameFragment mNameFragment = new ConversationNewNameFragment();

    private ViewPager mViewPager;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_new);
		setTitle(R.string.title_conversation_new);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new FragmentAdapter(getFragmentManager(), mUsersFragment, mNameFragment));
	}

    @Override
    protected void onResume() {
        super.onResume();

        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mViewPager.removeOnPageChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        if (viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }

    public void proceed() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

    @Override
    public void onPageSelected(final int position) {
        mNameFragment.setRegistrationIds(mUsersFragment.getRegistrationIds());
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(final int state) {

    }

    private static class FragmentAdapter extends FragmentStatePagerAdapter {

        private final Fragment[] mFragments;

        public FragmentAdapter(final FragmentManager manager, final Fragment... fragments) {
            super(manager);
            mFragments = fragments;
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }
    }


    @ArcaFragment(
        fragmentLayout = R.layout.fragment_conversation_new_users,
        adapterItemLayout = R.layout.list_item_user_select
    )
    public static class ConversationNewUsersFragment extends ArcaSimpleAdapterFragment implements View.OnClickListener {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.user_name, VoltageContentProvider.UserTable.Columns.NAME)
        );

        private final Set<String> mRegIds = new HashSet<>();

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
                ((ConversationNewActivity)getActivity()).proceed();
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

    @ArcaFragment(
            fragmentLayout = R.layout.fragment_conversation_new_name,
            monitor = ConversationAddMonitor.class
    )
    public static class ConversationNewNameFragment extends ArcaSimpleDispatcherFragment implements View.OnClickListener, TextWatcher {

        private Button mCreateButton;
        private EditText mConversationName;

        private Set<String> mRegIds;

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mCreateButton = (Button) view.findViewById(R.id.conversation_create);
            mCreateButton.setOnClickListener(this);

            mConversationName = (EditText) view.findViewById(R.id.conversation_name);
            mConversationName.addTextChangedListener(this);
        }

        public void setRegistrationIds(final Set<String> regIds) {
            mRegIds = regIds;
            checkRequirements();
        }

        private void checkRequirements() {
            final boolean itemsChecked = mRegIds != null && mRegIds.size() > 0;
            mCreateButton.setEnabled(itemsChecked);
        }

        @Override
        public void onClick(final View view) {
            final String name = mConversationName.getText().toString();

            final String threadId = UUID.randomUUID().toString();
            final String threadKey = CryptoUtils.generateThreadKey();
            final String senderId = VoltagePreferences.getRegId(getActivity());

            execute(new ThreadInsertBatch(threadId, threadKey, name, senderId, mRegIds));

            ConversationActivity.newInstance(getActivity(), threadId);
        }

        @Override
        public void beforeTextChanged(final CharSequence sequence, final int start, final int count, final int after) {

        }

        @Override
        public void onTextChanged(final CharSequence sequence, final int start, final int before, final int count) {

        }

        @Override
        public void afterTextChanged(final Editable editable) {
            checkRequirements();
        }
    }
}