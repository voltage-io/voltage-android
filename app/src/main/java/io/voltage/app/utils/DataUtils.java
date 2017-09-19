package io.voltage.app.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pivotal.arca.provider.Column;
import io.pivotal.arca.provider.SQLiteTable;

public class DataUtils {


    /*

        These methods iterate over a List<Object> and populates ContentValues from the Objects fields.

     */

    public static ContentValues[] values(final List<?> list) {
        final ContentValues[] values = new ContentValues[list.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = values(list.get(i));
        }
        return values;
    }

    public static ContentValues values(final Object object) {
        final Class<?> klass = object.getClass();
        final ContentValues values = new ContentValues();

        try {
            getValuesFromFields(object, klass, values);
        } catch (final Exception e) {
            Logger.ex(e);
        }

        return values;
    }

    private static void getValuesFromFields(final Object object, final Class<?> klass, final ContentValues values) throws Exception {
        final Field[] fields = klass.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            addValuesObject(values, field.getName(), field.get(object));
        }

        final Class<?> superKlass = klass.getSuperclass();
        if (!superKlass.equals(Object.class)) {
            getValuesFromFields(object, superKlass, values);
        }
    }

    private static void addValuesObject(final ContentValues values, final String key, final Object value) throws Exception {
        if (value != null) {
            final Class<?> klass = values.getClass();
            final Method method = klass.getMethod("put", String.class, value.getClass());
            method.invoke(values, key, value);
        }
    }



    // ===========================================


    /*

        These methods iterate over Objects and populates ContentValues based off the SQLiteTable @Columns provided.

     */


    public static ContentValues[] values(final Class<? extends SQLiteTable> klass, final List<?> data) throws Exception {
        final List<ContentValues> result = new ArrayList<>();
        for (final Object object : data) {
            final ContentValues values = new ContentValues();
            getColumns(klass, object, values);
            result.add(values);
        }
        return result.toArray(new ContentValues[result.size()]);
    }

    public static ContentValues[] values(final Class<? extends SQLiteTable> klass, final JSONArray data) throws Exception {
        final List<ContentValues> result = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            final ContentValues values = new ContentValues();
            getColumns(klass, data.getJSONObject(i), values);
            result.add(values);
        }
        return result.toArray(new ContentValues[result.size()]);
    }

    public static ContentValues values(final Class<? extends SQLiteTable> klass, final Object data) throws Exception {
        final ContentValues values = new ContentValues();
        try {
            getColumns(klass, data, values);
        } catch (Exception e) {
            Logger.ex(e);
        }
        return values;
    }

    private static void getColumns(final Class<?> klass, final Object data, final ContentValues values) throws Exception {
        final Field[] fields = klass.getFields();
        for (final Field field : fields) {
            getField(field, data, values);
        }

        final Class<?>[] klasses = klass.getDeclaredClasses();
        for (final Class<?> k : klasses) {
            getColumns(k, data, values);
        }
    }

    private static void getField(final Field field, final Object data, final ContentValues values) throws Exception {

        final Column annotation = field.getAnnotation(Column.class);
        if (annotation == null) return;

        final String name = (String) field.get(null);

        if (data instanceof Map) {
            getFieldFromMap(name, (Map<String, Object>) data, values);

        } else if (data instanceof JSONObject) {
            getFieldFromJson(name, (JSONObject) data, values);

        } else {
            getFieldFromObject(name, data, values);
        }
    }

    private static void getFieldFromMap(final String key, final Map<String, Object> data, final ContentValues values) throws Exception {

        if (data.containsKey(key)) {
            final Object value = data.get(key);

            addValuesObject(values, key, value);
        }
    }

    private static void getFieldFromJson(final String key, final JSONObject data, final ContentValues values) throws Exception {

        if (data.has(key)) {
            final Object value = data.get(key);

            addValuesObject(values, key, value);
        }
    }

    private static void getFieldFromObject(final String name, final Object data, final ContentValues values) throws Exception {

        try {
            final Field field = data.getClass().getDeclaredField(name);
            field.setAccessible(true);

            addValuesObject(values, name, field.get(data));

        } catch (final NoSuchFieldException e) {
            // do nothing
        }
    }



    // ===========================================



    public static Cursor getCursor(final Class<?> klass, final ContentValues[] values) {

        try {
            final String[] columns = getColumnNames(klass);
            return getCursor(columns, values);

        } catch (final Exception e) {
            Logger.ex(e);
            return new MatrixCursor(new String[] {"_id"});
        }
    }

    private static Cursor getCursor(final String[] columns, final ContentValues[] values) {
        final MatrixCursor cursor = new MatrixCursor(columns);

        if (values == null || values.length == 0) {
            return cursor;
        }

        for (final ContentValues topic : values) {
            cursor.addRow(getRow(columns, topic));
        }

        return cursor;
    }

    private static String[] getRow(final String[] columns, final ContentValues value) {
        final String[] row = new String[columns.length];

        for (int i = 0; i < columns.length; i++) {
            row[i] = value.getAsString(columns[i]);
        }

        return row;
    }

    private static String[] getColumnNames(final Class<?> klass) throws IllegalAccessException {
        final List<String> result = new ArrayList<>();

        final Field[] fields = klass.getFields();
        for (final Field field : fields) {
            final Column columnType = field.getAnnotation(Column.class);
            if (columnType != null) {
                result.add((String) field.get(null));
            }
        }

        return result.toArray(new String[result.size()]);
    }




    // ===========================================



    public static Map<String, String> toMap(final Object object) {
        final HashMap<String, String> map = new HashMap<>();
        try {
            addFieldsToMap(object, map, object.getClass());
        } catch (final Exception e) {
            Logger.ex(e);
        }
        return map;
    }

    private static void addFieldsToMap(final Object object, final HashMap<String, String> map, final Class<?> klass) throws Exception {
        final Field[] fields = klass.getDeclaredFields();

        for (final Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), String.valueOf(field.get(object)));
        }

        final Class<?> superKlass = klass.getSuperclass();
        if (!superKlass.equals(Object.class)) {
            addFieldsToMap(object, map, superKlass);
        }
    }

}
