package io.voltage.app.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.pivotal.arca.fragments.ArcaQueryFragment;
import io.pivotal.arca.service.OperationService;
import io.voltage.app.R;
import io.voltage.app.monitors.UserAddMonitor;
import io.voltage.app.operations.FriendOperation;
import io.voltage.app.requests.UserInsert;

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

    public static class UserAddFragment extends ArcaQueryFragment implements View.OnClickListener {

        private TextView mNameView;
        private TextView mRegIdView;
        private Button mAddUser;

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_user_add, container, false);
        }

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mNameView = (TextView) view.findViewById(R.id.user_add_name);
            mRegIdView = (TextView) view.findViewById(R.id.user_add_reg_id);

            mAddUser = (Button) view.findViewById(R.id.user_add_button);
            mAddUser.setOnClickListener(this);

            setRequestMonitor(new UserAddMonitor());
        }

        @Override
        public void onClick(final View view) {
            final String name = mNameView.getText().toString();
            final String regId = mRegIdView.getText().toString();

            execute(new UserInsert(name, regId));

            OperationService.start(getActivity(), new FriendOperation(regId));

            getActivity().finish();
        }

        public void setRegistrationId(final String regId) {
            mRegIdView.setText(regId);
        }

        public void setUserName(final String userName) {
            mNameView.setText(userName);
        }
    }
}