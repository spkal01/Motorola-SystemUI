package com.android.systemui.p006qs.tileimpl;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.tileimpl.HeightOverrideable */
/* compiled from: HeightOverrideable.kt */
public interface HeightOverrideable {
    void resetOverride();

    void setHeightOverride(int i);

    /* renamed from: com.android.systemui.qs.tileimpl.HeightOverrideable$DefaultImpls */
    /* compiled from: HeightOverrideable.kt */
    public static final class DefaultImpls {
        public static void resetOverride(@NotNull HeightOverrideable heightOverrideable) {
            Intrinsics.checkNotNullParameter(heightOverrideable, "this");
            heightOverrideable.setHeightOverride(-1);
        }
    }
}
