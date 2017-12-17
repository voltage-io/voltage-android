package io.voltage.app.utils;

import android.database.MatrixCursor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.pivotal.arca.provider.ColumnName;

public class DataUtils {

    public static <T> MatrixCursor getCursor(final List<T> list) {

        try {

            final String[] columns = getColumns(list.get(0));
            final MatrixCursor cursor = new MatrixCursor(columns);

            int index = 0;
            for (final Object object : list) {
                final String[] row = new String[columns.length];
                final List<String> values = new ArrayList<>();
                values.add(String.valueOf(index++));
                values.addAll(getFieldValuesFromObject(object));
                values.addAll(getMethodValuesFromObject(object));
                cursor.addRow(values.toArray(row));
            }

            return cursor;

        } catch (final Exception e) {
            return new MatrixCursor(new String[] { "_id" });
        }
    }

    private static String[] getColumns(final Object object) throws IllegalAccessException {
        final Class<?> klass = object.getClass();
        final List<String> result = new ArrayList<>();
        result.add("_id");

        final Field[] fields = klass.getDeclaredFields();
        for (final Field field : fields) {
            final ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                result.add(annotation.value());
            }
        }

        final Method[] methods = klass.getDeclaredMethods();
        for (final Method method : methods) {
            final ColumnName annotation = method.getAnnotation(ColumnName.class);
            if (annotation != null) {
                result.add(annotation.value());
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private static List<String> getFieldValuesFromObject(final Object object) throws Exception {
        final Class<?> klass = object.getClass();
        final List<String> result = new ArrayList<>();

        final Field[] fields = klass.getDeclaredFields();
        for (final Field field : fields) {
            final ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                field.setAccessible(true);
                result.add((String) field.get(object));
            }
        }

        return result;
    }

    private static List<String> getMethodValuesFromObject(final Object object) throws Exception {
        final Class<?> klass = object.getClass();
        final List<String> result = new ArrayList<>();

        final Method[] methods = klass.getDeclaredMethods();
        for (final Method method : methods) {
            final ColumnName annotation = method.getAnnotation(ColumnName.class);
            if (annotation != null) {
                method.setAccessible(true);
                result.add((String) method.invoke(object));
            }
        }

        return result;
    }
}
