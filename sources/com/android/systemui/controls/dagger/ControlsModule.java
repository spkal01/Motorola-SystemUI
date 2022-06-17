package com.android.systemui.controls.dagger;

import android.content.pm.PackageManager;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsModule.kt */
public abstract class ControlsModule {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    public static final boolean providesControlsFeatureEnabled(@NotNull PackageManager packageManager) {
        return Companion.providesControlsFeatureEnabled(packageManager);
    }

    /* compiled from: ControlsModule.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final boolean providesControlsFeatureEnabled(@NotNull PackageManager packageManager) {
            Intrinsics.checkNotNullParameter(packageManager, "pm");
            return packageManager.hasSystemFeature("android.software.controls");
        }
    }
}
