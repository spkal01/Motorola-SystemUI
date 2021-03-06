package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import com.android.systemui.controls.ControlInterface;
import com.android.systemui.controls.ControlStatus;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsModel.kt */
public final class ControlStatusWrapper extends ElementWrapper implements ControlInterface {
    @NotNull
    private final ControlStatus controlStatus;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof ControlStatusWrapper) && Intrinsics.areEqual((Object) this.controlStatus, (Object) ((ControlStatusWrapper) obj).controlStatus);
    }

    @NotNull
    public ComponentName getComponent() {
        return this.controlStatus.getComponent();
    }

    @NotNull
    public String getControlId() {
        return this.controlStatus.getControlId();
    }

    @Nullable
    public Icon getCustomIcon() {
        return this.controlStatus.getCustomIcon();
    }

    public int getDeviceType() {
        return this.controlStatus.getDeviceType();
    }

    public boolean getFavorite() {
        return this.controlStatus.getFavorite();
    }

    public boolean getRemoved() {
        return this.controlStatus.getRemoved();
    }

    @NotNull
    public CharSequence getSubtitle() {
        return this.controlStatus.getSubtitle();
    }

    @NotNull
    public CharSequence getTitle() {
        return this.controlStatus.getTitle();
    }

    public int hashCode() {
        return this.controlStatus.hashCode();
    }

    @NotNull
    public String toString() {
        return "ControlStatusWrapper(controlStatus=" + this.controlStatus + ')';
    }

    @NotNull
    public final ControlStatus getControlStatus() {
        return this.controlStatus;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ControlStatusWrapper(@NotNull ControlStatus controlStatus2) {
        super((DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(controlStatus2, "controlStatus");
        this.controlStatus = controlStatus2;
    }
}
