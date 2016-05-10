package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Update;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;

public class MessageUpdate extends Update {

    public MessageUpdate(final ContentValues values) {
        super(VoltageContentProvider.Uris.MESSAGES, values);

        final String uuid = values.getAsString(MessageTable.Columns.MSG_UUID);
        setWhere(MessageTable.Columns.MSG_UUID + "=?", uuid);
    }

    public MessageUpdate(final String uuid, final int state) {
        super(VoltageContentProvider.Uris.MESSAGES, values(state));
        setWhere(MessageTable.Columns.MSG_UUID + "=?", uuid);
    }

    private static ContentValues values(final int state) {
        final ContentValues values = new ContentValues();
        values.put(MessageTable.Columns._STATE, state);
        return values;
    }
}
