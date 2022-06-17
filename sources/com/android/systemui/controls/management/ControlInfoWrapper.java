package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import com.android.systemui.controls.ControlInterface;
import com.android.systemui.controls.controller.ControlInfo;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsModel.kt */
public final class ControlInfoWrapper extends ElementWrapper implements ControlInterface {
    @NotNull
    private final ComponentName component;
    @NotNull
    private final ControlInfo controlInfo;
    @NotNull
    private Function2<? super ComponentName, ? super String, Icon> customIconGetter;
    private boolean favorite;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ControlInfoWrapper)) {
            return false;
        }
        ControlInfoWrapper controlInfoWrapper = (ControlInfoWrapper) obj;
        return Intrinsics.areEqual((Object) getComponent(), (Object) controlInfoWrapper.getComponent()) && Intrinsics.areEqual((Object) this.controlInfo, (Object) controlInfoWrapper.controlInfo) && getFavorite() == controlInfoWrapper.getFavorite();
    }

    public int hashCode() {
        int hashCode = ((getComponent().hashCode() * 31) + this.controlInfo.hashCode()) * 31;
        boolean favorite2 = getFavorite();
        if (favorite2) {
            favorite2 = true;
        }
        return hashCode + (favorite2 ? 1 : 0);
    }

    @NotNull
    public String toString() {
        return "ControlInfoWrapper(component=" + getComponent() + ", controlInfo=" + this.controlInfo + ", favorite=" + getFavorite() + ')';
    }

    public boolean getRemoved() {
        return ControlInterface.DefaultImpls.getRemoved(this);
    }

    @NotNull
    public ComponentName getComponent() {
        return this.component;
    }

    @NotNull
    public final ControlInfo getControlInfo() {
        return this.controlInfo;
    }

    public boolean getFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean z) {
        this.favorite = z;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ControlInfoWrapper(@NotNull ComponentName componentName, @NotNull ControlInfo controlInfo2, boolean z) {
        super((DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(controlInfo2, "controlInfo");
        this.component = componentName;
        this.controlInfo = controlInfo2;
        this.favorite = z;
        this.customIconGetter = ControlInfoWrapper$customIconGetter$1.INSTANCE;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public ControlInfoWrapper(@NotNull ComponentName componentName, @NotNull ControlInfo controlInfo2, boolean z, @NotNull Function2<? super ComponentName, ? super String, Icon> function2) {
        this(componentName, controlInfo2, z);
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(controlInfo2, "controlInfo");
        Intrinsics.checkNotNullParameter(function2, "customIconGetter");
        this.customIconGetter = function2;
    }

    @NotNull
    public String getControlId() {
        return this.controlInfo.getControlId();
    }

    @NotNull
    public CharSequence getTitle() {
        return this.controlInfo.getControlTitle();
    }

    @NotNull
    public CharSequence getSubtitle() {
        return this.controlInfo.getControlSubtitle();
    }

    public int getDeviceType() {
        return this.controlInfo.getDeviceType();
    }

    @Nullable
    public Icon getCustomIcon() {
        return this.customIconGetter.invoke(getComponent(), getControlId());
    }
}
