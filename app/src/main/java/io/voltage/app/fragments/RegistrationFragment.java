package io.voltage.app.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleItemFragment;
import io.voltage.app.R;
import io.voltage.app.activities.AccountActivity;
import io.voltage.app.application.VoltageContentProvider.RegistrationTable;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.requests.RegistrationQuery;

@ArcaFragment(
        fragmentLayout = R.layout.fragment_registration
)
public class RegistrationFragment extends ArcaSimpleItemFragment implements OnAccountsUpdateListener {

    @ArcaFragmentBindings
    private static final Collection<Binding> BINDINGS = Arrays.asList(
            new Binding(R.id.registration_id, RegistrationTable.Columns.REG_ID),
            new Binding(R.id.registration_lookup, RegistrationTable.Columns.LOOKUP)
    );

    private TextView mRegistrationView;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRegistrationView = (TextView) view.findViewById(R.id.registration_name);
        mRegistrationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AccountActivity.newInstance(getActivity());
            }
        });

        mRegistrationView.setText(VoltagePreferences.getUserName(getActivity()));

        execute(new RegistrationQuery());
    }

    @Override
    public void onResume() {
        super.onResume();

        final AccountManager manager = AccountManager.get(getActivity());
        manager.addOnAccountsUpdatedListener(this, null, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        final AccountManager manager = AccountManager.get(getActivity());
        manager.removeOnAccountsUpdatedListener(this);
    }

    @Override
    public void onAccountsUpdated(final Account[] accounts) {

        mRegistrationView.setText(VoltagePreferences.getUserName(getActivity()));
    }

}
