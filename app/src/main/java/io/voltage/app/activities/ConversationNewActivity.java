package io.voltage.app.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.Set;
import java.util.UUID;

import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.voltage.app.R;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.fragments.ConversationNewNameFragment;
import io.voltage.app.fragments.ConversationNewUsersFragment;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.monitors.ConversationAddMonitor;
import io.voltage.app.requests.MessageInsert;
import io.voltage.app.requests.ThreadInsert;
import io.voltage.app.requests.ThreadUserInsert;

public class ConversationNewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    public static void newInstance(final Context context) {
        final Intent intent = new Intent(context, ConversationNewActivity.class);
        context.startActivity(intent);
    }

    private ConversationNewUsersFragment mUsersFragment = new ConversationNewUsersFragment();
    private ConversationNewNameFragment mNameFragment = new ConversationNewNameFragment();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_new);
		setTitle(R.string.title_conversation_new);

        final FragmentManager fragmentManager = getFragmentManager();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentAdapter(fragmentManager, mUsersFragment, mNameFragment));
        viewPager.setOnPageChangeListener(this);
	}

    @Override
    public void onBackPressed() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final int currentItem = viewPager.getCurrentItem();

        if (currentItem == 1) {
            viewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }

    public void proceed() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final int nextItem = viewPager.getCurrentItem() + 1;
        viewPager.setCurrentItem(nextItem, true);
    }

    public void createThread(final String name, final Set<String> regIds) {
        final String threadId = UUID.randomUUID().toString();
        final String senderId = VoltagePreferences.getRegId(this);

        final ArcaDispatcher dispatcher = ArcaDispatcherFactory.generateDispatcher(this);
        dispatcher.setRequestMonitor(new ConversationAddMonitor());

        dispatcher.execute(new ThreadInsert(threadId, name));
        dispatcher.execute(new MessageInsert(senderId, threadId, GcmPayload.Type.THREAD_CREATED.name(), name, GcmPayload.Type.THREAD_CREATED));
        dispatcher.execute(new MessageInsert(senderId, threadId, GcmPayload.Type.USER_ADDED.name(), senderId, GcmPayload.Type.USER_ADDED));

        for (final String regId : regIds) {
            dispatcher.execute(new ThreadUserInsert(threadId, regId));
            dispatcher.execute(new MessageInsert(senderId, threadId, GcmPayload.Type.USER_ADDED.name(), regId, GcmPayload.Type.USER_ADDED));
        }

        ConversationActivity.newInstance(this, threadId);
    }

    @Override
    public void onPageSelected(final int position) {
        final Set<String> regIds = mUsersFragment.getRegistrationIds();
        mNameFragment.setRegistrationIds(regIds);
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
}