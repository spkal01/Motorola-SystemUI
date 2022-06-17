package com.android.systemui.statusbar.lockscreen;

import com.android.systemui.statusbar.policy.ConfigurationController;

/* compiled from: LockscreenSmartspaceController.kt */
public final class LockscreenSmartspaceController$configChangeListener$1 implements ConfigurationController.ConfigurationListener {
    final /* synthetic */ LockscreenSmartspaceController this$0;

    LockscreenSmartspaceController$configChangeListener$1(LockscreenSmartspaceController lockscreenSmartspaceController) {
        this.this$0 = lockscreenSmartspaceController;
    }

    public void onThemeChanged() {
        this.this$0.execution.assertIsMainThread();
        this.this$0.updateTextColorFromWallpaper();
    }
}
