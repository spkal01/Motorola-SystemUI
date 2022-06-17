package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.graphics.drawable.Icon;
import com.android.systemui.controls.CustomIconCache;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: FavoritesModel.kt */
/* synthetic */ class FavoritesModel$elements$1$1 extends FunctionReferenceImpl implements Function2<ComponentName, String, Icon> {
    FavoritesModel$elements$1$1(CustomIconCache customIconCache) {
        super(2, customIconCache, CustomIconCache.class, "retrieve", "retrieve(Landroid/content/ComponentName;Ljava/lang/String;)Landroid/graphics/drawable/Icon;", 0);
    }

    @Nullable
    public final Icon invoke(@NotNull ComponentName componentName, @NotNull String str) {
        Intrinsics.checkNotNullParameter(componentName, "p0");
        Intrinsics.checkNotNullParameter(str, "p1");
        return ((CustomIconCache) this.receiver).retrieve(componentName, str);
    }
}
