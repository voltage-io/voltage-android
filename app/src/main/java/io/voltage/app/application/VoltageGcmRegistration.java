package io.voltage.app.application;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import io.pivotal.arca.utils.ArrayUtils;
import io.voltage.app.utils.Logger;

public class VoltageGcmRegistration {

    public interface Listener {
        void onRegistrationComplete(final String regId);
    }

    private Context mContext;

    public VoltageGcmRegistration(final Context context) {
        mContext = context;
    }

    public void register(final Listener... listeners) {
        final ProxyListener proxy = new ProxyListener(listeners);

        proxy.addListeners(new Listener() {
            @Override
            public void onRegistrationComplete(final String regId) {
                setRegistrationId(mContext, regId);
            }
        });

        final String regId = getRegistrationId(mContext);

        if (TextUtils.isEmpty(regId)) {
            new RegistrationTask(mContext, proxy).execute();
        } else {
            proxy.onRegistrationComplete(regId);
        }
    }

    private void setRegistrationId(final Context context, final String regId) {
        if (!TextUtils.isEmpty(regId)) {
            VoltagePreferences.storeRegistrationInfo(context, regId);
        }
    }

    private String getRegistrationId(final Context context) {
        final String regId = VoltagePreferences.getRegId(context);
        if (TextUtils.isEmpty(regId)) {
            return null;
        }

        final String version = VoltagePreferences.getRegVersion(context);
        if (!version.equals(VoltageApplication.getVersion(context))) {
            return null;
        }

        return regId;
    }

    private static class ProxyListener implements Listener {

        private Listener[] mListeners;

        public ProxyListener(final Listener... listeners) {
            mListeners = listeners;
        }

        public void addListeners(final Listener... listeners) {
            mListeners = ArrayUtils.append(mListeners, listeners);
        }

        @Override
        public void onRegistrationComplete(final String regId) {
            if (mListeners != null) {
                for (final Listener listener : mListeners) {
                    listener.onRegistrationComplete(regId);
                }
            }
        }
    }

    private static class RegistrationTask extends AsyncTask<Void, Void, String> {

        private final Context mContext;
        private final Listener mListener;

        public RegistrationTask(final Context context, final Listener listener) {
            mContext = context;
            mListener = listener;
        }

        @Override
        protected String doInBackground(final Void... params) {
            try {
                Logger.v("Registering with GCM");
                final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
                final String regId = gcm.register(VoltageProperties.GCM_SENDER_ID);
                Logger.v("Registered: " + regId);
                return regId;
            } catch (final IOException ex) {
                Logger.v("Registration error: " + ex);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (mListener != null) {
                mListener.onRegistrationComplete(result);
            }
        }
    }
}
