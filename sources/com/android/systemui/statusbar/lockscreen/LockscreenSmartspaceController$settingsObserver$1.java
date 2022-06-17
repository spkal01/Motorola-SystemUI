package com.android.systemui.statusbar.lockscreen;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import org.jetbrains.annotations.Nullable;

/* compiled from: LockscreenSmartspaceController.kt */
public final class LockscreenSmartspaceController$settingsObserver$1 extends ContentObserver {
    final /* synthetic */ LockscreenSmartspaceController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    LockscreenSmartspaceController$settingsObserver$1(LockscreenSmartspaceController lockscreenSmartspaceController, Handler handler) {
        super(handler);
        this.this$0 = lockscreenSmartspaceController;
    }

    public void onChange(boolean z, @Nullable Uri uri) {
        this.this$0.execution.assertIsMainThread();
        this.this$0.reloadSmartspace();
    }
}
