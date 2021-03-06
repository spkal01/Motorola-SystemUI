package com.android.systemui.controls.controller;

import android.content.ComponentName;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: StructureInfo.kt */
public final class StructureInfo {
    @NotNull
    private final ComponentName componentName;
    @NotNull
    private final List<ControlInfo> controls;
    @NotNull
    private final CharSequence structure;

    public static /* synthetic */ StructureInfo copy$default(StructureInfo structureInfo, ComponentName componentName2, CharSequence charSequence, List<ControlInfo> list, int i, Object obj) {
        if ((i & 1) != 0) {
            componentName2 = structureInfo.componentName;
        }
        if ((i & 2) != 0) {
            charSequence = structureInfo.structure;
        }
        if ((i & 4) != 0) {
            list = structureInfo.controls;
        }
        return structureInfo.copy(componentName2, charSequence, list);
    }

    @NotNull
    public final StructureInfo copy(@NotNull ComponentName componentName2, @NotNull CharSequence charSequence, @NotNull List<ControlInfo> list) {
        Intrinsics.checkNotNullParameter(componentName2, "componentName");
        Intrinsics.checkNotNullParameter(charSequence, "structure");
        Intrinsics.checkNotNullParameter(list, "controls");
        return new StructureInfo(componentName2, charSequence, list);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StructureInfo)) {
            return false;
        }
        StructureInfo structureInfo = (StructureInfo) obj;
        return Intrinsics.areEqual((Object) this.componentName, (Object) structureInfo.componentName) && Intrinsics.areEqual((Object) this.structure, (Object) structureInfo.structure) && Intrinsics.areEqual((Object) this.controls, (Object) structureInfo.controls);
    }

    public int hashCode() {
        return (((this.componentName.hashCode() * 31) + this.structure.hashCode()) * 31) + this.controls.hashCode();
    }

    @NotNull
    public String toString() {
        return "StructureInfo(componentName=" + this.componentName + ", structure=" + this.structure + ", controls=" + this.controls + ')';
    }

    public StructureInfo(@NotNull ComponentName componentName2, @NotNull CharSequence charSequence, @NotNull List<ControlInfo> list) {
        Intrinsics.checkNotNullParameter(componentName2, "componentName");
        Intrinsics.checkNotNullParameter(charSequence, "structure");
        Intrinsics.checkNotNullParameter(list, "controls");
        this.componentName = componentName2;
        this.structure = charSequence;
        this.controls = list;
    }

    @NotNull
    public final ComponentName getComponentName() {
        return this.componentName;
    }

    @NotNull
    public final CharSequence getStructure() {
        return this.structure;
    }

    @NotNull
    public final List<ControlInfo> getControls() {
        return this.controls;
    }
}
