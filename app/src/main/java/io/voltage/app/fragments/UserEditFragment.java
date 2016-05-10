package io.voltage.app.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

@ArcaFragment(
    fragmentLayout = R.layout.fragment_user_edit,
    monitor = UserEditMonitor.class
)
public class UserEditFragment extends ArcaSimpleItemFragment implements View.OnClickListener {

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
            mRegId = id;
            execute(new UserQuery(mRegId));
        }
    }

    @Override
    public void onClick(final View view) {
        final String name = mNameView.getText().toString();
        final String regId = mRegIdView.getText().toString();

        final UserUpdate request = new UserUpdate(name, regId, mRegId);

        getRequestDispatcher().execute(request);
        getActivity().finish();
    }
}