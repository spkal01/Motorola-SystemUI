package com.android.systemui.accessibility.floatingmenu;

import android.graphics.Rect;

public final /* synthetic */ class AccessibilityFloatingMenuView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ AccessibilityFloatingMenuView f$0;
    public final /* synthetic */ Rect f$1;

    public /* synthetic */ AccessibilityFloatingMenuView$$ExternalSyntheticLambda6(AccessibilityFloatingMenuView accessibilityFloatingMenuView, Rect rect) {
        this.f$0 = accessibilityFloatingMenuView;
        this.f$1 = rect;
    }

    public final void run() {
        this.f$0.lambda$setSystemGestureExclusion$6(this.f$1);
    }
}
