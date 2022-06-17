package com.android.systemui.flags;

import android.os.SystemProperties;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: SystemPropertiesHelper.kt */
public final class SystemPropertiesHelper {
    public final boolean getBoolean(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "name");
        return SystemProperties.getBoolean(str, z);
    }
}
