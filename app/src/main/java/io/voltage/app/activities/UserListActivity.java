package io.voltage.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import io.voltage.app.R;

public class UserListActivity extends ColorActivity {

	public static void newInstance(final Context context) {
		final Intent intent = new Intent(context, UserListActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
        setTitle(R.string.title_user_list);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add:
                UserNewActivity.newInstance(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}