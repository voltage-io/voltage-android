package io.voltage.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.voltage.app.R;

public class UserNewActivity extends ColorActivity {

    public static void newInstance(final Context context) {
        final Intent intent = new Intent(context, UserNewActivity.class);
        context.startActivity(intent);
    }

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_new);
		setTitle(R.string.title_user_new);
	}
}