package com.android.systemui.navigationbar.gestural;

public final /* synthetic */ class EdgeBackGestureHandler$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ EdgeBackGestureHandler f$0;

    public /* synthetic */ EdgeBackGestureHandler$$ExternalSyntheticLambda2(EdgeBackGestureHandler edgeBackGestureHandler) {
        this.f$0 = edgeBackGestureHandler;
    }

    public final void run() {
        this.f$0.onNavigationSettingsChanged();
    }
}
