package io.voltage.app.application;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import io.voltage.app.utils.Logger;

public class VoltageSyncAdapter extends AbstractThreadedSyncAdapter {

	public VoltageSyncAdapter(final Context context, final boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	public void onPerformSync(final Account account, final Bundle extras, final String authority, final ContentProviderClient provider, final SyncResult syncResult) {
		Logger.v("voltage.io syncing");
	}
}