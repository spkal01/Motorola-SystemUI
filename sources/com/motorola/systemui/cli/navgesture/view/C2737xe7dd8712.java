package com.motorola.systemui.cli.navgesture.view;

import com.motorola.systemui.cli.navgesture.view.RecentsViewContainer;

/* renamed from: com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$2$OnScrollCallback$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2737xe7dd8712 implements Runnable {
    public final /* synthetic */ RecentsViewContainer.C27332.OnScrollCallback f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ C2737xe7dd8712(RecentsViewContainer.C27332.OnScrollCallback onScrollCallback, int i) {
        this.f$0 = onScrollCallback;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onScrolled$0(this.f$1);
    }
}
