package com.android.p011wm.shell.legacysplitscreen;

import android.app.ActivityManager;
import java.util.function.Predicate;

/* renamed from: com.android.wm.shell.legacysplitscreen.WindowManagerProxy$$ExternalSyntheticLambda0 */
public final /* synthetic */ class WindowManagerProxy$$ExternalSyntheticLambda0 implements Predicate {
    public final /* synthetic */ LegacySplitScreenTaskListener f$0;

    public /* synthetic */ WindowManagerProxy$$ExternalSyntheticLambda0(LegacySplitScreenTaskListener legacySplitScreenTaskListener) {
        this.f$0 = legacySplitScreenTaskListener;
    }

    public final boolean test(Object obj) {
        return WindowManagerProxy.lambda$buildDismissSplit$0(this.f$0, (ActivityManager.RunningTaskInfo) obj);
    }
}
