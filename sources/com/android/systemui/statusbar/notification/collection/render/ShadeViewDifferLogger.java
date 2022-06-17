package com.android.systemui.statusbar.notification.collection.render;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ShadeViewDifferLogger.kt */
public final class ShadeViewDifferLogger {
    @NotNull
    private final LogBuffer buffer;

    public ShadeViewDifferLogger(@NotNull LogBuffer logBuffer) {
        Intrinsics.checkNotNullParameter(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logDetachingChild(@NotNull String str, boolean z, @Nullable String str2, @Nullable String str3) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        ShadeViewDifferLogger$logDetachingChild$2 shadeViewDifferLogger$logDetachingChild$2 = ShadeViewDifferLogger$logDetachingChild$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifViewManager", logLevel, shadeViewDifferLogger$logDetachingChild$2);
            obtain.setStr1(str);
            obtain.setBool1(z);
            obtain.setStr2(str2);
            obtain.setStr3(str3);
            logBuffer.push(obtain);
        }
    }

    public final void logSkippingDetach(@NotNull String str, @Nullable String str2) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        ShadeViewDifferLogger$logSkippingDetach$2 shadeViewDifferLogger$logSkippingDetach$2 = ShadeViewDifferLogger$logSkippingDetach$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifViewManager", logLevel, shadeViewDifferLogger$logSkippingDetach$2);
            obtain.setStr1(str);
            obtain.setStr2(str2);
            logBuffer.push(obtain);
        }
    }

    public final void logAttachingChild(@NotNull String str, @NotNull String str2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(str2, "parent");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        ShadeViewDifferLogger$logAttachingChild$2 shadeViewDifferLogger$logAttachingChild$2 = ShadeViewDifferLogger$logAttachingChild$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifViewManager", logLevel, shadeViewDifferLogger$logAttachingChild$2);
            obtain.setStr1(str);
            obtain.setStr2(str2);
            logBuffer.push(obtain);
        }
    }

    public final void logMovingChild(@NotNull String str, @NotNull String str2, int i) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(str2, "parent");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        ShadeViewDifferLogger$logMovingChild$2 shadeViewDifferLogger$logMovingChild$2 = ShadeViewDifferLogger$logMovingChild$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifViewManager", logLevel, shadeViewDifferLogger$logMovingChild$2);
            obtain.setStr1(str);
            obtain.setStr2(str2);
            obtain.setInt1(i);
            logBuffer.push(obtain);
        }
    }

    public final void logDuplicateNodeInTree(@NotNull NodeSpec nodeSpec, @NotNull RuntimeException runtimeException) {
        Intrinsics.checkNotNullParameter(nodeSpec, "node");
        Intrinsics.checkNotNullParameter(runtimeException, "ex");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.ERROR;
        ShadeViewDifferLogger$logDuplicateNodeInTree$2 shadeViewDifferLogger$logDuplicateNodeInTree$2 = ShadeViewDifferLogger$logDuplicateNodeInTree$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifViewManager", logLevel, shadeViewDifferLogger$logDuplicateNodeInTree$2);
            obtain.setStr1(runtimeException.toString());
            obtain.setStr2(NodeControllerKt.treeSpecToStr(nodeSpec));
            logBuffer.push(obtain);
        }
    }
}
