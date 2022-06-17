package com.android.p011wm.shell.onehanded;

import com.android.p011wm.shell.onehanded.OneHandedController;

/* renamed from: com.android.wm.shell.onehanded.OneHandedController$OneHandedImpl$$ExternalSyntheticLambda5 */
public final /* synthetic */ class OneHandedController$OneHandedImpl$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ OneHandedController.OneHandedImpl f$0;
    public final /* synthetic */ OneHandedTransitionCallback f$1;

    public /* synthetic */ OneHandedController$OneHandedImpl$$ExternalSyntheticLambda5(OneHandedController.OneHandedImpl oneHandedImpl, OneHandedTransitionCallback oneHandedTransitionCallback) {
        this.f$0 = oneHandedImpl;
        this.f$1 = oneHandedTransitionCallback;
    }

    public final void run() {
        this.f$0.lambda$registerTransitionCallback$5(this.f$1);
    }
}
