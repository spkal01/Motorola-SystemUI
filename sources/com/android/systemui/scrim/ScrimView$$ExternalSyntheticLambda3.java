package com.android.systemui.scrim;

import android.graphics.drawable.Drawable;

public final /* synthetic */ class ScrimView$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ScrimView f$0;
    public final /* synthetic */ Drawable f$1;

    public /* synthetic */ ScrimView$$ExternalSyntheticLambda3(ScrimView scrimView, Drawable drawable) {
        this.f$0 = scrimView;
        this.f$1 = drawable;
    }

    public final void run() {
        this.f$0.lambda$setDrawable$1(this.f$1);
    }
}
