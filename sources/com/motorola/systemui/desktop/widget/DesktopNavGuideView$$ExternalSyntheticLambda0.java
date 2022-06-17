package com.motorola.systemui.desktop.widget;

import android.view.View;
import com.android.systemui.Dependency;
import com.motorola.taskbar.MotoTaskBarController;

public final /* synthetic */ class DesktopNavGuideView$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public static final /* synthetic */ DesktopNavGuideView$$ExternalSyntheticLambda0 INSTANCE = new DesktopNavGuideView$$ExternalSyntheticLambda0();

    private /* synthetic */ DesktopNavGuideView$$ExternalSyntheticLambda0() {
    }

    public final void onClick(View view) {
        ((MotoTaskBarController) Dependency.get(MotoTaskBarController.class)).requestNavTrackpadGuide(false);
    }
}
