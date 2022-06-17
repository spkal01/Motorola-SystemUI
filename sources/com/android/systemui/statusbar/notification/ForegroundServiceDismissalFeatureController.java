package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.systemui.util.DeviceConfigProxy;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ForegroundServiceDismissalFeatureController.kt */
public final class ForegroundServiceDismissalFeatureController {
    @NotNull
    private final Context context;
    @NotNull
    private final DeviceConfigProxy proxy;

    public ForegroundServiceDismissalFeatureController(@NotNull DeviceConfigProxy deviceConfigProxy, @NotNull Context context2) {
        Intrinsics.checkNotNullParameter(deviceConfigProxy, "proxy");
        Intrinsics.checkNotNullParameter(context2, "context");
        this.proxy = deviceConfigProxy;
        this.context = context2;
    }

    public final boolean isForegroundServiceDismissalEnabled() {
        return ForegroundServiceDismissalFeatureControllerKt.isEnabled(this.proxy);
    }
}
