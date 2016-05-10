package io.voltage.app.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import io.voltage.app.R;
import io.voltage.app.fragments.UserEditFragment;

public class UserEditActivity extends ColorActivity {

    private interface Extras {
        String REG_ID = "reg_id";
    }

	public static void newInstance(final Context context, final String regId) {
		final Intent intent = newIntent(context, regId);
		context.startActivity(intent);
	}

    public static Intent newIntent(final Context context, final String regId) {
        final Intent intent = new Intent(context, UserEditActivity.class);
        intent.putExtra(Extras.REG_ID, regId);
        return intent;
    }

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        setTitle(R.string.title_user_edit);

        final String regId = getIntent().getStringExtra(Extras.REG_ID);

        if (TextUtils.isEmpty(regId)) {
            Toast.makeText(this, "Registration ID cannot be null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            getFragment().setRegId(regId);
        }
    }

    private UserEditFragment getFragment() {
        final FragmentManager manager = getFragmentManager();
        return (UserEditFragment) manager.findFragmentById(R.id.fragment_user_edit);
    }
}