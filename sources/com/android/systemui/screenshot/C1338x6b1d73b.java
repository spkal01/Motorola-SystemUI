package com.android.systemui.screenshot;

import android.content.DialogInterface;
import com.android.systemui.screenshot.MotoGlobalScreenshot;

/* renamed from: com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1338x6b1d73b implements DialogInterface.OnCancelListener {
    public final /* synthetic */ MotoGlobalScreenshot.DisplayScreenshotSession f$0;

    public /* synthetic */ C1338x6b1d73b(MotoGlobalScreenshot.DisplayScreenshotSession displayScreenshotSession) {
        this.f$0 = displayScreenshotSession;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$showDeleteConfirmDialog$18(dialogInterface);
    }
}
