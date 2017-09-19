package io.voltage.app.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import io.pivotal.arca.provider.ContextDataset;

public abstract class SearchDataset extends ContextDataset {


    protected abstract Cursor search(String selectionArg) throws Exception;

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        try {
            return search(selectionArgs[0]);
        } catch (final Exception e) {
            return new MatrixCursor(new String[] { "_id" });
        }
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
