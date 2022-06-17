package com.motorola.systemui.cli.navgesture.executors;

import android.os.Handler;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface LooperExecutor extends Executor {
    void executeNext(Runnable runnable);

    Handler getHandler();

    <T> Future<T> submit(Callable<T> callable);
}
