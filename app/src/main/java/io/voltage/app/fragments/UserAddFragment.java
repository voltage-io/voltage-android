package io.voltage.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.pivotal.arca.fragments.ArcaDispatcherFactory;
import io.pivotal.arca.monitor.ArcaDispatcher;
import io.voltage.app.R;
import io.voltage.app.monitors.UserAddMonitor;
import io.voltage.app.requests.UserInsert;

public class UserAddFragment extends Fragment implements View.OnClickListener {

    private ArcaDispatcher mDispatcher;

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

        mDispatcher = ArcaDispatcherFactory.generateDispatcher(this);
        mDispatcher.setRequestMonitor(new UserAddMonitor());
    }

    @Override
    public void onClick(final View view) {
        final String name = mNameView.getText().toString();
        final String regId = mRegIdView.getText().toString();

        mDispatcher.execute(new UserInsert(name, regId));

        getActivity().finish();
    }

    public void setRegistrationId(final String regId) {
        mRegIdView.setText(regId);
    }

    public void setUserName(final String userName) {
        mNameView.setText(userName);
    }
}