package io.voltage.app.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaSimpleDispatcherFragment;
import io.voltage.app.R;
import io.voltage.app.monitors.UserAddMonitor;
import io.voltage.app.requests.UserInsert;

public abstract class UserAddActivity extends ColorDefaultActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_add);
		setTitle(R.string.title_user_add);
	}

    protected void setUserInfo(final String userName, final String regId, final String publicKey) {
        final UserAddFragment fragment = getFragment();
        fragment.setRegistrationId(regId);
        fragment.setUserName(userName);
        fragment.setPublicKey(publicKey);
    }

    private UserAddFragment getFragment() {
        final FragmentManager manager = getFragmentManager();
        return (UserAddFragment) manager.findFragmentById(R.id.fragment_user_add);
    }

    @ArcaFragment(
            fragmentLayout = R.layout.fragment_user_add,
            monitor = UserAddMonitor.class
    )
    public static class UserAddFragment extends ArcaSimpleDispatcherFragment implements View.OnClickListener {

        private TextView mNameView;
        private TextView mRegIdView;
        private TextView mPublicKeyView;
        private Button mAddUser;

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mNameView = (TextView) view.findViewById(R.id.user_add_name);
            mRegIdView = (TextView) view.findViewById(R.id.user_add_reg_id);
            mPublicKeyView = (TextView) view.findViewById(R.id.user_add_public_key);

            mAddUser = (Button) view.findViewById(R.id.user_add_button);
            mAddUser.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            final String name = mNameView.getText().toString();
            final String regId = mRegIdView.getText().toString();
            final String publicKey = mPublicKeyView.getText().toString();

            execute(new UserInsert(name, regId, publicKey));

            getActivity().finish();
        }

        public void setRegistrationId(final String regId) {
            mRegIdView.setText(regId);
        }

        public void setUserName(final String userName) {
            mNameView.setText(userName);
        }

        public void setPublicKey(final String publicKey) {
            mPublicKeyView.setText(publicKey);
        }
    }
}