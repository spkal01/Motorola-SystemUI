package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger$logRemoteExceptionOnClearAllNotifications$2 */
/* compiled from: NotifCollectionLogger.kt */
final class C1575x8e3a9df8 extends Lambda implements Function1<LogMessage, String> {
    public static final C1575x8e3a9df8 INSTANCE = new C1575x8e3a9df8();

    C1575x8e3a9df8() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("RemoteException while attempting to clear all notifications:\n", logMessage.getStr1());
    }
}
