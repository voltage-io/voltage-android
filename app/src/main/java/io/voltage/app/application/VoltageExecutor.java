package io.voltage.app.application;

import android.content.Context;

import io.pivotal.arca.dispatcher.Batch;
import io.pivotal.arca.dispatcher.BatchResult;
import io.pivotal.arca.dispatcher.Delete;
import io.pivotal.arca.dispatcher.DeleteResult;
import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.dispatcher.InsertResult;
import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.dispatcher.Update;
import io.pivotal.arca.dispatcher.UpdateResult;
import io.pivotal.arca.fragments.ArcaExecutorFactory;
import io.pivotal.arca.monitor.ArcaExecutor;

public class VoltageExecutor {

    public static InsertResult execute(final Context context, final Insert request) {
        final ArcaExecutor executor = ArcaExecutorFactory.generateExecutor(context);
        return executor.execute(request);
    }

    public static UpdateResult execute(final Context context, final Update request) {
        final ArcaExecutor executor = ArcaExecutorFactory.generateExecutor(context);
        return executor.execute(request);
    }

    public static DeleteResult execute(final Context context, final Delete request) {
        final ArcaExecutor executor = ArcaExecutorFactory.generateExecutor(context);
        return executor.execute(request);
    }

    public static QueryResult execute(final Context context, final Query request) {
        final ArcaExecutor executor = ArcaExecutorFactory.generateExecutor(context);
        return executor.execute(request);
    }

    public static BatchResult execute(final Context context, final Batch request) {
        final ArcaExecutor executor = ArcaExecutorFactory.generateExecutor(context);
        return executor.execute(request);
    }
}
