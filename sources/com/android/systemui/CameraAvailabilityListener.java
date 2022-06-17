package com.android.systemui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraManager;
import android.util.PathParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: CameraAvailabilityListener.kt */
public final class CameraAvailabilityListener {
    @NotNull
    public static final Factory Factory = new Factory((DefaultConstructorMarker) null);
    @NotNull
    private final CameraManager.AvailabilityCallback availabilityCallback = new CameraAvailabilityListener$availabilityCallback$1(this);
    @NotNull
    private final CameraManager cameraManager;
    @NotNull
    private Rect cutoutBounds = new Rect();
    @NotNull
    private final Path cutoutProtectionPath;
    @NotNull
    private final Set<String> excludedPackageIds;
    @NotNull
    private final Executor executor;
    @NotNull
    private final List<CameraTransitionCallback> listeners = new ArrayList();
    /* access modifiers changed from: private */
    @NotNull
    public final String targetCameraId;

    /* compiled from: CameraAvailabilityListener.kt */
    public interface CameraTransitionCallback {
        void onApplyCameraProtection(@NotNull Path path, @NotNull Rect rect);

        void onHideCameraProtection();
    }

    public CameraAvailabilityListener(@NotNull CameraManager cameraManager2, @NotNull Path path, @NotNull String str, @NotNull String str2, @NotNull Executor executor2) {
        Intrinsics.checkNotNullParameter(cameraManager2, "cameraManager");
        Intrinsics.checkNotNullParameter(path, "cutoutProtectionPath");
        Intrinsics.checkNotNullParameter(str, "targetCameraId");
        Intrinsics.checkNotNullParameter(str2, "excludedPackages");
        Intrinsics.checkNotNullParameter(executor2, "executor");
        this.cameraManager = cameraManager2;
        this.cutoutProtectionPath = path;
        this.targetCameraId = str;
        this.executor = executor2;
        RectF rectF = new RectF();
        path.computeBounds(rectF, false);
        this.cutoutBounds.set(MathKt__MathJVMKt.roundToInt(rectF.left), MathKt__MathJVMKt.roundToInt(rectF.top), MathKt__MathJVMKt.roundToInt(rectF.right), MathKt__MathJVMKt.roundToInt(rectF.bottom));
        this.excludedPackageIds = CollectionsKt___CollectionsKt.toSet(StringsKt__StringsKt.split$default(str2, new String[]{","}, false, 0, 6, (Object) null));
    }

    public final void startListening() {
        registerCameraListener();
    }

    public final void addTransitionCallback(@NotNull CameraTransitionCallback cameraTransitionCallback) {
        Intrinsics.checkNotNullParameter(cameraTransitionCallback, "callback");
        this.listeners.add(cameraTransitionCallback);
    }

    /* access modifiers changed from: private */
    public final boolean isExcluded(String str) {
        return this.excludedPackageIds.contains(str);
    }

    private final void registerCameraListener() {
        this.cameraManager.registerAvailabilityCallback(this.executor, this.availabilityCallback);
    }

    /* access modifiers changed from: private */
    public final void notifyCameraActive() {
        for (CameraTransitionCallback onApplyCameraProtection : this.listeners) {
            onApplyCameraProtection.onApplyCameraProtection(this.cutoutProtectionPath, this.cutoutBounds);
        }
    }

    /* access modifiers changed from: private */
    public final void notifyCameraInactive() {
        for (CameraTransitionCallback onHideCameraProtection : this.listeners) {
            onHideCameraProtection.onHideCameraProtection();
        }
    }

    /* compiled from: CameraAvailabilityListener.kt */
    public static final class Factory {
        public /* synthetic */ Factory(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Factory() {
        }

        @NotNull
        public final CameraAvailabilityListener build(@NotNull Context context, @NotNull Executor executor) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(executor, "executor");
            Object systemService = context.getSystemService("camera");
            Objects.requireNonNull(systemService, "null cannot be cast to non-null type android.hardware.camera2.CameraManager");
            Resources resources = context.getResources();
            String string = resources.getString(R$string.config_frontBuiltInDisplayCutoutProtection);
            String string2 = resources.getString(R$string.config_protectedCameraId);
            String string3 = resources.getString(R$string.config_cameraProtectionExcludedPackages);
            Intrinsics.checkNotNullExpressionValue(string, "pathString");
            Path pathFromString = pathFromString(string);
            Intrinsics.checkNotNullExpressionValue(string2, "cameraId");
            Intrinsics.checkNotNullExpressionValue(string3, "excluded");
            return new CameraAvailabilityListener((CameraManager) systemService, pathFromString, string2, string3, executor);
        }

        private final Path pathFromString(String str) {
            Objects.requireNonNull(str, "null cannot be cast to non-null type kotlin.CharSequence");
            try {
                Path createPathFromPathData = PathParser.createPathFromPathData(StringsKt__StringsKt.trim(str).toString());
                Intrinsics.checkNotNullExpressionValue(createPathFromPathData, "createPathFromPathData(spec)");
                return createPathFromPathData;
            } catch (Throwable th) {
                throw new IllegalArgumentException("Invalid protection path", th);
            }
        }
    }
}
