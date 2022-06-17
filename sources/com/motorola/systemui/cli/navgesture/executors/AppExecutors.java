package com.motorola.systemui.cli.navgesture.executors;

import android.os.HandlerThread;
import android.os.Looper;

public class AppExecutors {
    private static final LooperExecutor BACKGROUND_EXECUTOR = new LooperExecutorService(createAndStartNewLooper("ui-thread-helper", -2));
    private static final LooperExecutor MAIN_EXECUTOR = new LooperExecutorService(Looper.getMainLooper());

    /* renamed from: ui */
    public static LooperExecutor m97ui() {
        return MAIN_EXECUTOR;
    }

    public static LooperExecutor background() {
        return BACKGROUND_EXECUTOR;
    }

    public static Looper createAndStartNewLooper(String str, int i) {
        HandlerThread handlerThread = new HandlerThread(str, i);
        handlerThread.start();
        return handlerThread.getLooper();
    }
}
