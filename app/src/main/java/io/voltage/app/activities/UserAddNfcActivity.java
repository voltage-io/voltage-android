package io.voltage.app.activities;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

import com.google.gson.Gson;

import io.voltage.app.models.NfcContent;

public class UserAddNfcActivity extends UserAddActivity {

    @Override
    public void onNewIntent(final Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            final NfcContent content = getNfcContent(intent);
            final String userName = content.getName();
            final String regId = content.getRegId();

            setUserInfo(userName, regId);
        }
    }

    private NfcContent getNfcContent(final Intent intent) {
        final NdefRecord record = getNdefRecord(intent);
        final String payload = new String(record.getPayload());
        return new Gson().fromJson(payload, NfcContent.class);
    }

    private NdefRecord getNdefRecord(final Intent intent) {
        final String extra = NfcAdapter.EXTRA_NDEF_MESSAGES;
        final Parcelable[] raw = intent.getParcelableArrayExtra(extra);
        final NdefMessage msg = (NdefMessage) raw[0];
        return msg.getRecords()[0];
    }
}