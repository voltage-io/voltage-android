package io.voltage.app.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import io.voltage.app.application.VoltagePreferences;

public abstract class ColorDefaultActivity extends ColorActivity implements OnSharedPreferenceChangeListener {

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences prefs = VoltagePreferences.getSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        updateColor(VoltagePreferences.getPrimaryColour(this));
    }

    @Override
    protected void onPause() {
        super.onPause();

        final SharedPreferences prefs = VoltagePreferences.getSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {

        updateColor(VoltagePreferences.getPrimaryColour(this));
    }
}
