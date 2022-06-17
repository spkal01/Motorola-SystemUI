package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.SelectionItem */
/* compiled from: ControlsUiControllerImpl.kt */
final class SelectionItem {
    @NotNull
    private final CharSequence appName;
    @NotNull
    private final ComponentName componentName;
    @NotNull
    private final Drawable icon;
    @NotNull
    private final CharSequence structure;
    private final int uid;

    public static /* synthetic */ SelectionItem copy$default(SelectionItem selectionItem, CharSequence charSequence, CharSequence charSequence2, Drawable drawable, ComponentName componentName2, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            charSequence = selectionItem.appName;
        }
        if ((i2 & 2) != 0) {
            charSequence2 = selectionItem.structure;
        }
        CharSequence charSequence3 = charSequence2;
        if ((i2 & 4) != 0) {
            drawable = selectionItem.icon;
        }
        Drawable drawable2 = drawable;
        if ((i2 & 8) != 0) {
            componentName2 = selectionItem.componentName;
        }
        ComponentName componentName3 = componentName2;
        if ((i2 & 16) != 0) {
            i = selectionItem.uid;
        }
        return selectionItem.copy(charSequence, charSequence3, drawable2, componentName3, i);
    }

    @NotNull
    public final SelectionItem copy(@NotNull CharSequence charSequence, @NotNull CharSequence charSequence2, @NotNull Drawable drawable, @NotNull ComponentName componentName2, int i) {
        Intrinsics.checkNotNullParameter(charSequence, "appName");
        Intrinsics.checkNotNullParameter(charSequence2, "structure");
        Intrinsics.checkNotNullParameter(drawable, "icon");
        Intrinsics.checkNotNullParameter(componentName2, "componentName");
        return new SelectionItem(charSequence, charSequence2, drawable, componentName2, i);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SelectionItem)) {
            return false;
        }
        SelectionItem selectionItem = (SelectionItem) obj;
        return Intrinsics.areEqual((Object) this.appName, (Object) selectionItem.appName) && Intrinsics.areEqual((Object) this.structure, (Object) selectionItem.structure) && Intrinsics.areEqual((Object) this.icon, (Object) selectionItem.icon) && Intrinsics.areEqual((Object) this.componentName, (Object) selectionItem.componentName) && this.uid == selectionItem.uid;
    }

    public int hashCode() {
        return (((((((this.appName.hashCode() * 31) + this.structure.hashCode()) * 31) + this.icon.hashCode()) * 31) + this.componentName.hashCode()) * 31) + Integer.hashCode(this.uid);
    }

    @NotNull
    public String toString() {
        return "SelectionItem(appName=" + this.appName + ", structure=" + this.structure + ", icon=" + this.icon + ", componentName=" + this.componentName + ", uid=" + this.uid + ')';
    }

    public SelectionItem(@NotNull CharSequence charSequence, @NotNull CharSequence charSequence2, @NotNull Drawable drawable, @NotNull ComponentName componentName2, int i) {
        Intrinsics.checkNotNullParameter(charSequence, "appName");
        Intrinsics.checkNotNullParameter(charSequence2, "structure");
        Intrinsics.checkNotNullParameter(drawable, "icon");
        Intrinsics.checkNotNullParameter(componentName2, "componentName");
        this.appName = charSequence;
        this.structure = charSequence2;
        this.icon = drawable;
        this.componentName = componentName2;
        this.uid = i;
    }

    @NotNull
    public final CharSequence getStructure() {
        return this.structure;
    }

    @NotNull
    public final Drawable getIcon() {
        return this.icon;
    }

    @NotNull
    public final ComponentName getComponentName() {
        return this.componentName;
    }

    public final int getUid() {
        return this.uid;
    }

    @NotNull
    public final CharSequence getTitle() {
        return this.structure.length() == 0 ? this.appName : this.structure;
    }
}
