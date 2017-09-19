package io.voltage.app.application;

import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.pivotal.arca.service.OperationService;
import io.voltage.app.operations.RegistrationPostOperation;
import io.voltage.app.utils.Logger;

public class VoltageInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        publishRegistration();
    }

    private void publishRegistration() {

        final String token = FirebaseInstanceId.getInstance().getToken();

        if (!TextUtils.isEmpty(token)) {

            if (VoltagePreferences.shouldPublishRegId(this)) {
                OperationService.start(this, new RegistrationPostOperation(token));
            }
        }

        Logger.v("[REG_ID]: " + token);
    }
}
