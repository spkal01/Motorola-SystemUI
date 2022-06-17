package com.android.systemui.statusbar.commandline;

import java.util.concurrent.FutureTask;
import kotlin.Unit;

/* compiled from: CommandRegistry.kt */
final class CommandRegistry$onShellCommand$1 implements Runnable {
    final /* synthetic */ FutureTask<Unit> $task;

    CommandRegistry$onShellCommand$1(FutureTask<Unit> futureTask) {
        this.$task = futureTask;
    }

    public final void run() {
        this.$task.run();
    }
}
