package com.android.systemui.statusbar.commandline;

import android.content.Context;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: CommandRegistry.kt */
public final class CommandRegistry {
    @NotNull
    private final Map<String, CommandWrapper> commandMap = new LinkedHashMap();
    @NotNull
    private final Context context;
    private boolean initialized;
    @NotNull
    private final Executor mainExecutor;

    public CommandRegistry(@NotNull Context context2, @NotNull Executor executor) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(executor, "mainExecutor");
        this.context = context2;
        this.mainExecutor = executor;
    }

    @NotNull
    public final Context getContext() {
        return this.context;
    }

    public final synchronized void registerCommand(@NotNull String str, @NotNull Function0<? extends Command> function0, @NotNull Executor executor) {
        Intrinsics.checkNotNullParameter(str, "name");
        Intrinsics.checkNotNullParameter(function0, "commandFactory");
        Intrinsics.checkNotNullParameter(executor, "executor");
        if (this.commandMap.get(str) == null) {
            this.commandMap.put(str, new CommandWrapper(function0, executor));
        } else {
            throw new IllegalStateException("A command is already registered for (" + str + ')');
        }
    }

    public final synchronized void registerCommand(@NotNull String str, @NotNull Function0<? extends Command> function0) {
        Intrinsics.checkNotNullParameter(str, "name");
        Intrinsics.checkNotNullParameter(function0, "commandFactory");
        registerCommand(str, function0, this.mainExecutor);
    }

    public final synchronized void unregisterCommand(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "command");
        this.commandMap.remove(str);
    }

    private final void initializeCommands() {
        this.initialized = true;
        registerCommand("prefs", new CommandRegistry$initializeCommands$1(this));
    }

    public final void onShellCommand(@NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        if (!this.initialized) {
            initializeCommands();
        }
        if (strArr.length == 0) {
            help(printWriter);
            return;
        }
        CommandWrapper commandWrapper = this.commandMap.get(strArr[0]);
        if (commandWrapper == null) {
            help(printWriter);
            return;
        }
        FutureTask futureTask = new FutureTask(new CommandRegistry$onShellCommand$task$1(commandWrapper.getCommandFactory().invoke(), printWriter, strArr));
        commandWrapper.getExecutor().execute(new CommandRegistry$onShellCommand$1(futureTask));
        futureTask.get();
    }

    private final void help(PrintWriter printWriter) {
        printWriter.println("Usage: adb shell cmd statusbar <command>");
        printWriter.println("  known commands:");
        for (String stringPlus : this.commandMap.keySet()) {
            printWriter.println(Intrinsics.stringPlus("   ", stringPlus));
        }
    }
}
