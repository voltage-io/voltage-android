package io.voltage.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import io.voltage.app.R;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.utils.ColorUtils;

public class InboxActivity extends ColorActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    public static void newInstance(final Context context) {
		final Intent intent = new Intent(context, InboxActivity.class);
		context.startActivity(intent);
	}

	@Override
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
    protected void onResume() {
        super.onResume();

        final String color = VoltagePreferences.getPrimaryColour(this);
        final int darkColor = ColorUtils.darkenColor(color, 0.2f);

        findViewById(R.id.drawer_header).setBackgroundColor(darkColor);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
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

            case R.id.menu_settings:
                SettingsActivity.newInstance(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}