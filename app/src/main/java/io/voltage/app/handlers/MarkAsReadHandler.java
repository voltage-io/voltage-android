package io.voltage.app.handlers;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import io.pivotal.arca.dispatcher.Update;
import io.pivotal.arca.monitor.ArcaExecutor;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.monitors.MarkAsReadMonitor;
import io.voltage.app.requests.MessageUpdate;

public class MarkAsReadHandler extends Handler {

    private static final int MSG_READ = 1000;
    private static final long MSG_DELAY = 2000;

    private ArcaExecutor mExecutor;

    public MarkAsReadHandler(final Context context) {
        final ContentResolver resolver = context.getContentResolver();
        mExecutor = new ArcaExecutor.DefaultArcaExecutor(resolver, context);
        mExecutor.setRequestMonitor(new MarkAsReadMonitor());
    }

    public void sendMessageRead(final String uuid) {
        final Message msg = obtainMessage(MSG_READ, uuid);
        sendMessageDelayed(msg, MSG_DELAY);
    }

    @Override
    public void handleMessage(final Message msg) {
        if (msg.what == MSG_READ) {
            final String uuid = (String) msg.obj;
            markMessageAsRead(uuid);
        }
    }

    private void markMessageAsRead(final String uuid) {
        final int state = MessageTable.State.DEFAULT;
        final Update update = new MessageUpdate(uuid, state);

        mExecutor.execute(update);
    }
}
