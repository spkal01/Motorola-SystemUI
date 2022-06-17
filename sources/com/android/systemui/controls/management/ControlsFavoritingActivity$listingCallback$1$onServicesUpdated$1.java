package com.android.systemui.controls.management;

import android.view.View;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$listingCallback$1$onServicesUpdated$1 implements Runnable {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$listingCallback$1$onServicesUpdated$1(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public final void run() {
        View access$getOtherAppsButton$p = this.this$0.otherAppsButton;
        if (access$getOtherAppsButton$p != null) {
            access$getOtherAppsButton$p.setVisibility(0);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("otherAppsButton");
            throw null;
        }
    }
}
