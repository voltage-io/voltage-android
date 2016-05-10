package io.voltage.app.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;

import io.voltage.app.R;
import io.voltage.app.activities.AccountActivity;
import io.voltage.app.application.VoltagePreferences;

public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener, OnAccountsUpdateListener {

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
            if (key.equals(VoltagePreferences.Property.USER_NAME)) {
                final Activity activity = getActivity();
                if (activity != null) {
                    preference.setSummary(VoltagePreferences.getUserName(activity));
                }
                preference.setOnPreferenceClickListener(this);
            } else {
                preference.setSummary(mPreferences.getString(key, null));
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences prefs, final String key) {
        showPreferences();
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        AccountActivity.newInstance(getActivity());
        return false;
    }

    @Override
    public void onAccountsUpdated(final Account[] accounts) {
        showPreferences();
    }
}
