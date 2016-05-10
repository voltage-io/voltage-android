package io.voltage.app.application;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VoltageAuthenticatorService extends Service {

	private VoltageAuthenticator mAuthenticator;

	@Override
	public void onCreate() {
		super.onCreate();
		
		if (mAuthenticator == null) { 
			mAuthenticator = new VoltageAuthenticator(this);
		}
	}
	
	@Override
	public IBinder onBind(final Intent intent) {
		return mAuthenticator.getIBinder();
	}
}