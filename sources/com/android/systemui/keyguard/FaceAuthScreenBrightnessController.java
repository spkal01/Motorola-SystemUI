package com.android.systemui.keyguard;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dumpable;
import com.android.systemui.R$drawable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.util.settings.GlobalSettings;
import com.android.systemui.util.settings.SystemSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: FaceAuthScreenBrightnessController.kt */
public class FaceAuthScreenBrightnessController implements Dumpable {
    private final long brightnessAnimationDuration;
    /* access modifiers changed from: private */
    @Nullable
    public ValueAnimator brightnessAnimator;
    @NotNull
    private final DumpManager dumpManager;
    /* access modifiers changed from: private */
    public final boolean enabled;
    @NotNull
    private final GlobalSettings globalSettings;
    @NotNull
    private final FaceAuthScreenBrightnessController$keyguardUpdateCallback$1 keyguardUpdateCallback;
    @NotNull
    private final KeyguardUpdateMonitor keyguardUpdateMonitor;
    @NotNull
    private final Handler mainHandler;
    private final float maxScreenBrightness;
    /* access modifiers changed from: private */
    public final float maxScrimOpacity;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationShadeWindowController notificationShadeWindowController;
    private boolean overridingBrightness;
    @NotNull
    private final Resources resources;
    /* access modifiers changed from: private */
    @NotNull
    public final SystemSettings systemSettings;
    private boolean useFaceAuthWallpaper;
    /* access modifiers changed from: private */
    public float userDefinedBrightness = 1.0f;
    /* access modifiers changed from: private */
    public View whiteOverlay;

    @VisibleForTesting
    public static /* synthetic */ void getUseFaceAuthWallpaper$annotations() {
    }

    public FaceAuthScreenBrightnessController(@NotNull NotificationShadeWindowController notificationShadeWindowController2, @NotNull KeyguardUpdateMonitor keyguardUpdateMonitor2, @NotNull Resources resources2, @NotNull GlobalSettings globalSettings2, @NotNull SystemSettings systemSettings2, @NotNull Handler handler, @NotNull DumpManager dumpManager2, boolean z) {
        Intrinsics.checkNotNullParameter(notificationShadeWindowController2, "notificationShadeWindowController");
        Intrinsics.checkNotNullParameter(keyguardUpdateMonitor2, "keyguardUpdateMonitor");
        Intrinsics.checkNotNullParameter(resources2, "resources");
        Intrinsics.checkNotNullParameter(globalSettings2, "globalSettings");
        Intrinsics.checkNotNullParameter(systemSettings2, "systemSettings");
        Intrinsics.checkNotNullParameter(handler, "mainHandler");
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        this.notificationShadeWindowController = notificationShadeWindowController2;
        this.keyguardUpdateMonitor = keyguardUpdateMonitor2;
        this.resources = resources2;
        this.globalSettings = globalSettings2;
        this.systemSettings = systemSettings2;
        this.mainHandler = handler;
        this.dumpManager = dumpManager2;
        this.enabled = z;
        this.useFaceAuthWallpaper = globalSettings2.getInt("sysui.use_face_auth_wallpaper", FaceAuthScreenBrightnessControllerKt.getDEFAULT_USE_FACE_WALLPAPER() ? 1 : 0) != 1 ? false : true;
        this.brightnessAnimationDuration = globalSettings2.getLong("sysui.face_brightness_anim_duration", FaceAuthScreenBrightnessControllerKt.getDEFAULT_ANIMATION_DURATION());
        this.maxScreenBrightness = ((float) globalSettings2.getInt("sysui.face_max_brightness", FaceAuthScreenBrightnessControllerKt.getMAX_SCREEN_BRIGHTNESS())) / 100.0f;
        this.maxScrimOpacity = ((float) globalSettings2.getInt("sysui.face_max_scrim_opacity", FaceAuthScreenBrightnessControllerKt.getMAX_SCRIM_OPACTY())) / 100.0f;
        this.keyguardUpdateCallback = new FaceAuthScreenBrightnessController$keyguardUpdateCallback$1(this);
    }

    public final boolean getUseFaceAuthWallpaper() {
        return this.useFaceAuthWallpaper;
    }

    /* access modifiers changed from: private */
    public final void setOverridingBrightness(boolean z) {
        if (this.overridingBrightness != z) {
            this.overridingBrightness = z;
            ValueAnimator valueAnimator = this.brightnessAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!z) {
                this.notificationShadeWindowController.setFaceAuthDisplayBrightness(-1.0f);
                View view = this.whiteOverlay;
                if (view == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
                    throw null;
                } else if (view.getAlpha() > 0.0f) {
                    View view2 = this.whiteOverlay;
                    if (view2 != null) {
                        ValueAnimator createAnimator = createAnimator(view2.getAlpha(), 0.0f);
                        createAnimator.setDuration(200);
                        createAnimator.addUpdateListener(new FaceAuthScreenBrightnessController$overridingBrightness$1$1(this));
                        createAnimator.addListener(new FaceAuthScreenBrightnessController$overridingBrightness$1$2(this));
                        createAnimator.start();
                        Unit unit = Unit.INSTANCE;
                        this.brightnessAnimator = createAnimator;
                        return;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
                    throw null;
                }
            } else {
                float max = Float.max(this.maxScreenBrightness, this.userDefinedBrightness);
                View view3 = this.whiteOverlay;
                if (view3 != null) {
                    view3.setVisibility(0);
                    ValueAnimator createAnimator2 = createAnimator(0.0f, 1.0f);
                    createAnimator2.setDuration(this.brightnessAnimationDuration);
                    createAnimator2.addUpdateListener(new FaceAuthScreenBrightnessController$overridingBrightness$2$1(this, max));
                    createAnimator2.addListener(new FaceAuthScreenBrightnessController$overridingBrightness$2$2(this));
                    createAnimator2.start();
                    Unit unit2 = Unit.INSTANCE;
                    this.brightnessAnimator = createAnimator2;
                    return;
                }
                Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
                throw null;
            }
        }
    }

    @VisibleForTesting
    public ValueAnimator createAnimator(float f, float f2) {
        return ValueAnimator.ofFloat(new float[]{f, f2});
    }

    @Nullable
    public final Bitmap getFaceAuthWallpaper() {
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (!this.useFaceAuthWallpaper || !this.keyguardUpdateMonitor.isFaceAuthEnabledForUser(currentUser)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(this.resources, R$drawable.face_auth_wallpaper, options);
    }

    public final void attach(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "overlayView");
        this.whiteOverlay = view;
        if (view != null) {
            view.setFocusable(8);
            View view2 = this.whiteOverlay;
            if (view2 != null) {
                view2.setBackground(new ColorDrawable(-1));
                View view3 = this.whiteOverlay;
                if (view3 != null) {
                    view3.setEnabled(false);
                    View view4 = this.whiteOverlay;
                    if (view4 != null) {
                        view4.setAlpha(0.0f);
                        View view5 = this.whiteOverlay;
                        if (view5 != null) {
                            view5.setVisibility(4);
                            DumpManager dumpManager2 = this.dumpManager;
                            String name = FaceAuthScreenBrightnessController.class.getName();
                            Intrinsics.checkNotNullExpressionValue(name, "this.javaClass.name");
                            dumpManager2.registerDumpable(name, this);
                            this.keyguardUpdateMonitor.registerCallback(this.keyguardUpdateCallback);
                            this.systemSettings.registerContentObserver("screen_brightness_float", (ContentObserver) new FaceAuthScreenBrightnessController$attach$1(this, this.mainHandler));
                            this.userDefinedBrightness = this.systemSettings.getFloat("screen_brightness_float", 1.0f);
                            return;
                        }
                        Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
                        throw null;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
                    throw null;
                }
                Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("whiteOverlay");
        throw null;
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println(Intrinsics.stringPlus("overridingBrightness: ", Boolean.valueOf(this.overridingBrightness)));
        printWriter.println(Intrinsics.stringPlus("useFaceAuthWallpaper: ", Boolean.valueOf(getUseFaceAuthWallpaper())));
        printWriter.println(Intrinsics.stringPlus("brightnessAnimator: ", this.brightnessAnimator));
        printWriter.println(Intrinsics.stringPlus("brightnessAnimationDuration: ", Long.valueOf(this.brightnessAnimationDuration)));
        printWriter.println(Intrinsics.stringPlus("maxScreenBrightness: ", Float.valueOf(this.maxScreenBrightness)));
        printWriter.println(Intrinsics.stringPlus("userDefinedBrightness: ", Float.valueOf(this.userDefinedBrightness)));
        printWriter.println(Intrinsics.stringPlus("enabled: ", Boolean.valueOf(this.enabled)));
    }
}
