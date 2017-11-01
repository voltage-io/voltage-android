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
import android.text.TextUtils;

import io.pivotal.arca.service.OperationService;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.operations.MessageOperation;
import io.voltage.app.operations.FriendRequestOperation;
import io.voltage.app.requests.MessageQuery;
import io.voltage.app.requests.UserQuery;
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

            syncUsers();
            syncMessages();
        }

        private void syncUsers() {
            final Cursor cursor = VoltageExecutor.execute(getContext(), new MessageQuery()).getData();

            while (cursor.moveToNext()) {
                final String msgUuid = cursor.getString(cursor.getColumnIndex(MessageTable.Columns.MSG_UUID));

                Logger.v("[sync message] " + msgUuid);

                OperationService.start(getContext(), new MessageOperation(msgUuid));
            }

            cursor.close();
        }

        private void syncMessages() {
            final Cursor cursor = VoltageExecutor.execute(getContext(), new UserQuery()).getData();

            while (cursor.moveToNext()) {
                final String regId = cursor.getString(cursor.getColumnIndex(UserTable.Columns.REG_ID));
                final String publicKey = cursor.getString(cursor.getColumnIndex(UserTable.Columns.PUBLIC_KEY));

                if (TextUtils.isEmpty(publicKey)) {
                    Logger.v("[sync user] " + regId);

                    OperationService.start(getContext(), new FriendRequestOperation(regId));
                }
            }

            cursor.close();
        }
    }
}