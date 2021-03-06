package com.android.systemui.broadcast.logging;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: BroadcastDispatcherLogger.kt */
public final class BroadcastDispatcherLogger {
    /* access modifiers changed from: private */
    @NotNull
    public final LogBuffer buffer;

    public BroadcastDispatcherLogger(@NotNull LogBuffer logBuffer) {
        Intrinsics.checkNotNullParameter(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logBroadcastReceived(int i, int i2, @NotNull Intent intent) {
        Intrinsics.checkNotNullParameter(intent, "intent");
        String intent2 = intent.toString();
        Intrinsics.checkNotNullExpressionValue(intent2, "intent.toString()");
        LogLevel logLevel = LogLevel.INFO;
        BroadcastDispatcherLogger$logBroadcastReceived$2 broadcastDispatcherLogger$logBroadcastReceived$2 = BroadcastDispatcherLogger$logBroadcastReceived$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("BroadcastDispatcherLog", logLevel, broadcastDispatcherLogger$logBroadcastReceived$2);
            obtain.setInt1(i);
            obtain.setInt2(i2);
            obtain.setStr1(intent2);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logBroadcastDispatched(int i, @Nullable String str, @NotNull BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        String broadcastReceiver2 = broadcastReceiver.toString();
        LogLevel logLevel = LogLevel.DEBUG;
        BroadcastDispatcherLogger$logBroadcastDispatched$2 broadcastDispatcherLogger$logBroadcastDispatched$2 = BroadcastDispatcherLogger$logBroadcastDispatched$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("BroadcastDispatcherLog", logLevel, broadcastDispatcherLogger$logBroadcastDispatched$2);
            obtain.setInt1(i);
            obtain.setStr1(str);
            obtain.setStr2(broadcastReceiver2);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logReceiverRegistered(int i, @NotNull BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        String broadcastReceiver2 = broadcastReceiver.toString();
        LogLevel logLevel = LogLevel.INFO;
        BroadcastDispatcherLogger$logReceiverRegistered$2 broadcastDispatcherLogger$logReceiverRegistered$2 = BroadcastDispatcherLogger$logReceiverRegistered$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("BroadcastDispatcherLog", logLevel, broadcastDispatcherLogger$logReceiverRegistered$2);
            obtain.setInt1(i);
            obtain.setStr1(broadcastReceiver2);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logReceiverUnregistered(int i, @NotNull BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkNotNullParameter(broadcastReceiver, "receiver");
        String broadcastReceiver2 = broadcastReceiver.toString();
        LogLevel logLevel = LogLevel.INFO;
        BroadcastDispatcherLogger$logReceiverUnregistered$2 broadcastDispatcherLogger$logReceiverUnregistered$2 = BroadcastDispatcherLogger$logReceiverUnregistered$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("BroadcastDispatcherLog", logLevel, broadcastDispatcherLogger$logReceiverUnregistered$2);
            obtain.setInt1(i);
            obtain.setStr1(broadcastReceiver2);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logContextReceiverRegistered(int i, @NotNull IntentFilter intentFilter) {
        String str;
        Intrinsics.checkNotNullParameter(intentFilter, "filter");
        Iterator<String> actionsIterator = intentFilter.actionsIterator();
        Intrinsics.checkNotNullExpressionValue(actionsIterator, "filter.actionsIterator()");
        String joinToString$default = SequencesKt___SequencesKt.joinToString$default(SequencesKt__SequencesKt.asSequence(actionsIterator), ",", "Actions(", ")", 0, (CharSequence) null, (Function1) null, 56, (Object) null);
        if (intentFilter.countCategories() != 0) {
            Iterator<String> categoriesIterator = intentFilter.categoriesIterator();
            Intrinsics.checkNotNullExpressionValue(categoriesIterator, "filter.categoriesIterator()");
            str = SequencesKt___SequencesKt.joinToString$default(SequencesKt__SequencesKt.asSequence(categoriesIterator), ",", "Categories(", ")", 0, (CharSequence) null, (Function1) null, 56, (Object) null);
        } else {
            str = "";
        }
        LogLevel logLevel = LogLevel.INFO;
        BroadcastDispatcherLogger$logContextReceiverRegistered$2 broadcastDispatcherLogger$logContextReceiverRegistered$2 = BroadcastDispatcherLogger$logContextReceiverRegistered$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("BroadcastDispatcherLog", logLevel, broadcastDispatcherLogger$logContextReceiverRegistered$2);
            obtain.setInt1(i);
            if (!Intrinsics.areEqual((Object) str, (Object) "")) {
                joinToString$default = joinToString$default + 10 + str;
            }
            obtain.setStr1(joinToString$default);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logContextReceiverUnregistered(int i, @NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "action");
        LogLevel logLevel = LogLevel.INFO;
        BroadcastDispatcherLogger$logContextReceiverUnregistered$2 broadcastDispatcherLogger$logContextReceiverUnregistered$2 = BroadcastDispatcherLogger$logContextReceiverUnregistered$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("BroadcastDispatcherLog", logLevel, broadcastDispatcherLogger$logContextReceiverUnregistered$2);
            obtain.setInt1(i);
            obtain.setStr1(str);
            access$getBuffer$p.push(obtain);
        }
    }
}
