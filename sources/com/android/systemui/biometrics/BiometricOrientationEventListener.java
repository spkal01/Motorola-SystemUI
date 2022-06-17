package com.android.systemui.biometrics;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.view.Display;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: BiometricOrientationEventListener.kt */
public final class BiometricOrientationEventListener implements DisplayManager.DisplayListener {
    @NotNull
    private final Context context;
    @NotNull
    private final DisplayManager displayManager;
    @NotNull
    private final Handler handler;
    private int lastRotation;
    @NotNull
    private final Function0<Unit> onOrientationChanged;

    public void onDisplayAdded(int i) {
    }

    public void onDisplayRemoved(int i) {
    }

    public BiometricOrientationEventListener(@NotNull Context context2, @NotNull Function0<Unit> function0, @NotNull DisplayManager displayManager2, @NotNull Handler handler2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(function0, "onOrientationChanged");
        Intrinsics.checkNotNullParameter(displayManager2, "displayManager");
        Intrinsics.checkNotNullParameter(handler2, "handler");
        this.context = context2;
        this.onOrientationChanged = function0;
        this.displayManager = displayManager2;
        this.handler = handler2;
        Display display = context2.getDisplay();
        this.lastRotation = display == null ? 0 : display.getRotation();
    }

    public void onDisplayChanged(int i) {
        int intValue;
        Display display = this.context.getDisplay();
        Integer valueOf = display == null ? null : Integer.valueOf(display.getRotation());
        if (valueOf != null && this.lastRotation != (intValue = valueOf.intValue())) {
            this.lastRotation = intValue;
            this.onOrientationChanged.invoke();
        }
    }

    public final void enable() {
        this.displayManager.registerDisplayListener(this, this.handler);
    }

    public final void disable() {
        this.displayManager.unregisterDisplayListener(this);
    }
}
