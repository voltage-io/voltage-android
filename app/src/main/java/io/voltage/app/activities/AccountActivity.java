package io.voltage.app.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import io.voltage.app.R;
import io.voltage.app.fragments.AccountFragment;

public class AccountActivity extends FragmentActivity {

    private static final int IF_NOT_EXISTS = 1;

    private interface Extras {
        String FINISH_IF_EXISTS = "finish_if_exists";
    }

    public static void newInstance(final Activity activity) {
        final Intent intent = new Intent(activity, AccountActivity.class);
        activity.startActivity(intent);
    }

    public static void newInstanceIfNotExists(final Activity activity) {
        final Intent intent = new Intent(activity, AccountActivity.class);
        intent.putExtra(Extras.FINISH_IF_EXISTS, true);
        activity.startActivityForResult(intent, IF_NOT_EXISTS);
    }

    @Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle(R.string.title_account);

        final boolean finish = getIntent().getBooleanExtra(Extras.FINISH_IF_EXISTS, false);

        getFragment().finishIfExists(finish);
	}

    private AccountFragment getFragment() {
        final FragmentManager manager = getFragmentManager();
        return (AccountFragment) manager.findFragmentById(R.id.fragment_account);
    }
}