package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.service.controls.Control;
import com.android.systemui.controls.controller.ControlInfo;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ControlWithState */
/* compiled from: ControlWithState.kt */
public final class ControlWithState {
    @NotNull

    /* renamed from: ci */
    private final ControlInfo f86ci;
    @NotNull
    private final ComponentName componentName;
    @Nullable
    private final Control control;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ControlWithState)) {
            return false;
        }
        ControlWithState controlWithState = (ControlWithState) obj;
        return Intrinsics.areEqual((Object) this.componentName, (Object) controlWithState.componentName) && Intrinsics.areEqual((Object) this.f86ci, (Object) controlWithState.f86ci) && Intrinsics.areEqual((Object) this.control, (Object) controlWithState.control);
    }

    public int hashCode() {
        int hashCode = ((this.componentName.hashCode() * 31) + this.f86ci.hashCode()) * 31;
        Control control2 = this.control;
        return hashCode + (control2 == null ? 0 : control2.hashCode());
    }

    @NotNull
    public String toString() {
        return "ControlWithState(componentName=" + this.componentName + ", ci=" + this.f86ci + ", control=" + this.control + ')';
    }

    public ControlWithState(@NotNull ComponentName componentName2, @NotNull ControlInfo controlInfo, @Nullable Control control2) {
        Intrinsics.checkNotNullParameter(componentName2, "componentName");
        Intrinsics.checkNotNullParameter(controlInfo, "ci");
        this.componentName = componentName2;
        this.f86ci = controlInfo;
        this.control = control2;
    }

    @NotNull
    public final ComponentName getComponentName() {
        return this.componentName;
    }

    @NotNull
    public final ControlInfo getCi() {
        return this.f86ci;
    }

    @Nullable
    public final Control getControl() {
        return this.control;
    }
}
