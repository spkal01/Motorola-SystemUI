package com.android.systemui.statusbar;

import android.os.Trace;
import android.util.Log;
import android.util.MathUtils;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationShadeDepthController.kt */
final class NotificationShadeDepthController$updateBlurCallback$1 implements Choreographer.FrameCallback {
    final /* synthetic */ NotificationShadeDepthController this$0;

    NotificationShadeDepthController$updateBlurCallback$1(NotificationShadeDepthController notificationShadeDepthController) {
        this.this$0 = notificationShadeDepthController;
    }

    public final void doFrame(long j) {
        boolean z = false;
        this.this$0.updateScheduled = false;
        float max = (float) Math.max(Math.max(Math.max((int) ((((float) this.this$0.getShadeSpring().getRadius()) * 0.4f) + (((float) MathUtils.constrain(this.this$0.getShadeAnimation().getRadius(), this.this$0.blurUtils.getMinBlurRadius(), this.this$0.blurUtils.getMaxBlurRadius())) * 0.6f)), this.this$0.blurUtils.blurRadiusOfRatio(this.this$0.getQsPanelExpansion() * this.this$0.shadeExpansion)), this.this$0.blurUtils.blurRadiusOfRatio(this.this$0.getTransitionToFullShadeProgress())), this.this$0.wakeAndUnlockBlurRadius);
        if (this.this$0.getBlursDisabledForAppLaunch()) {
            max = 0.0f;
        }
        int i = (int) max;
        if (this.this$0.scrimsVisible) {
            i = 0;
        }
        float ratioOfBlurRadius = this.this$0.blurUtils.ratioOfBlurRadius(i);
        if (!this.this$0.blurUtils.supportsBlursOnWindows()) {
            i = 0;
        }
        int ratio = (int) (((float) i) * (1.0f - this.this$0.getBrightnessMirrorSpring().getRatio()));
        if (this.this$0.scrimsVisible && !this.this$0.getBlursDisabledForAppLaunch()) {
            z = true;
        }
        Trace.traceCounter(4096, "shade_blur_radius", ratio);
        BlurUtils access$getBlurUtils$p = this.this$0.blurUtils;
        View access$getBlurRoot$p = this.this$0.blurRoot;
        ViewRootImpl viewRootImpl = access$getBlurRoot$p == null ? null : access$getBlurRoot$p.getViewRootImpl();
        if (viewRootImpl == null) {
            viewRootImpl = this.this$0.getRoot().getViewRootImpl();
        }
        access$getBlurUtils$p.applyBlur(viewRootImpl, ratio, z);
        this.this$0.lastAppliedBlur = ratio;
        try {
            if (!this.this$0.getRoot().isAttachedToWindow() || this.this$0.getRoot().getWindowToken() == null) {
                Log.i("DepthController", Intrinsics.stringPlus("Won't set zoom. Window not attached ", this.this$0.getRoot()));
            } else {
                this.this$0.wallpaperManager.setWallpaperZoomOut(this.this$0.getRoot().getWindowToken(), ratioOfBlurRadius);
            }
        } catch (IllegalArgumentException e) {
            Log.w("DepthController", Intrinsics.stringPlus("Can't set zoom. Window is gone: ", this.this$0.getRoot().getWindowToken()), e);
        }
        for (NotificationShadeDepthController.DepthListener depthListener : this.this$0.listeners) {
            depthListener.onWallpaperZoomOutChanged(ratioOfBlurRadius);
            depthListener.onBlurRadiusChanged(ratio);
        }
        this.this$0.notificationShadeWindowController.setBackgroundBlurRadius(ratio);
    }
}
