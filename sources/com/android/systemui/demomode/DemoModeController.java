package com.android.systemui.demomode;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.util.Assert;
import com.android.systemui.util.settings.GlobalSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: DemoModeController.kt */
public final class DemoModeController implements CallbackController<DemoMode>, Dumpable {
    @NotNull
    private final DemoModeController$broadcastReceiver$1 broadcastReceiver;
    @NotNull
    private final Context context;
    @NotNull
    private final DumpManager dumpManager;
    @NotNull
    private final GlobalSettings globalSettings;
    private boolean initialized;
    private boolean isInDemoMode;
    @NotNull
    private final Map<String, List<DemoMode>> receiverMap;
    @NotNull
    private final List<DemoMode> receivers = new ArrayList();
    @NotNull
    private final DemoModeController$tracker$1 tracker;

    public DemoModeController(@NotNull Context context2, @NotNull DumpManager dumpManager2, @NotNull GlobalSettings globalSettings2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        Intrinsics.checkNotNullParameter(globalSettings2, "globalSettings");
        this.context = context2;
        this.dumpManager = dumpManager2;
        this.globalSettings = globalSettings2;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        List<String> list = DemoMode.COMMANDS;
        Intrinsics.checkNotNullExpressionValue(list, "COMMANDS");
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (String str : list) {
            Intrinsics.checkNotNullExpressionValue(str, "command");
            arrayList.add((List) linkedHashMap.put(str, new ArrayList()));
        }
        this.receiverMap = linkedHashMap;
        this.tracker = new DemoModeController$tracker$1(this, this.context);
        this.broadcastReceiver = new DemoModeController$broadcastReceiver$1(this);
    }

    public final boolean isInDemoMode() {
        return this.isInDemoMode;
    }

    public final boolean isAvailable() {
        return this.tracker.isDemoModeAvailable();
    }

    public final void initialize() {
        if (!this.initialized) {
            this.initialized = true;
            this.dumpManager.registerDumpable("DemoModeController", this);
            this.tracker.startTracking();
            this.isInDemoMode = this.tracker.isInDemoMode();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.android.systemui.demo");
            this.context.registerReceiverAsUser(this.broadcastReceiver, UserHandle.ALL, intentFilter, "android.permission.DUMP", (Handler) null);
            return;
        }
        throw new IllegalStateException("Already initialized");
    }

    public void addCallback(@NotNull DemoMode demoMode) {
        Intrinsics.checkNotNullParameter(demoMode, "listener");
        List<String> demoCommands = demoMode.demoCommands();
        Intrinsics.checkNotNullExpressionValue(demoCommands, "commands");
        for (String str : demoCommands) {
            if (this.receiverMap.containsKey(str)) {
                List list = this.receiverMap.get(str);
                Intrinsics.checkNotNull(list);
                list.add(demoMode);
            } else {
                throw new IllegalStateException("Command (" + str + ") not recognized. See DemoMode.java for valid commands");
            }
        }
        synchronized (this) {
            this.receivers.add(demoMode);
        }
        if (this.isInDemoMode) {
            demoMode.onDemoModeStarted();
        }
    }

    public void removeCallback(@NotNull DemoMode demoMode) {
        Intrinsics.checkNotNullParameter(demoMode, "listener");
        synchronized (this) {
            List<String> demoCommands = demoMode.demoCommands();
            Intrinsics.checkNotNullExpressionValue(demoCommands, "listener.demoCommands()");
            for (String str : demoCommands) {
                List list = this.receiverMap.get(str);
                Intrinsics.checkNotNull(list);
                list.remove(demoMode);
            }
            this.receivers.remove(demoMode);
        }
    }

    /* access modifiers changed from: private */
    public final void setIsDemoModeAllowed(boolean z) {
        if (this.isInDemoMode && !z) {
            requestFinishDemoMode();
        }
    }

    /* access modifiers changed from: private */
    public final void enterDemoMode() {
        List<T> list;
        this.isInDemoMode = true;
        Assert.isMainThread();
        synchronized (this) {
            list = CollectionsKt___CollectionsKt.toList(this.receivers);
            Unit unit = Unit.INSTANCE;
        }
        for (T onDemoModeStarted : list) {
            onDemoModeStarted.onDemoModeStarted();
        }
    }

    /* access modifiers changed from: private */
    public final void exitDemoMode() {
        List<T> list;
        this.isInDemoMode = false;
        Assert.isMainThread();
        synchronized (this) {
            list = CollectionsKt___CollectionsKt.toList(this.receivers);
            Unit unit = Unit.INSTANCE;
        }
        for (T onDemoModeFinished : list) {
            onDemoModeFinished.onDemoModeFinished();
        }
    }

    public final void dispatchDemoCommand(@NotNull String str, @NotNull Bundle bundle) {
        Intrinsics.checkNotNullParameter(str, "command");
        Intrinsics.checkNotNullParameter(bundle, "args");
        Assert.isMainThread();
        if (isAvailable()) {
            if (Intrinsics.areEqual((Object) str, (Object) "enter")) {
                enterDemoMode();
            } else if (Intrinsics.areEqual((Object) str, (Object) "exit")) {
                exitDemoMode();
            } else if (!this.isInDemoMode) {
                enterDemoMode();
            }
            List<DemoMode> list = this.receiverMap.get(str);
            Intrinsics.checkNotNull(list);
            for (DemoMode dispatchDemoCommand : list) {
                dispatchDemoCommand.dispatchDemoCommand(str, bundle);
            }
        }
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        List<T> list;
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println("DemoModeController state -");
        printWriter.println(Intrinsics.stringPlus("  isInDemoMode=", Boolean.valueOf(this.isInDemoMode)));
        printWriter.println(Intrinsics.stringPlus("  isDemoModeAllowed=", Boolean.valueOf(isAvailable())));
        printWriter.print("  receivers=[");
        synchronized (this) {
            list = CollectionsKt___CollectionsKt.toList(this.receivers);
            Unit unit = Unit.INSTANCE;
        }
        for (T t : list) {
            printWriter.print(Intrinsics.stringPlus(" ", t.getClass().getSimpleName()));
        }
        printWriter.println(" ]");
        printWriter.println("  receiverMap= [");
        for (String str : this.receiverMap.keySet()) {
            printWriter.print("    " + str + " : [");
            List<DemoMode> list2 = this.receiverMap.get(str);
            Intrinsics.checkNotNull(list2);
            ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list2, 10));
            for (DemoMode demoMode : list2) {
                arrayList.add(demoMode.getClass().getSimpleName());
            }
            printWriter.println(Intrinsics.stringPlus(CollectionsKt___CollectionsKt.joinToString$default(arrayList, ",", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null), " ]"));
        }
    }

    public final void requestSetDemoModeAllowed(boolean z) {
        setGlobal("sysui_demo_allowed", z ? 1 : 0);
    }

    public final void requestStartDemoMode() {
        setGlobal("sysui_tuner_demo_on", 1);
    }

    public final void requestFinishDemoMode() {
        setGlobal("sysui_tuner_demo_on", 0);
    }

    private final void setGlobal(String str, int i) {
        this.globalSettings.putInt(str, i);
    }
}
