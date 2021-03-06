package com.android.systemui.controls.controller;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsController.kt */
public final class SeedResponse {
    private final boolean accepted;
    @NotNull
    private final String packageName;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SeedResponse)) {
            return false;
        }
        SeedResponse seedResponse = (SeedResponse) obj;
        return Intrinsics.areEqual((Object) this.packageName, (Object) seedResponse.packageName) && this.accepted == seedResponse.accepted;
    }

    public int hashCode() {
        int hashCode = this.packageName.hashCode() * 31;
        boolean z = this.accepted;
        if (z) {
            z = true;
        }
        return hashCode + (z ? 1 : 0);
    }

    @NotNull
    public String toString() {
        return "SeedResponse(packageName=" + this.packageName + ", accepted=" + this.accepted + ')';
    }

    public SeedResponse(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "packageName");
        this.packageName = str;
        this.accepted = z;
    }

    public final boolean getAccepted() {
        return this.accepted;
    }

    @NotNull
    public final String getPackageName() {
        return this.packageName;
    }
}
