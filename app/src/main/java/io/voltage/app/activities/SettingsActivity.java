package io.voltage.app.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import io.pivotal.arca.service.OperationService;
import io.voltage.app.R;
import io.voltage.app.activities.ColorSelectionActivity.ColorSelectionFragment;
import io.voltage.app.activities.ColorSelectionActivity.ColorSelectionFragment.OnColorSelectedListener;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.operations.RegistrationDeleteOperation;
import io.voltage.app.operations.RegistrationPostOperation;

public class SettingsActivity extends ColorDefaultActivity implements OnColorSelectedListener {

	public static void newInstance(final Context context) {
		final Intent intent = new Intent(context, SettingsActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setTitle(R.string.title_settings);

		findColorSelectionFragment().setOnColorSelectedListener(this);
	}

    private ColorSelectionFragment findColorSelectionFragment() {
        final FragmentManager manager = getFragmentManager();
        return (ColorSelectionFragment) manager.findFragmentById(R.id.fragment_color_selection);
    }

    @Override
    public void onColorSelected(final String color) {
        VoltagePreferences.setPrimaryColour(this, color);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener, OnAccountsUpdateListener {

        private SharedPreferences mPreferences;

        @Override
        public void onCreatePreferences(final Bundle bundle, final String s) {
            addPreferencesFromResource(R.xml.settings_preferences);

            mPreferences = getPreferenceManager().getSharedPreferences();

            showPreferences();
        }

        @Override
        public void onResume() {
            super.onResume();

            mPreferences.registerOnSharedPreferenceChangeListener(this);

            final AccountManager manager = AccountManager.get(getActivity());
            manager.addOnAccountsUpdatedListener(this, null, true);
        }

        @Override
        public void onPause() {
            super.onPause();

            mPreferences.unregisterOnSharedPreferenceChangeListener(this);

            final AccountManager manager = AccountManager.get(getActivity());
            manager.removeOnAccountsUpdatedListener(this);
        }

        private void showPreferences() {
            final PreferenceScreen screen = getPreferenceScreen();
            for (int i = 0; i < screen.getPreferenceCount(); i++) {
                final Preference preference = screen.getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    showPreference((PreferenceGroup) preference);
                } else {
                    showPreference(preference);
                }
            }
        }

        private void showPreference(final PreferenceGroup group) {
            for (int i = 0; i < group.getPreferenceCount(); i++) {
                showPreference(group.getPreference(i));
            }
        }

        private void showPreference(final Preference preference) {
            final String key = preference.getKey();
            if (!(preference instanceof CheckBoxPreference)) {
                preference.setSummary(mPreferences.getString(key, null));
            }
            if (key.equals(VoltagePreferences.Property.PUBLISH_REG_ID)) {
                preference.setOnPreferenceClickListener(this);
            }
        }

        @Override
        public void onAccountsUpdated(final Account[] accounts) {
            showPreferences();
        }

        @Override
        public void onSharedPreferenceChanged(final SharedPreferences prefs, final String key) {
            showPreferences();
        }

        @Override
        public boolean onPreferenceClick(final Preference preference) {
            if (preference.getKey().equals(VoltagePreferences.Property.PUBLISH_REG_ID)) {
                checkRegistration();
            }
            return false;
        }

        private void checkRegistration() {
            final String token = FirebaseInstanceId.getInstance().getToken();

            if (!TextUtils.isEmpty(token)) {

                if (VoltagePreferences.shouldPublishRegId(getActivity())) {
                    OperationService.start(getActivity(), new RegistrationPostOperation(token));
                } else {
                    OperationService.start(getActivity(), new RegistrationDeleteOperation(token));
                }
            }
        }
    }
}