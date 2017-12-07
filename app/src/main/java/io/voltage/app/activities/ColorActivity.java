package io.voltage.app.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.lang.Thread.UncaughtExceptionHandler;

import io.voltage.app.application.VoltageExceptionHandler;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.utils.ColorUtils;
import io.voltage.app.utils.DisplayUtils;

public abstract class ColorActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {

    private static void handleUncaughtException(final Context context) {
        final UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new VoltageExceptionHandler(context, handler));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleUncaughtException(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences prefs = VoltagePreferences.getSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        updateColor();
    }

    @Override
    protected void onPause() {
        super.onPause();

        final SharedPreferences prefs = VoltagePreferences.getSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        updateColor();
    }

    private void updateColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            updateTitleBarColor();
        }

        updateActionBarColor();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateTitleBarColor() {
        final String color = VoltagePreferences.getPrimaryColour(this);
        final int darkColor = ColorUtils.darkenColor(color, 0.2f);

        getWindow().setStatusBarColor(darkColor);
    }

    public void updateActionBarColor() {
        final String color = VoltagePreferences.getPrimaryColour(this);
        final String textColor = VoltagePreferences.getSecondaryColour(this);

        final ActionBarDrawable drawable = new ActionBarDrawable(this, color);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(drawable);

            // Hack to fix the color not showing on certain devices
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        final int titleId = getResources().getIdentifier("action_bar_title", "id", "android");

        final TextView titleView = (TextView) findViewById(titleId);
        if (titleView != null) {
            titleView.setTextColor(Color.parseColor(textColor));
        }
    }
    
    private static final class ActionBarDrawable extends ColorDrawable {

        private static final int STROKE_WIDTH = 1;
        private static final float STROKE_RATIO = 0.2f;

        private final Paint mLightPaint = new Paint();
        private final Paint mDarkPaint = new Paint();

        public ActionBarDrawable(final Context context, final String color) {
            super(Color.parseColor(color));

            final int width = DisplayUtils.convertToDp(context, STROKE_WIDTH);

            mLightPaint.setStrokeWidth(width);
            mLightPaint.setColor(ColorUtils.lightenColor(color, STROKE_RATIO));

            mDarkPaint.setStrokeWidth(width);
            mDarkPaint.setColor(ColorUtils.darkenColor(color, STROKE_RATIO));
        }

        @Override
        public void draw(final Canvas canvas) {
            super.draw(canvas);
            final Rect bounds = getBounds();
            final int x = bounds.width();
            final int y = bounds.height() - 1;
            canvas.drawLine(0, y, x, y, mDarkPaint);
            canvas.drawLine(0, 0, x, 0, mLightPaint);
        }
    }
}
