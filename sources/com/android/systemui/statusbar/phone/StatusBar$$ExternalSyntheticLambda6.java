package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.BackDropView;
import com.android.systemui.statusbar.NotificationShadeDepthController;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda6 implements NotificationShadeDepthController.DepthListener {
    public final /* synthetic */ float f$0;
    public final /* synthetic */ BackDropView f$1;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda6(float f, BackDropView backDropView) {
        this.f$0 = f;
        this.f$1 = backDropView;
    }

    public final void onWallpaperZoomOutChanged(float f) {
        StatusBar.lambda$makeStatusBarView$6(this.f$0, this.f$1, f);
    }
}
