package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.content.res.Resources;
import com.android.systemui.R$plurals;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: AppAdapter.kt */
public final class FavoritesRenderer {
    @NotNull
    private final Function1<ComponentName, Integer> favoriteFunction;
    @NotNull
    private final Resources resources;

    public FavoritesRenderer(@NotNull Resources resources2, @NotNull Function1<? super ComponentName, Integer> function1) {
        Intrinsics.checkNotNullParameter(resources2, "resources");
        Intrinsics.checkNotNullParameter(function1, "favoriteFunction");
        this.resources = resources2;
        this.favoriteFunction = function1;
    }

    @Nullable
    public final String renderFavoritesForComponent(@NotNull ComponentName componentName) {
        Intrinsics.checkNotNullParameter(componentName, "component");
        int intValue = this.favoriteFunction.invoke(componentName).intValue();
        if (intValue == 0) {
            return null;
        }
        return this.resources.getQuantityString(R$plurals.controls_number_of_favorites, intValue, new Object[]{Integer.valueOf(intValue)});
    }
}
