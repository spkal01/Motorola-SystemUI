package com.android.systemui.screenshot;

import android.content.Context;
import android.net.Uri;

public final /* synthetic */ class DeleteScreenshotReceiver$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Context f$0;
    public final /* synthetic */ Uri f$1;

    public /* synthetic */ DeleteScreenshotReceiver$$ExternalSyntheticLambda0(Context context, Uri uri) {
        this.f$0 = context;
        this.f$1 = uri;
    }

    public final void run() {
        this.f$0.getContentResolver().delete(this.f$1, (String) null, (String[]) null);
    }
}
