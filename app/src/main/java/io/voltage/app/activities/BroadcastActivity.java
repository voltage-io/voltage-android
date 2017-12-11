package io.voltage.app.activities;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;

import com.google.gson.Gson;

import io.voltage.app.R;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.NfcContent;

public class BroadcastActivity extends ColorDefaultActivity implements CreateNdefMessageCallback {

    public static final short TNF_MIME_MEDIA = 0x02;

	public static void newInstance(final Context context) {
		final Intent intent = new Intent(context, BroadcastActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast);
		setTitle(R.string.title_broadcasting);

        final NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);

        if (adapter != null) {
            adapter.setNdefPushMessageCallback(this, this);
        }
	}

    @Override
    public NdefMessage createNdefMessage(final NfcEvent event) {
        final NdefRecord ndefRecord = getNdefRecord();
        final NdefRecord appRecord = getAppRecord();
        return new NdefMessage(new NdefRecord[] { ndefRecord, appRecord });
    }

    private NdefRecord getAppRecord() {
        return NdefRecord.createApplicationRecord("io.voltage.app");
    }

    private NdefRecord getNdefRecord() {
        final byte[] type = "application/vnd.io.voltage.app".getBytes();
        return new NdefRecord(TNF_MIME_MEDIA, type, null, createPayload());
    }

    private byte[] createPayload() {
        final String name = VoltagePreferences.getUserName(this);
        final String regId = VoltagePreferences.getRegId(this);
        final String publicKey = VoltagePreferences.getPublicKey(this);
        final NfcContent content = new NfcContent(name, regId, publicKey);
        return new Gson().toJson(content).getBytes();
    }
}