package com.android.p011wm.shell.splitscreen;

import android.view.SurfaceControl;
import com.android.p011wm.shell.common.split.SplitWindowManager;

/* renamed from: com.android.wm.shell.splitscreen.StageCoordinator$$ExternalSyntheticLambda3 */
public final /* synthetic */ class StageCoordinator$$ExternalSyntheticLambda3 implements SplitWindowManager.ParentContainerCallbacks {
    public final /* synthetic */ StageCoordinator f$0;

    public /* synthetic */ StageCoordinator$$ExternalSyntheticLambda3(StageCoordinator stageCoordinator) {
        this.f$0 = stageCoordinator;
    }

    public final void attachToParentSurface(SurfaceControl.Builder builder) {
        this.f$0.lambda$onDisplayAreaAppeared$4(builder);
    }
}
