package io.voltage.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.voltage.app.R;

public class UserSearchActivity extends ColorActivity {

    public static void newInstance(final Context context) {
        final Intent intent = new Intent(context, UserSearchActivity.class);
        context.startActivity(intent);
    }

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_search);
		setTitle(R.string.title_user_search);
	}
}