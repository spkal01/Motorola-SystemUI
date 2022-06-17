package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.DisplayCutout;
import android.view.WindowManager;
import android.view.WindowMetrics;
import com.android.systemui.Dumpable;
import com.android.systemui.R$dimen;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.leak.RotationUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: StatusBarContentInsetsProvider.kt */
public final class StatusBarContentInsetsProvider implements CallbackController<StatusBarContentInsetsChangedListener>, ConfigurationController.ConfigurationListener, Dumpable {
    @NotNull
    private final ConfigurationController configurationController;
    @NotNull
    private final Context context;
    @NotNull
    private final DumpManager dumpManager;
    @NotNull
    private final Rect[] insetsByCorner = new Rect[4];
    @NotNull
    private final Set<StatusBarContentInsetsChangedListener> listeners = new LinkedHashSet();
    @NotNull
    private final WindowManager windowManager;

    public StatusBarContentInsetsProvider(@NotNull Context context2, @NotNull ConfigurationController configurationController2, @NotNull WindowManager windowManager2, @NotNull DumpManager dumpManager2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(configurationController2, "configurationController");
        Intrinsics.checkNotNullParameter(windowManager2, "windowManager");
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        this.context = context2;
        this.configurationController = configurationController2;
        this.windowManager = windowManager2;
        this.dumpManager = dumpManager2;
        configurationController2.addCallback(this);
        dumpManager2.registerDumpable("StatusBarInsetsProvider", this);
    }

    public void addCallback(@NotNull StatusBarContentInsetsChangedListener statusBarContentInsetsChangedListener) {
        Intrinsics.checkNotNullParameter(statusBarContentInsetsChangedListener, "listener");
        this.listeners.add(statusBarContentInsetsChangedListener);
    }

    public void removeCallback(@NotNull StatusBarContentInsetsChangedListener statusBarContentInsetsChangedListener) {
        Intrinsics.checkNotNullParameter(statusBarContentInsetsChangedListener, "listener");
        this.listeners.remove(statusBarContentInsetsChangedListener);
    }

    public void onDensityOrFontScaleChanged() {
        clearCachedInsets();
    }

    public void onOverlayChanged() {
        clearCachedInsets();
    }

    private final void clearCachedInsets() {
        Rect[] rectArr = this.insetsByCorner;
        rectArr[0] = null;
        rectArr[1] = null;
        rectArr[2] = null;
        rectArr[3] = null;
        notifyInsetsChanged();
    }

    private final void notifyInsetsChanged() {
        for (StatusBarContentInsetsChangedListener onStatusBarContentInsetsChanged : this.listeners) {
            onStatusBarContentInsetsChanged.onStatusBarContentInsetsChanged();
        }
    }

    @NotNull
    public final Rect getBoundingRectForPrivacyChipForRotation(int i) {
        Rect rect = this.insetsByCorner[i];
        Resources resourcesForRotation = RotationUtils.getResourcesForRotation(i, this.context);
        if (rect == null) {
            Intrinsics.checkNotNullExpressionValue(resourcesForRotation, "rotatedResources");
            rect = getAndSetInsetsForRotation(i, resourcesForRotation);
        }
        int dimensionPixelSize = resourcesForRotation.getDimensionPixelSize(R$dimen.ongoing_appops_dot_diameter);
        int dimensionPixelSize2 = resourcesForRotation.getDimensionPixelSize(R$dimen.ongoing_appops_chip_max_width);
        boolean z = true;
        if (this.context.getResources().getConfiguration().getLayoutDirection() != 1) {
            z = false;
        }
        return StatusBarContentInsetsProviderKt.getPrivacyChipBoundingRectForInsets(rect, dimensionPixelSize, dimensionPixelSize2, z);
    }

    @NotNull
    public final Rect getStatusBarContentInsetsForRotation(int i) {
        Rect rect = this.insetsByCorner[i];
        if (rect != null) {
            return rect;
        }
        Resources resourcesForRotation = RotationUtils.getResourcesForRotation(i, this.context);
        Intrinsics.checkNotNullExpressionValue(resourcesForRotation, "rotatedResources");
        return getAndSetInsetsForRotation(i, resourcesForRotation);
    }

    private final Rect getAndSetInsetsForRotation(int i, Resources resources) {
        Rect calculatedInsetsForRotation = getCalculatedInsetsForRotation(i, resources);
        this.insetsByCorner[i] = calculatedInsetsForRotation;
        return calculatedInsetsForRotation;
    }

    private final Rect getCalculatedInsetsForRotation(int i, Resources resources) {
        int i2;
        int i3;
        DisplayCutout cutout = this.context.getDisplay().getCutout();
        int exactRotation = RotationUtils.getExactRotation(this.context);
        boolean z = true;
        if (resources.getConfiguration().getLayoutDirection() != 1) {
            z = false;
        }
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.rounded_corner_content_padding);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.ongoing_appops_dot_min_padding);
        if (z) {
            i2 = dimensionPixelSize;
            i3 = Math.max(dimensionPixelSize2, dimensionPixelSize);
        } else {
            i3 = dimensionPixelSize;
            i2 = Math.max(dimensionPixelSize2, dimensionPixelSize);
        }
        WindowMetrics maximumWindowMetrics = this.windowManager.getMaximumWindowMetrics();
        Intrinsics.checkNotNullExpressionValue(maximumWindowMetrics, "windowManager.maximumWindowMetrics");
        return StatusBarContentInsetsProviderKt.calculateInsetsForRotationWithRotatedResources(exactRotation, i, cutout, maximumWindowMetrics, resources.getDimensionPixelSize(R$dimen.status_bar_height), i3, i2);
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        Rect[] rectArr = this.insetsByCorner;
        int length = rectArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            printWriter.println(RotationUtils.toString(i2) + " -> " + rectArr[i]);
            i++;
            i2++;
        }
    }
}
