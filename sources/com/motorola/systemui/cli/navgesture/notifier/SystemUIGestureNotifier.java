package com.motorola.systemui.cli.navgesture.notifier;

import com.motorola.systemui.cli.navgesture.executors.AppExecutors;

public class SystemUIGestureNotifier implements IGestureEndTargetNotifier {
    /* access modifiers changed from: private */
    /* renamed from: notifyGestureTriggered */
    public void lambda$notifyGestureEndTargetChanged$0(int i) {
    }

    public void notifyGestureEndTargetChanged(int i) {
        AppExecutors.background().execute(new SystemUIGestureNotifier$$ExternalSyntheticLambda0(this, i));
    }
}
