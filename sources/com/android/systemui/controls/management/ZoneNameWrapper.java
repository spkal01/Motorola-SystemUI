package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsModel.kt */
public final class ZoneNameWrapper extends ElementWrapper {
    @NotNull
    private final CharSequence zoneName;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof ZoneNameWrapper) && Intrinsics.areEqual((Object) this.zoneName, (Object) ((ZoneNameWrapper) obj).zoneName);
    }

    public int hashCode() {
        return this.zoneName.hashCode();
    }

    @NotNull
    public String toString() {
        return "ZoneNameWrapper(zoneName=" + this.zoneName + ')';
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ZoneNameWrapper(@NotNull CharSequence charSequence) {
        super((DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(charSequence, "zoneName");
        this.zoneName = charSequence;
    }

    @NotNull
    public final CharSequence getZoneName() {
        return this.zoneName;
    }
}
