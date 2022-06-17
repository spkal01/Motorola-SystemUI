package com.android.systemui.statusbar.lockscreen;

import android.app.smartspace.SmartspaceSession;
import android.app.smartspace.SmartspaceTarget;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LockscreenSmartspaceController.kt */
final class LockscreenSmartspaceController$sessionListener$1 implements SmartspaceSession.OnTargetsAvailableListener {
    final /* synthetic */ LockscreenSmartspaceController this$0;

    LockscreenSmartspaceController$sessionListener$1(LockscreenSmartspaceController lockscreenSmartspaceController) {
        this.this$0 = lockscreenSmartspaceController;
    }

    public final void onTargetsAvailable(List<SmartspaceTarget> list) {
        this.this$0.execution.assertIsMainThread();
        Intrinsics.checkNotNullExpressionValue(list, "targets");
        LockscreenSmartspaceController lockscreenSmartspaceController = this.this$0;
        ArrayList arrayList = new ArrayList();
        for (T next : list) {
            if (lockscreenSmartspaceController.filterSmartspaceTarget((SmartspaceTarget) next)) {
                arrayList.add(next);
            }
        }
        BcSmartspaceDataPlugin access$getPlugin$p = this.this$0.plugin;
        if (access$getPlugin$p != null) {
            access$getPlugin$p.onTargetsAvailable(arrayList);
        }
    }
}
