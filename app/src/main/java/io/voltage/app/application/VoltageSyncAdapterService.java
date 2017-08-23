package io.voltage.app.application;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

import io.voltage.app.utils.Logger;

public class VoltageSyncAdapterService extends Service {

	private SyncAdapter mSyncAdapter = new SyncAdapter(this);

	@Override
	public IBinder onBind(Intent intent) {
		return mSyncAdapter.getSyncAdapterBinder();
	}

	public static class SyncAdapter extends AbstractThreadedSyncAdapter {

		private SyncAdapter(final Context context) {
			super(context, true);
		}

		@Override
		public void onPerformSync(final Account account, final Bundle extras, final String authority, final ContentProviderClient provider, final SyncResult syncResult) {
			Logger.v("voltage.io syncing");
		}
	}
}