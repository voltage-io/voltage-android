package io.voltage.app.application;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;

import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.fragments.ArcaExecutorFactory;
import io.pivotal.arca.monitor.ArcaExecutor;
import io.pivotal.arca.service.OperationService;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.operations.MessageOperation;
import io.voltage.app.requests.MessageQuery;
import io.voltage.app.utils.Logger;

public class VoltageSyncAdapterService extends Service {

    private SyncAdapter mSyncAdapter = new SyncAdapter(this);

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }

    private static class SyncAdapter extends AbstractThreadedSyncAdapter {

        private SyncAdapter(final Context context) {
            super(context, true);
        }

        @Override
        public void onPerformSync(final Account account, final Bundle extras, final String authority, final ContentProviderClient provider, final SyncResult syncResult) {

            final ArcaExecutor executor = ArcaExecutorFactory.generateExecutor(getContext());

            final QueryResult result = executor.execute(new MessageQuery());
            final Cursor cursor = result.getData();

            while (cursor.moveToNext()) {
                final String msgUuid = cursor.getString(cursor.getColumnIndex(MessageTable.Columns.MSG_UUID));

                Logger.v("voltage.io sync - " + msgUuid);

                OperationService.start(getContext(), new MessageOperation(msgUuid));
            }

            result.close();
        }
    }
}