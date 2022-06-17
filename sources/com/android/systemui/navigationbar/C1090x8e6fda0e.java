package com.android.systemui.navigationbar;

import android.app.ActivityManager;
import com.android.systemui.navigationbar.RotationButtonController;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.navigationbar.RotationButtonController$TaskStackListenerImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1090x8e6fda0e implements Consumer {
    public final /* synthetic */ RotationButtonController.TaskStackListenerImpl f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C1090x8e6fda0e(RotationButtonController.TaskStackListenerImpl taskStackListenerImpl, int i) {
        this.f$0 = taskStackListenerImpl;
        this.f$1 = i;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onActivityRequestedOrientationChanged$0(this.f$1, (ActivityManager.RunningTaskInfo) obj);
    }
}
