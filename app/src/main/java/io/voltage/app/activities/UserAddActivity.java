package io.voltage.app.activities;

import android.app.FragmentManager;
import android.os.Bundle;

import io.voltage.app.R;
import io.voltage.app.fragments.UserAddFragment;

public abstract class UserAddActivity extends ColorActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_add);
		setTitle(R.string.title_user_add);
	}

    protected void setUserInfo(final String userName, final String regId) {
        final UserAddFragment fragment = getFragment();
        fragment.setRegistrationId(regId);
        fragment.setUserName(userName);
    }

    private UserAddFragment getFragment() {
        final FragmentManager manager = getFragmentManager();
        return (UserAddFragment) manager.findFragmentById(R.id.fragment_user_add);
    }
}