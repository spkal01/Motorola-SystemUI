package com.motorola.systemui.desktop;

import com.motorola.systemui.desktop.DesktopDisplayRootModulesManager;

/* renamed from: com.motorola.systemui.desktop.DesktopDisplayRootModulesManager$SystemUIReadyForServiceConnection$1$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2742x4ca57d70 implements Runnable {
    public final /* synthetic */ DesktopDisplayRootModulesManager.SystemUIReadyForServiceConnection.C27411 f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C2742x4ca57d70(DesktopDisplayRootModulesManager.SystemUIReadyForServiceConnection.C27411 r1, int i) {
        this.f$0 = r1;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onUnreadNotificationCountChanged$0(this.f$1);
    }
}
