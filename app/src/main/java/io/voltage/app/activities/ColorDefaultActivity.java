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

        final String color = VoltagePreferences.getPrimaryColour(this);
        final String textColor = VoltagePreferences.getSecondaryColour(this);

        updateColor(color, textColor);
    }

    @Override
    protected void onPause() {
        super.onPause();

        final SharedPreferences prefs = VoltagePreferences.getSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final String color = VoltagePreferences.getPrimaryColour(this);
        final String textColor = VoltagePreferences.getSecondaryColour(this);

        updateColor(color, textColor);
    }
}
