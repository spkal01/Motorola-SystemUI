package com.android.systemui.statusbar.lockscreen;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LockscreenSmartspaceController.kt */
public final class LockscreenSmartspaceController$statusBarStateListener$1 implements StatusBarStateController.StateListener {
    final /* synthetic */ LockscreenSmartspaceController this$0;

    LockscreenSmartspaceController$statusBarStateListener$1(LockscreenSmartspaceController lockscreenSmartspaceController) {
        this.this$0 = lockscreenSmartspaceController;
    }

    public void onDozeAmountChanged(float f, float f2) {
        this.this$0.execution.assertIsMainThread();
        BcSmartspaceDataPlugin.SmartspaceView access$getSmartspaceView$p = this.this$0.smartspaceView;
        if (access$getSmartspaceView$p != null) {
            access$getSmartspaceView$p.setDozeAmount(f2);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("smartspaceView");
            throw null;
        }
    }
}
