package com.android.systemui.statusbar.commandline;

import java.io.PrintWriter;
import java.util.concurrent.Callable;
import kotlin.Unit;

/* compiled from: CommandRegistry.kt */
final class CommandRegistry$onShellCommand$task$1 implements Callable<Unit> {
    final /* synthetic */ String[] $args;
    final /* synthetic */ Command $command;
    final /* synthetic */ PrintWriter $pw;

    CommandRegistry$onShellCommand$task$1(Command command, PrintWriter printWriter, String[] strArr) {
        this.$command = command;
        this.$pw = printWriter;
        this.$args = strArr;
    }

    public final void call() {
        this.$command.execute(this.$pw, ArraysKt___ArraysKt.drop(this.$args, 1));
    }
}
