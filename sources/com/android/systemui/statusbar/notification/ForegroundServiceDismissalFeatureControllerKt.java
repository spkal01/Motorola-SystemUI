package com.android.systemui.statusbar.notification;

import com.android.systemui.util.DeviceConfigProxy;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

/* compiled from: ForegroundServiceDismissalFeatureController.kt */
public final class ForegroundServiceDismissalFeatureControllerKt {
    @Nullable
    private static Boolean sIsEnabled;

    /* access modifiers changed from: private */
    public static final boolean isEnabled(DeviceConfigProxy deviceConfigProxy) {
        if (sIsEnabled == null) {
            sIsEnabled = Boolean.valueOf(deviceConfigProxy.getBoolean("systemui", "notifications_allow_fgs_dismissal", false));
        }
        Boolean bool = sIsEnabled;
        Intrinsics.checkNotNull(bool);
        return bool.booleanValue();
    }
}
