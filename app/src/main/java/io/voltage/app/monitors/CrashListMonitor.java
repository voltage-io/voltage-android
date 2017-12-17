package io.voltage.app.monitors;

import android.content.Context;

import io.pivotal.arca.dispatcher.Delete;
import io.pivotal.arca.dispatcher.DeleteResult;
import io.pivotal.arca.monitor.RequestMonitor;
import io.voltage.app.application.VoltageContentProvider.Uris;
import io.voltage.app.requests.CrashDelete;

public class CrashListMonitor extends RequestMonitor.AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Delete request, final DeleteResult result) {

        if (request instanceof CrashDelete) {
            context.getContentResolver().notifyChange(Uris.CRASHES, null);
        }

        return Flags.DATA_VALID;
    }
}
