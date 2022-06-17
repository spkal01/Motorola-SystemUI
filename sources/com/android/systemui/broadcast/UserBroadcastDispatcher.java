package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.logging.BroadcastDispatcherLogger;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* compiled from: UserBroadcastDispatcher.kt */
public class UserBroadcastDispatcher implements Dumpable {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private static final AtomicInteger index = new AtomicInteger(0);
    @NotNull
    private final ArrayMap<String, ActionReceiver> actionsToActionsReceivers = new ArrayMap<>();
    @NotNull
    private final Executor bgExecutor;
    /* access modifiers changed from: private */
    @NotNull
    public final UserBroadcastDispatcher$bgHandler$1 bgHandler;
    @NotNull
    private final Looper bgLooper;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    /* access modifiers changed from: private */
    @NotNull
    public final BroadcastDispatcherLogger logger;
    @NotNull
    private final ArrayMap<BroadcastReceiver, Set<String>> receiverToActions = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final int userId;

    /* renamed from: getActionsToActionsReceivers$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m12xcb79c6f() {
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        boolean z = printWriter instanceof IndentingPrintWriter;
        if (z) {
            ((IndentingPrintWriter) printWriter).increaseIndent();
        }
        for (Map.Entry next : mo12166x51e40e6f().entrySet()) {
            printWriter.println(Intrinsics.stringPlus((String) next.getKey(), ":"));
            ((ActionReceiver) next.getValue()).dump(fileDescriptor, printWriter, strArr);
        }
        if (z) {
            ((IndentingPrintWriter) printWriter).decreaseIndent();
        }
    }

    public UserBroadcastDispatcher(@NotNull Context context2, int i, @NotNull Looper looper, @NotNull Executor executor, @NotNull BroadcastDispatcherLogger broadcastDispatcherLogger) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(looper, "bgLooper");
        Intrinsics.checkNotNullParameter(executor, "bgExecutor");
        Intrinsics.checkNotNullParameter(broadcastDispatcherLogger, "logger");
        this.context = context2;
        this.userId = i;
        this.bgLooper = looper;
        this.bgExecutor = executor;
        this.logger = broadcastDispatcherLogger;
        this.bgHandler = new UserBroadcastDispatcher$bgHandler$1(this, looper);
    }

    /* compiled from: UserBroadcastDispatcher.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    @NotNull
    /* renamed from: getActionsToActionsReceivers$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final ArrayMap<String, ActionReceiver> mo12166x51e40e6f() {
        return this.actionsToActionsReceivers;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
    /* renamed from: isReceiverReferenceHeld$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean mo12167xbd20662d(@org.jetbrains.annotations.NotNull android.content.BroadcastReceiver r5) {
        /*
            r4 = this;
            java.lang.String r0 = "receiver"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r5, r0)
            android.util.ArrayMap<java.lang.String, com.android.systemui.broadcast.ActionReceiver> r0 = r4.actionsToActionsReceivers
            java.util.Collection r0 = r0.values()
            java.lang.String r1 = "actionsToActionsReceivers.values"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
            boolean r1 = r0.isEmpty()
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x001a
        L_0x0018:
            r0 = r3
            goto L_0x0031
        L_0x001a:
            java.util.Iterator r0 = r0.iterator()
        L_0x001e:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0018
            java.lang.Object r1 = r0.next()
            com.android.systemui.broadcast.ActionReceiver r1 = (com.android.systemui.broadcast.ActionReceiver) r1
            boolean r1 = r1.hasReceiver(r5)
            if (r1 == 0) goto L_0x001e
            r0 = r2
        L_0x0031:
            if (r0 != 0) goto L_0x003d
            android.util.ArrayMap<android.content.BroadcastReceiver, java.util.Set<java.lang.String>> r4 = r4.receiverToActions
            boolean r4 = r4.containsKey(r5)
            if (r4 == 0) goto L_0x003c
            goto L_0x003d
        L_0x003c:
            r2 = r3
        L_0x003d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.broadcast.UserBroadcastDispatcher.mo12167xbd20662d(android.content.BroadcastReceiver):boolean");
    }

    public final void registerReceiver(@NotNull ReceiverData receiverData) {
        Intrinsics.checkNotNullParameter(receiverData, "receiverData");
        this.bgHandler.obtainMessage(0, receiverData).sendToTarget();
    }

    public final void unregisterReceiver(@NotNull BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        this.bgHandler.obtainMessage(1, broadcastReceiver).sendToTarget();
    }

    /* access modifiers changed from: private */
    public final void handleRegisterReceiver(ReceiverData receiverData) {
        Preconditions.checkState(this.bgHandler.getLooper().isCurrentThread(), "This method should only be called from BG thread");
        ArrayMap<BroadcastReceiver, Set<String>> arrayMap = this.receiverToActions;
        BroadcastReceiver receiver = receiverData.getReceiver();
        Set<String> set = arrayMap.get(receiver);
        if (set == null) {
            set = new ArraySet<>();
            arrayMap.put(receiver, set);
        }
        Collection collection = set;
        Iterator<String> actionsIterator = receiverData.getFilter().actionsIterator();
        Sequence<T> asSequence = actionsIterator == null ? null : SequencesKt__SequencesKt.asSequence(actionsIterator);
        if (asSequence == null) {
            asSequence = SequencesKt__SequencesKt.emptySequence();
        }
        boolean unused = CollectionsKt__MutableCollectionsKt.addAll(collection, asSequence);
        Iterator<String> actionsIterator2 = receiverData.getFilter().actionsIterator();
        Intrinsics.checkNotNullExpressionValue(actionsIterator2, "receiverData.filter.actionsIterator()");
        while (actionsIterator2.hasNext()) {
            String next = actionsIterator2.next();
            ArrayMap<String, ActionReceiver> actionsToActionsReceivers$frameworks__base__packages__SystemUI__android_common__SystemUI_core = mo12166x51e40e6f();
            ActionReceiver actionReceiver = actionsToActionsReceivers$frameworks__base__packages__SystemUI__android_common__SystemUI_core.get(next);
            if (actionReceiver == null) {
                Intrinsics.checkNotNullExpressionValue(next, "it");
                actionReceiver = mo12165xe87e3b27(next);
                actionsToActionsReceivers$frameworks__base__packages__SystemUI__android_common__SystemUI_core.put(next, actionReceiver);
            }
            actionReceiver.addReceiverData(receiverData);
        }
        this.logger.logReceiverRegistered(this.userId, receiverData.getReceiver());
    }

    @NotNull
    /* renamed from: createActionReceiver$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public ActionReceiver mo12165xe87e3b27(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "action");
        return new ActionReceiver(str, this.userId, new UserBroadcastDispatcher$createActionReceiver$1(this), new UserBroadcastDispatcher$createActionReceiver$2(this, str), this.bgExecutor, this.logger);
    }

    /* access modifiers changed from: private */
    public final void handleUnregisterReceiver(BroadcastReceiver broadcastReceiver) {
        Preconditions.checkState(this.bgHandler.getLooper().isCurrentThread(), "This method should only be called from BG thread");
        Object orDefault = this.receiverToActions.getOrDefault(broadcastReceiver, new LinkedHashSet());
        Intrinsics.checkNotNullExpressionValue(orDefault, "receiverToActions.getOrDefault(receiver, mutableSetOf())");
        for (String str : (Iterable) orDefault) {
            ActionReceiver actionReceiver = mo12166x51e40e6f().get(str);
            if (actionReceiver != null) {
                actionReceiver.removeReceiver(broadcastReceiver);
            }
        }
        this.receiverToActions.remove(broadcastReceiver);
        this.logger.logReceiverUnregistered(this.userId, broadcastReceiver);
    }
}
