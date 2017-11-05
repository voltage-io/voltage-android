package io.voltage.app.helpers;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.pivotal.arca.utils.ArrayUtils;
import io.voltage.app.application.VoltageApi;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmMessage;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmRequest;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.GcmSyncMessage;
import io.voltage.app.models.Recipients;
import io.voltage.app.models.User;
import io.voltage.app.utils.Logger;

public interface MessagingHelper {

    List<GcmResponse> aesEncryptAndSend(final Context context, final Recipients recipients, final GcmMessage gcmMessage) throws Exception;
    List<GcmResponse> rsaEncryptAndSend(final Context context, final Recipients recipients, final GcmMessage gcmMessage) throws Exception;
    GcmResponse rsaEncryptAndSend(final Context context, final User user, final GcmSyncMessage gcmSyncMessage) throws Exception;
    GcmResponse send(final Context context, final String regId, final GcmPayload gcmPayload) throws Exception;
    GcmResponse send(final Context context, final List<String> regIds, final GcmPayload gcmPayload) throws Exception;


    class Default implements MessagingHelper {

        protected final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();


        public List<GcmResponse> aesEncryptAndSend(final Context context, final Recipients recipients, final GcmMessage gcmMessage) throws Exception {
            final String threadKey = recipients.getThreadKey();

            if (TextUtils.isEmpty(threadKey)) {
                throw new IllegalStateException("Missing symmetric key for thread: " + gcmMessage.getThreadId());
            }

            gcmMessage.attemptAesEncrypt(threadKey);

            return Collections.singletonList(send(context, recipients.getUserIdsList(), gcmMessage));
        }

        public List<GcmResponse> rsaEncryptAndSend(final Context context, final Recipients recipients, final GcmMessage gcmMessage) throws Exception {
            final ArrayList<String> errors = new ArrayList<>();
            final ArrayList<GcmResponse> responses = new ArrayList<>();

            final List<String> regIds = recipients.getUserIdsList();
            final List<String> publicKeys = recipients.getUserPublicKeysList();

            for (int i = 0; i < regIds.size(); i++) {
                final String regId = regIds.get(i);
                final String publicKey = publicKeys.get(i);

                if (!TextUtils.isEmpty(publicKey)) {
                    gcmMessage.attemptRsaEncrypt(publicKey);

                    responses.add(send(context, regId, gcmMessage));
                } else {
                    errors.add(regId);
                }
            }

            if (errors.size() > 0) {
                throw new IllegalStateException("Missing public key for users: " + errors);
            }

            return responses;
        }

        public GcmResponse rsaEncryptAndSend(final Context context, final User user, final GcmSyncMessage gcmSyncMessage) throws Exception {
            final String publicKey = user.getPublicKey();

            if (TextUtils.isEmpty(publicKey)) {
                throw new IllegalStateException("Missing public key for user: " + user.getRegId());
            }

            gcmSyncMessage.attemptRsaEncrypt(publicKey);

            return send(context, user.getRegId(), gcmSyncMessage);
        }


        public GcmResponse send(final Context context, final String regId, final GcmPayload gcmPayload) throws Exception {
            return send(context, Collections.singletonList(regId), gcmPayload);
        }

        public GcmResponse send(final Context context, final List<String> regIds, final GcmPayload gcmPayload) throws Exception {

            if (ArrayUtils.isEmpty(regIds)) {
                throw new IllegalArgumentException("Message not being sent to any users.");
            }

            if (regIds.size() > 1) {
                regIds.remove(VoltagePreferences.getRegId(context));
            }

            final GcmRequest gcmRequest = new GcmRequest(gcmPayload, regIds);
            final GcmResponse gcmResponse = VoltageApi.sendMessage(gcmRequest);

            Logger.v("Response: " + gcmResponse);

            return handleGcmResponse(context, regIds, gcmResponse);
        }

        private GcmResponse handleGcmResponse(final Context context, final List<String> regIds, final GcmResponse gcmResponse) throws Exception {

            final List<GcmResponse.Result> results = gcmResponse.getResults();
            for (int i = 0; i < results.size(); i++) {

                final GcmResponse.Result result = results.get(i);
                final String newRegId = result.getRegistrationId();

                if (!TextUtils.isEmpty(newRegId)) {
                    final String oldRegId = regIds.get(i);
                    mDatabaseHelper.updateRegistration(context, newRegId, oldRegId);

                    Logger.v("New registration id: " + newRegId);
                    Logger.v("Old registration id: " + oldRegId);
                }
            }

            if (!gcmResponse.hasSuccess()) throw new RuntimeException();

            return gcmResponse;
        }
    }
}
