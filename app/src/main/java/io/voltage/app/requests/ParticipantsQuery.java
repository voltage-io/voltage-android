package io.voltage.app.requests;

import java.util.Random;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ParticipantView;

public class ParticipantsQuery extends Query {

    private static final int ID = new Random().nextInt(1000) + 1000;

    public ParticipantsQuery(final String threadId) {
        super(VoltageContentProvider.Uris.PARTICIPANTS, ID);

        setWhere(ParticipantView.Columns.THREAD_ID + "=?", threadId);
    }
}
