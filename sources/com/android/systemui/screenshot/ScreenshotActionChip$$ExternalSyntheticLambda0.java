package com.android.systemui.screenshot;

import android.app.PendingIntent;
import android.view.View;

public final /* synthetic */ class ScreenshotActionChip$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ PendingIntent f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ ScreenshotActionChip$$ExternalSyntheticLambda0(PendingIntent pendingIntent, Runnable runnable) {
        this.f$0 = pendingIntent;
        this.f$1 = runnable;
    }

    public final void onClick(View view) {
        ScreenshotActionChip.lambda$setPendingIntent$0(this.f$0, this.f$1, view);
    }
}
