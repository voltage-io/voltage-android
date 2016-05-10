package io.voltage.app.application;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class VoltageSyncAdapterService extends Service {

	private static VoltageSyncAdapter sSyncAdapter;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		synchronized (this) {
			if (sSyncAdapter == null) {
				final Context context = getApplicationContext();
				sSyncAdapter = new VoltageSyncAdapter(context, true);
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}

}