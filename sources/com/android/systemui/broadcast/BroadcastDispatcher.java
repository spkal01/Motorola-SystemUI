package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.logging.BroadcastDispatcherLogger;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.settings.UserTracker;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: BroadcastDispatcher.kt */
public class BroadcastDispatcher implements Dumpable {
    @NotNull
    private final Executor bgExecutor;
    @NotNull
    private final Looper bgLooper;
    @NotNull
    private final Context context;
    @NotNull
    private final DumpManager dumpManager;
    @NotNull
    private final BroadcastDispatcher$handler$1 handler;
    @NotNull
    private final BroadcastDispatcherLogger logger;
    /* access modifiers changed from: private */
    @NotNull
    public final SparseArray<UserBroadcastDispatcher> receiversByUser = new SparseArray<>(20);
    /* access modifiers changed from: private */
    @NotNull
    public final UserTracker userTracker;

    public final void registerReceiver(@NotNull BroadcastReceiver broadcastReceiver, @NotNull IntentFilter intentFilter) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        Intrinsics.checkNotNullParameter(intentFilter, "filter");
        registerReceiver$default(this, broadcastReceiver, intentFilter, (Executor) null, (UserHandle) null, 12, (Object) null);
    }

    public final void registerReceiverWithHandler(@NotNull BroadcastReceiver broadcastReceiver, @NotNull IntentFilter intentFilter, @NotNull Handler handler2) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        Intrinsics.checkNotNullParameter(intentFilter, "filter");
        Intrinsics.checkNotNullParameter(handler2, "handler");
        registerReceiverWithHandler$default(this, broadcastReceiver, intentFilter, handler2, (UserHandle) null, 8, (Object) null);
    }

    public BroadcastDispatcher(@NotNull Context context2, @NotNull Looper looper, @NotNull Executor executor, @NotNull DumpManager dumpManager2, @NotNull BroadcastDispatcherLogger broadcastDispatcherLogger, @NotNull UserTracker userTracker2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(looper, "bgLooper");
        Intrinsics.checkNotNullParameter(executor, "bgExecutor");
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        Intrinsics.checkNotNullParameter(broadcastDispatcherLogger, "logger");
        Intrinsics.checkNotNullParameter(userTracker2, "userTracker");
        this.context = context2;
        this.bgLooper = looper;
        this.bgExecutor = executor;
        this.dumpManager = dumpManager2;
        this.logger = broadcastDispatcherLogger;
        this.userTracker = userTracker2;
        this.handler = new BroadcastDispatcher$handler$1(this, looper);
    }

    public final void initialize() {
        DumpManager dumpManager2 = this.dumpManager;
        String name = BroadcastDispatcher.class.getName();
        Intrinsics.checkNotNullExpressionValue(name, "javaClass.name");
        dumpManager2.registerDumpable(name, this);
    }

    public static /* synthetic */ void registerReceiverWithHandler$default(BroadcastDispatcher broadcastDispatcher, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Handler handler2, UserHandle userHandle, int i, Object obj) {
        if (obj == null) {
            if ((i & 8) != 0) {
                userHandle = broadcastDispatcher.context.getUser();
                Intrinsics.checkNotNullExpressionValue(userHandle, "fun registerReceiverWithHandler(\n        receiver: BroadcastReceiver,\n        filter: IntentFilter,\n        handler: Handler,\n        user: UserHandle = context.user\n    ) {\n        registerReceiver(receiver, filter, HandlerExecutor(handler), user)\n    }");
            }
            broadcastDispatcher.registerReceiverWithHandler(broadcastReceiver, intentFilter, handler2, userHandle);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: registerReceiverWithHandler");
    }

    public void registerReceiverWithHandler(@NotNull BroadcastReceiver broadcastReceiver, @NotNull IntentFilter intentFilter, @NotNull Handler handler2, @NotNull UserHandle userHandle) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        Intrinsics.checkNotNullParameter(intentFilter, "filter");
        Intrinsics.checkNotNullParameter(handler2, "handler");
        Intrinsics.checkNotNullParameter(userHandle, "user");
        registerReceiver(broadcastReceiver, intentFilter, new HandlerExecutor(handler2), userHandle);
    }

    public static /* synthetic */ void registerReceiver$default(BroadcastDispatcher broadcastDispatcher, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Executor executor, UserHandle userHandle, int i, Object obj) {
        if (obj == null) {
            if ((i & 4) != 0) {
                executor = null;
            }
            if ((i & 8) != 0) {
                userHandle = null;
            }
            broadcastDispatcher.registerReceiver(broadcastReceiver, intentFilter, executor, userHandle);
            return;
        }
        throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: registerReceiver");
    }

    public void registerReceiver(@NotNull BroadcastReceiver broadcastReceiver, @NotNull IntentFilter intentFilter, @Nullable Executor executor, @Nullable UserHandle userHandle) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        Intrinsics.checkNotNullParameter(intentFilter, "filter");
        checkFilter(intentFilter);
        BroadcastDispatcher$handler$1 broadcastDispatcher$handler$1 = this.handler;
        if (executor == null) {
            executor = this.context.getMainExecutor();
        }
        Intrinsics.checkNotNullExpressionValue(executor, "executor ?: context.mainExecutor");
        if (userHandle == null) {
            userHandle = this.context.getUser();
        }
        Intrinsics.checkNotNullExpressionValue(userHandle, "user ?: context.user");
        broadcastDispatcher$handler$1.obtainMessage(0, new ReceiverData(broadcastReceiver, intentFilter, executor, userHandle)).sendToTarget();
    }

    private final void checkFilter(IntentFilter intentFilter) {
        StringBuilder sb = new StringBuilder();
        if (intentFilter.countActions() == 0) {
            sb.append("Filter must contain at least one action. ");
        }
        if (intentFilter.countDataAuthorities() != 0) {
            sb.append("Filter cannot contain DataAuthorities. ");
        }
        if (intentFilter.countDataPaths() != 0) {
            sb.append("Filter cannot contain DataPaths. ");
        }
        if (intentFilter.countDataSchemes() != 0) {
            sb.append("Filter cannot contain DataSchemes. ");
        }
        if (intentFilter.countDataTypes() != 0) {
            sb.append("Filter cannot contain DataTypes. ");
        }
        if (intentFilter.getPriority() != 0) {
            sb.append("Filter cannot modify priority. ");
        }
        if (!TextUtils.isEmpty(sb)) {
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void unregisterReceiver(@NotNull BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        this.handler.obtainMessage(1, broadcastReceiver).sendToTarget();
    }

    /* access modifiers changed from: protected */
    @NotNull
    @VisibleForTesting
    public UserBroadcastDispatcher createUBRForUser(int i) {
        return new UserBroadcastDispatcher(this.context, i, this.bgLooper, this.bgExecutor, this.logger);
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println("Broadcast dispatcher:");
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.increaseIndent();
        int size = this.receiversByUser.size();
        if (size > 0) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                indentingPrintWriter.println(Intrinsics.stringPlus("User ", Integer.valueOf(this.receiversByUser.keyAt(i))));
                this.receiversByUser.valueAt(i).dump(fileDescriptor, indentingPrintWriter, strArr);
                if (i2 >= size) {
                    break;
                }
                i = i2;
            }
        }
        indentingPrintWriter.decreaseIndent();
    }
}
