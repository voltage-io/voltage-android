package io.voltage.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class UserAddParamsActivity extends UserAddActivity {

    private interface Extras {
        String NAME = "name";
        String REG_ID = "reg_id";
        String PUBLIC_KEY = "public_key";
    }

    public static void newInstance(final Context context, final String regId) {
        final Intent intent = new Intent(context, UserAddParamsActivity.class);
        intent.putExtra(Extras.REG_ID, regId);
        context.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();

        if (extras != null && extras.containsKey(Extras.REG_ID)) {
            final String name = extras.getString(Extras.NAME);
            final String regId = extras.getString(Extras.REG_ID);
            final String publicKey = extras.getString(Extras.PUBLIC_KEY);

            setUserInfo(name, regId, publicKey);
        }
    }
}