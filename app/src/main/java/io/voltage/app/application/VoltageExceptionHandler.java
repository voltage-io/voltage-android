package io.voltage.app.application;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import io.voltage.app.requests.CrashInsert;


public class VoltageExceptionHandler implements UncaughtExceptionHandler {

    private final Context mContext;
    private final UncaughtExceptionHandler mHandler;

    public VoltageExceptionHandler(final Context context, final UncaughtExceptionHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable e) {

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        e.printStackTrace(printWriter);

        final String stacktrace = stringWriter.toString();
        final CrashInsert request = new CrashInsert(stacktrace);

        VoltageExecutor.execute(mContext, request);

        printWriter.close();

        mHandler.uncaughtException(thread, e);
    }
}
