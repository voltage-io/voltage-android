package io.voltage.app.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleItemFragment;
import io.voltage.app.R;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.monitors.UserEditMonitor;
import io.voltage.app.requests.UserQuery;
import io.voltage.app.requests.UserUpdate;

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

    @ArcaFragment(
        fragmentLayout = R.layout.fragment_user_edit,
        monitor = UserEditMonitor.class
    )
    public static class UserEditFragment extends ArcaSimpleItemFragment implements View.OnClickListener {

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.user_edit_name, UserTable.Columns.NAME),
            new Binding(R.id.user_edit_reg_id, UserTable.Columns.REG_ID)
        );

        private Button mEditUser;
        private TextView mNameView;
        private TextView mRegIdView;
        private String mRegId;

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mEditUser = (Button) view.findViewById(R.id.user_edit_button);
            mEditUser.setOnClickListener(this);

            mNameView = (TextView) view.findViewById(R.id.user_edit_name);
            mRegIdView = (TextView) view.findViewById(R.id.user_edit_reg_id);
        }

        public void setRegId(final String id) {
            if (id != null) {
                execute(new UserQuery(mRegId = id));
            }
        }

        @Override
        public void onClick(final View view) {
            final String name = mNameView.getText().toString();
            final String regId = mRegIdView.getText().toString();

            execute(new UserUpdate(name, regId, mRegId));

            getActivity().finish();
        }
    }
}