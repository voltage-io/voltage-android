package io.voltage.app.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.voltage.app.R;
import io.voltage.app.application.VoltagePreferences;

public class LauncherActivity extends ColorActivity {

    private static final int LAUNCH_MSG = 100;
    private static final int LAUNCH_DURATION = 2000;
    private static final int REQUEST_CODE = 3000;

    private final LaunchHandler mHandler = new LaunchHandler(this);

    private int mResultCode;

    @Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);

        setBackgroundColor();
	}

    private void setBackgroundColor() {
        final String colour = VoltagePreferences.getPrimaryColour(this);
        final View background = findViewById(R.id.launcher_background);
        background.setBackgroundColor(Color.parseColor(colour));
    }

    @Override
	protected void onStart() {
		super.onStart();

        mResultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (mResultCode == ConnectionResult.SUCCESS) {
            AccountActivity.newInstanceIfNotExists(this);
        } else {
            showErrorDialog();
        }
	}

    @Override
    protected void onPause() {
        super.onPause();

        if (mResultCode == ConnectionResult.SUCCESS) {
            mHandler.removeMessages(LAUNCH_MSG);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (mResultCode == ConnectionResult.SUCCESS) {
                mHandler.removeMessages(LAUNCH_MSG);
                mHandler.sendEmptyMessage(LAUNCH_MSG);
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mHandler.sendEmptyMessageDelayed(LAUNCH_MSG, LAUNCH_DURATION);
        } else {
            finish();
        }
    }

    private void showErrorDialog() {
        final Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, mResultCode, REQUEST_CODE);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

    public void launch() {
		InboxActivity.newInstance(this);
        finish();
	}

    private static final class LaunchHandler extends Handler {

		private final LauncherActivity mActivity;

		public LaunchHandler(final LauncherActivity activity) {
			mActivity = activity;
		}

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);

			if (msg.what == LAUNCH_MSG) {
				mActivity.launch();
			}
		}
	}
}