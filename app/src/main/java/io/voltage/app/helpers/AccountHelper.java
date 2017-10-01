package io.voltage.app.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import java.util.UUID;

import io.voltage.app.application.VoltageAuthenticatorService;
import io.voltage.app.application.VoltageContentProvider;

public interface AccountHelper {

    boolean hasAccount(final Context context);
    Account getAccount(final Context context);
    Account[] getAccounts(final Context context);
    void setUsername(final Context context, final String name);
    void requestSync(final Context context);


    class Default implements AccountHelper {

        private static final int SYNCABLE = 1;
        private static final int ONE_HOUR = 60 * 60;

        public boolean hasAccount(final Context context) {
            return getAccounts(context).length > 0;
        }

        public Account getAccount(final Context context) {
            final Account[] accounts = getAccounts(context);
            return accounts.length > 0 ? accounts[0] : null;
        }

        public Account[] getAccounts(final Context context) {
            final AccountManager manager = AccountManager.get(context);
            return manager.getAccountsByType(VoltageAuthenticatorService.ACCOUNT_TYPE);
        }

        public void setUsername(final Context context, final String name) {
            if (hasAccount(context)) {
                updateAccount(context, name);
            } else {
                addAccount(context, name);
            }
        }

        public void requestSync(final Context context) {
            final Account account = getAccount(context);
            final String authority = VoltageContentProvider.AUTHORITY;

            ContentResolver.requestSync(account, authority, createSyncBundle());
        }

        private Bundle createSyncBundle() {
            final Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            return bundle;
        }

        private void updateAccount(final Context context, final String name) {
            final Account account = getAccount(context);
            if (!account.name.equals(name)) {
                removeAccount(context, account);
                addAccount(context, name);
            }
        }

        private void removeAccount(final Context context, final Account account) {
            final AccountManager manager = AccountManager.get(context);
            manager.removeAccount(account, null, null);
        }

        private void addAccount(final Context context, final String name) {
            final String type = VoltageAuthenticatorService.ACCOUNT_TYPE;
            final Account account = new Account(name, type);

            setupSync(account);
            setupAccount(context, account);
        }

        private void setupSync(final Account account) {
            final String authority = VoltageContentProvider.AUTHORITY;

            ContentResolver.setIsSyncable(account, authority, SYNCABLE);
            ContentResolver.setSyncAutomatically(account, authority, true);
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, ONE_HOUR);
        }

        private void setupAccount(final Context context, final Account account) {
            final String tokenType = VoltageAuthenticatorService.TOKEN_TYPE;
            final String authToken = UUID.randomUUID().toString();

            final AccountManager manager = AccountManager.get(context);
            manager.addAccountExplicitly(account, null, null);
            manager.setAuthToken(account, tokenType, authToken);
        }
    }
}
