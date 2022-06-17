package com.android.systemui.controls;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CustomIconCache.kt */
public final class CustomIconCache {
    @NotNull
    private final Map<String, Icon> cache = new LinkedHashMap();
    @Nullable
    private ComponentName currentComponent;

    public final void store(@NotNull ComponentName componentName, @NotNull String str, @Nullable Icon icon) {
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(str, "controlId");
        if (!Intrinsics.areEqual((Object) componentName, (Object) this.currentComponent)) {
            clear();
            this.currentComponent = componentName;
        }
        synchronized (this.cache) {
            if (icon != null) {
                Icon put = this.cache.put(str, icon);
            } else {
                Icon remove = this.cache.remove(str);
            }
        }
    }

    @Nullable
    public final Icon retrieve(@NotNull ComponentName componentName, @NotNull String str) {
        Icon icon;
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(str, "controlId");
        if (!Intrinsics.areEqual((Object) componentName, (Object) this.currentComponent)) {
            return null;
        }
        synchronized (this.cache) {
            icon = this.cache.get(str);
        }
        return icon;
    }

    private final void clear() {
        synchronized (this.cache) {
            this.cache.clear();
            Unit unit = Unit.INSTANCE;
        }
    }
}
